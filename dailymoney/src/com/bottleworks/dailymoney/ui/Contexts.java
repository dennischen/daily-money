package com.bottleworks.dailymoney.ui;

import android.content.Context;
import android.os.Bundle;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.InMemoryDataProvider;
import com.bottleworks.dailymoney.data.SQLiteDataProvider;
import com.bottleworks.dailymoney.data.SQLiteHelper;

/**
 * Helps me to do some quick/cacheable operation in a application's life cycle 
 * @author dennis
 *
 */
public class Contexts {

    private static Contexts instance;
    
    private Bundle bundle;
    private Context context;
    
    private IDataProvider dataProvider;
    private I18N i18n;
    
    boolean inmemory = false;//true;
    
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
        if(!bundleInit){
            this.bundle = bundle;
//            inmemory = bundle.getBoolean(Preferences.IN_MENORY_PROVIDER, true);
            
            
            bundleInit = true;
        }
        initContext(context);
        return this;
    }
    
    
    Contexts initContext(Context context){
        if(this.context != context){
            this.context = context;
            this.i18n = new I18N(context);
            initDataProvider(context);
        }
        return this;
    }
    
    Contexts cleanContext(Context context){
        if(this.context == context){
            this.context = null;
            this.i18n = null;
            cleanDataProvider(context);
        }
        return this;
    }
    
    
    public I18N getI18n() {
        return i18n;
    }

    private void initDataProvider(Context context) {
        if(inmemory){
            dataProvider = new InMemoryDataProvider();
        }else{
            dataProvider = new SQLiteDataProvider(new SQLiteHelper(context,"dm.db"));
        }
        
        dataProvider.init();
        Logger.d("initDataProvider :"+dataProvider);
    }
    public void cleanDataProvider(Context context){
        if(dataProvider!=null){
            Logger.d("cleanDataProvider :"+dataProvider);
            dataProvider.destroyed();
            dataProvider = null;
        }
    }
    
    public IDataProvider getDataProvider(){
        return dataProvider;
    }
}
