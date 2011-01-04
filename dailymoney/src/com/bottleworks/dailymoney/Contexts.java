package com.bottleworks.dailymoney;

import android.os.Bundle;

import com.bottleworks.dailymoney.data.FakeDataProvider;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.User;

/**
 * Helps me to do some quick/cacheable operation in a application life cycle 
 * @author dennis
 *
 */
public class Contexts {

    private static Contexts instance;
    
    private Bundle bundle;
    private User user;
    private IDataProvider dataProvider;
    
    private Contexts(Bundle bundle){
        this.bundle = bundle;
    }
    
    static public Contexts instance(){
        if(instance == null){
            throw new IllegalStateException("context isn't initialized yet.");
        }
        return instance;
    }
    
    /**
     * initial the context no matter where it been started.
     */
    static synchronized void initialContext(Bundle bundle){
        if(instance!=null){
            return;
        }
        instance = new Contexts(bundle);
        instance.initialContext();
    }
    
    
    
    private void initialContext() {
        // TODO Auto-generated method stub
        
        
        resetDataProvider();
    }

    private void resetDataProvider() {
        dataProvider = new FakeDataProvider();
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
        resetDataProvider();
    }
}
