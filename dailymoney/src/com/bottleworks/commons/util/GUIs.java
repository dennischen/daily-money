package com.bottleworks.commons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bottleworks.dailymoney.core.R;

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
        shortToast(context,context.getString(R.string.cmsg_error,e.getMessage()));
    }
    
    static public void error(Context context,Throwable e){
        alert(context,context.getString(R.string.cmsg_error,e.getMessage()));
    }
    
    static public View inflateView(Context context,ViewGroup parent, int resourceid){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resourceid, parent);
    }
    
    private static ScheduledExecutorService delayPostExecutor = Executors.newSingleThreadScheduledExecutor();
    private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
    private static Handler guiHandler = new Handler();
    
    static public void delayPost(final Runnable r){
        delayPost(r,50);
    }
    static public void delayPost(final Runnable r,final long delay){
        delayPostExecutor.schedule(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {}
                post(r);
            }},delay,TimeUnit.MILLISECONDS);
    }
    
    static public void post(Runnable r){
        guiHandler.post(new NothrowRunnable(r));
    }
    
    static public void doBusy(Context context,IBusyRunnable r){
        doBusy(context,(Runnable)r);
    }
    
    static public void doBusy(Context context,String msg,IBusyRunnable r){
        doBusy(context,(Runnable)r);
    }
    static public void doBusy(Context context,Runnable r){
        doBusy(context,context.getString(R.string.cmsg_busy),r); 
    }
    
    //lock & release rotation!! not work in sdk(2.1,2.2) but work fine in my i9000
    static public void lockOrientation(Activity activity){
        switch (activity.getResources().getConfiguration().orientation) {
        case Configuration.ORIENTATION_PORTRAIT:
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
        case Configuration.ORIENTATION_LANDSCAPE:
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        }
    }
    static public void releaseOrientation(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
    static public void doBusy(Context context,String msg,Runnable r){
        doBusy(context,msg,r,500);
    }
    static public void doBusy(Context context,String msg,Runnable r,final long dealy){
        final ProgressDialog dlg = new ProgressDialog(context);//ProgressDialog.show(context,null,msg,true,false);
        dlg.setMessage(msg);
        dlg.setTitle(null);
        dlg.setIndeterminate(true);
        dlg.setCancelable(false);
        
        if (context instanceof Activity) {
            lockOrientation((Activity)context);  
        }
        
        final BusyRunnable br = new BusyRunnable(context,dlg,r);
        singleExecutor.submit(br);
        
        delayPost(new Runnable(){
            @Override
            public void run() {
                synchronized(br){
                    if(!br.finish){
                        dlg.show();
                        br.showing = true;
                    }
                }
            }
        },dealy); 
    }
    
    static private class NothrowRunnable implements Runnable{
        Runnable r;
        public NothrowRunnable(Runnable r){
            this.r = r;
        }
        @Override
        public void run() {
            try{
                r.run();
            }catch(Exception x){
                Logger.e(x.getMessage(),x);
            }
        }
    }
    
    static private class BusyRunnable implements Runnable{
        ProgressDialog dlg;
        Context context;
        Runnable run;
        volatile boolean showing = false;
        volatile boolean finish = false;
        
        public BusyRunnable(Context context,ProgressDialog dlg,Runnable run){
            this.context = context;
            this.dlg = dlg;
            this.run = run;
        }
        
        @Override
        public void run() {
            final FinalVar<Throwable> x = new FinalVar<Throwable>();
            try{
                run.run();
            }catch(final Throwable x0){
                x.value = x0;
                Logger.e(x0.getMessage(),x0);
            }
            
            //close dlg if it is showing
            synchronized (this) {
                finish = true;
                if (showing) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showing = false;
                            if (dlg.isShowing()) {
                                dlg.dismiss();
                            }
                        }
                    });
                }
            }
            
            //notify success of error
            if (run instanceof IBusyRunnable) {
                if (x.value == null) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ((IBusyRunnable) run).onBusyFinish();
                        }
                    });
                } else {
                    if (run instanceof IBusyRunnable) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                ((IBusyRunnable) run).onBusyError(x.value);
                            }
                        });
                    }
                }
            }
            //release orientation lock
            post(new Runnable() {
                @Override
                public void run() {
                    if (context instanceof Activity) {
                        releaseOrientation((Activity) context);
                    }
                }
            });
        }
    }

    /**
     * on busy event will be invoked in gui thread.
     */
    public static interface IBusyRunnable extends Runnable{
        void onBusyFinish();
        void onBusyError(Throwable t);
    }
    
    public static abstract class BusyAdapter implements IBusyRunnable{
        @Override
        public void onBusyFinish() {
        }
        @Override
        public void onBusyError(Throwable t) {
            Logger.e(t.getMessage(),t);
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
    
    public static int converDP2Pixel(Context context,float dp){
        return (int)(dp*getDPRatio(context)+0.5F);
    }

    public static float getDPRatio(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
    
    public static int getOrientation(Activity activity){
        return activity.getResources().getConfiguration().orientation;
    }
    
    public static boolean isPortrait(Activity activity){
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    public static boolean isLandscape(Activity activity){
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
