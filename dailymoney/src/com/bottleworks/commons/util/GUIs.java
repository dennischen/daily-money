package com.bottleworks.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bottleworks.dailymoney.DetailEditorDialog;
import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.ui.Contexts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * 
 * @author dennis
 *
 */
public class GUIs {

    
    public static final int NO_ICON_RES=0x0;
    
    public static final int OK_BUTTON = AlertDialog.BUTTON_POSITIVE;
    public static final int CANCEL_BUTTON = AlertDialog.BUTTON_NEGATIVE;
    
    static public void alert(Context context,String title,String msg,String oktext,int icon){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if(title!=null){
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(msg);
        
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,oktext, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           }
        });
        if(icon!=NO_ICON_RES){
            alertDialog.setIcon(icon);
        }
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    
    static public void alert(Context context,String msg){
        alert(context,null,msg,context.getString(R.string.cact_ok),NO_ICON_RES);
    }
    
    static public void alert(Context context,int msg){
        alert(context,null,context.getString(msg),context.getString(R.string.cact_ok),NO_ICON_RES);
    }
    
    static public void confirm(Context context,int msg,OnFinishListener listener){
        confirm(context,null,context.getString(msg),context.getString(R.string.cact_ok),context.getString(R.string.cact_cancel),NO_ICON_RES,listener);
    }
    
    static public void confirm(Context context,String msg,OnFinishListener listener){
        confirm(context,null,msg,context.getString(R.string.cact_ok),context.getString(R.string.cact_cancel),NO_ICON_RES,listener);
    }
    
    static public void confirm(Context context,String title, String msg, String oktext,String canceltext,int icon,final OnFinishListener listener){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if(title!=null){
            alertDialog.setTitle(title);
        }
        alertDialog.setMessage(msg);
        
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onFinish(which);
            }
         };
        
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,oktext,l);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,canceltext,l);
        if(icon!=NO_ICON_RES){
            alertDialog.setIcon(icon);
        }
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
    
    static public void shortToast(Context context,String msg){
        toast(context,msg,Toast.LENGTH_SHORT);
    }
    
    static public void shortToast(Context context,int res){
        Toast.makeText(context,res,Toast.LENGTH_SHORT).show();
    }
    
    static public void longToast(Context context,String msg){
        toast(context,msg,Toast.LENGTH_LONG);
    }
    
    static public void longToast(Context context,int res){
        Toast.makeText(context,res,Toast.LENGTH_LONG).show();
    }
    
    static public void toast(Context context,String msg,int length){
        Toast.makeText(context,msg,length).show();
    }
    
    static public void toast(Context context,int res,int length){
        Toast.makeText(context,res,length).show();
    }
    
    static public void errorToast(Context context,Throwable e){
        shortToast(context,Contexts.instance().getI18n().string(R.string.cmsg_error,e.getMessage()));
    }
    
    static public void error(Context context,Throwable e){
        alert(context,Contexts.instance().getI18n().string(R.string.cmsg_error,e.getMessage()));
    }
    
    static public View inflateView(Context context,ViewGroup parent, int resourceid){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resourceid, parent);
    }
    
    
    private static ExecutorService busyExecutor = Executors.newSingleThreadExecutor();
    private static Handler guiHandler = new Handler();
    
    
    static public void postResume(final Runnable r){
        busyExecutor.submit(new Runnable(){
            @Override
            public void run() {
                post(r);
            }});
    }
    
    static public void post(Runnable r){
        guiHandler.post(r);
    }
    
    static public void doBusy(Context context,IBusyListener r){
        doBusy(context,(Runnable)r);
    }
    
    static public void doBusy(Context context,String msg,IBusyListener r){
        doBusy(context,(Runnable)r);
    }
    static public void doBusy(Context context,Runnable r){
        doBusy(context,Contexts.instance().getI18n().string(R.string.cmsg_busy),r); 
    }
    
    static public void doBusy(Context context,String msg,Runnable r){
        final ProgressDialog dlg = ProgressDialog.show(context,Contexts.instance().getI18n().string(R.string.clabel_busy),msg,true,false);
        final BusyRunnable br = new BusyRunnable(dlg,r);
        busyExecutor.submit(br);
        
        guiHandler.post(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                synchronized(br){
                    if(!br.finish){
                        dlg.show();
                    }
                }
            }
        });

        
    }
    
    static class BusyRunnable implements Runnable{
        ProgressDialog dlg;
        Runnable run;
        boolean finish = false;
        public BusyRunnable(ProgressDialog dlg,Runnable run){
            this.dlg = dlg;
            this.run = run;
        }
        
        @Override
        public void run() {
            try{
                run.run();
                synchronized(this){
                    //dlg is safe to be dismissed in other thread.
                    if(dlg.isShowing()){
                        dlg.dismiss();
                    }
                    finish = true;
                }
                if(run instanceof IBusyListener){
                    guiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            ((IBusyListener)run).onBusyFinish();                        
                        }});
                }
            }catch(final Throwable x){
                Logger.e(x.getMessage(),x);
                synchronized(this){
                    if(dlg.isShowing()){
                        dlg.dismiss();
                    }
                    finish = true;
                }
                if(run instanceof IBusyListener){
                    guiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            ((IBusyListener)run).onBusyError(x);                        
                        }});
                }
            }
        }
    }

    /**
     * on busy event will be invoke in gui thread.
     */
    public static interface IBusyListener extends Runnable{
        void onBusyFinish();
        void onBusyError(Throwable t);
    }
    
    public static abstract class BusyAdapter implements IBusyListener{
        @Override
        public void onBusyFinish() {
        }
        @Override
        public void onBusyError(Throwable t) {
        }
    }

    public static void openDatePicker(Context context, Date d,final OnFinishListener listener) {
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        //for event
        final DatePickerDialog[] s = new DatePickerDialog[1];
        DatePickerDialog picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,monthOfYear);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                listener.onFinish(c.getTime());
            }}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        s[0] = picker;
        picker.show();
    }
    
    public static interface OnFinishListener {   
        public boolean onFinish(Object data);
    }
}
