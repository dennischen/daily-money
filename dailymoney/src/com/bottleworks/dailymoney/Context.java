package com.bottleworks.dailymoney;

import android.app.Activity;

import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.User;

/**
 * @author dennis
 *
 */
public class Context {

    static Context instance;
    
    Activity activity;
    User user;
    IDataProvider dataProvider;
    
    
    private Context(Activity activity){
        this.activity = activity;
    }
    
    static public Context instance(){
        if(instance == null){
            throw new IllegalStateException("context doesn't be initialized yet.");
        }
        return instance;
    }
    
    static synchronized void initialContext(Activity activity){
        if(instance==null){
            return;
        }
        instance = new Context(activity);
    }
    
    
    
    public User getUser(){
        return user;
    }
    
    public IDataProvider getDataProvider(){
        return dataProvider;
    }
    
    public void switchUser(User user){
        if(this.user!=null && this.user.equals(user)){
            return;
        }
    }
}
