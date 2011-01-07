package com.bottleworks.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    
    static public void alert(Context context,String title,String msg,String btn,int icon){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        
        alertDialog.setButton(btn, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           }
        });
        if(icon!=NO_ICON_RES){
            alertDialog.setIcon(icon);
        }
        alertDialog.show();
    }
    
    static public void alert(Context context,String msg){
        alert(context,context.getString(R.string.clabel_alert),msg,context.getString(R.string.cact_ok),NO_ICON_RES);
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
    
    static public View inflateView(Context context,ViewGroup parent, int resourceid){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resourceid, parent);
    }
    
    
    private static ExecutorService busyExecutor = Executors.newSingleThreadExecutor();
    private static Handler busyGuiHandler = new Handler();
    
    static public void doBusy(Context context,Runnable r){
        doBusy(context,Contexts.instance().getI18n().string(R.string.cmsg_busy),r); 
    }
    
    static public void doBusy(Context context,String msg,Runnable r){
        final ProgressDialog dlg = ProgressDialog.show(context,Contexts.instance().getI18n().string(R.string.clabel_busy),msg,true,false);
        dlg.show();
        busyExecutor.submit(new BusyRunnable(dlg,r));
    }
    
    static class BusyRunnable implements Runnable{
        ProgressDialog dlg;
        Runnable run;
        public BusyRunnable(ProgressDialog dlg,Runnable run){
            this.dlg = dlg;
            this.run = run;
        }
        
        @Override
        public void run() {
            try{
                run.run();
                dlg.dismiss();
                if(run instanceof IBusyListener){
                    busyGuiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            ((IBusyListener)run).onBusyFinish();                        
                        }});
                }
            }catch(final Throwable x){
                Logger.e(x.getMessage(),x);
                if(run instanceof IBusyListener){
                    busyGuiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            ((IBusyListener)run).onBusyError(x);                        
                        }});
                }
            }
            //dlg is safe to be dismissed in other thread.
            
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

    public static void openDatePicker(Context context, Date d,final OnDialogFinishListener listener) {
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
                listener.onDialogFinish(s[0] , view, c.getTime());
            }}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        s[0] = picker;
        picker.show();
    }
}
