package com.bottleworks.commons.ui;

import android.content.Context;
import android.os.Bundle;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.FakeDataProvider;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.User;

/**
 * Helps me to do some quick/cacheable operation in a application's life cycle 
 * @author dennis
 *
 */
public class Contexts {

    private static Contexts instance;
    
    private Bundle bundle;
    private Context context;
    
    private User user;
    private IDataProvider dataProvider;
    private I18N i18n;
    
    private boolean bundleInit = false; 
    
    private Contexts(){
    }
    
    static public Contexts instance(){
        if(instance == null){
            synchronized(Contexts.class){
                if(instance==null){
                    instance = new Contexts();
                }
            }
        }
        return instance;
    }
    
    Contexts initialBundle(Context context,Bundle bundle){
        initContext(context);
        if(!bundleInit){
            this.bundle = bundle;
            reloadDataProvider();
            bundleInit = true;
        }
        return this;
    }
    
    
    Contexts initContext(Context context){
        if(this.context != context){
            this.context = context;
            this.i18n = new I18N(context);
        }
        return this;
    }
    
    Contexts cleanContext(Context context){
        if(this.context == context){
            this.context = null;
            this.i18n = null;
        }
        return this;
    }
    
    
    public I18N getI18n() {
        return i18n;
    }

    private void reloadDataProvider() {
        dataProvider = new FakeDataProvider();
        Logger.d("reloadDataProvider :"+dataProvider);
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
        reloadDataProvider();
    }
}
