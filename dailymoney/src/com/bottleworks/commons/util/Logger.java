package com.bottleworks.commons.util;

import android.util.Log;

/**
 * 
 * @author dennis
 *
 */
public class Logger {

    static String LOG_TAG = "daily-money";
    
    static public void d(String msg){
        Log.d(LOG_TAG, msg);
    }
    
    static public void d(String msg,Throwable t){
        Log.d(LOG_TAG, msg,t);
    }
    
    static public void w(String msg){
        Log.w(LOG_TAG, msg);
    }
    
    static public void w(String msg,Throwable t){
        Log.w(LOG_TAG, msg,t);
    }
    
    static public void e(String msg){
        Log.e(LOG_TAG, msg);
    }
    
    static public void e(String msg,Throwable t){
        Log.e(LOG_TAG, msg,t);
    }
    
    static public void i(String msg){
        Log.i(LOG_TAG,msg);
    }
    
    static public void i(String msg,Throwable t){
        Log.i(LOG_TAG,msg,t);
    }
}
