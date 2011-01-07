package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.Constants;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.InMemoryDataProvider;
import com.bottleworks.dailymoney.data.SQLiteDataProvider;
import com.bottleworks.dailymoney.data.SQLiteHelper;

/**
 * Helps me to do some quick access in context/ui thread
 * @author dennis
 *
 */
public class Contexts {

    private static Contexts instance;
    
    private Context context;
    
    private IDataProvider dataProvider;
    private I18N i18n;
    
    boolean useimprovider;
    
    private boolean prefsDirty = true;
    
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


    
    Contexts initContext(Context context){
        if(this.context != context){
            this.context = context;
            this.i18n = new I18N(context);
            if(prefsDirty){
                reloadPreference();
                prefsDirty = false;
            }
            initDataProvider(context);
        }
        return this;
    }
    
    private void reloadPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        useimprovider = prefs.getBoolean(Constants.PREFS_USE_INMENORY_PROVIDER, false);
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
        if(useimprovider){
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

    public void setPreferenceDirty() {
        prefsDirty = true;
    }
    
    public DateFormat getDateFormat(){
        return android.text.format.DateFormat.getDateFormat(context);
    }
    
    public Drawable getDrawable(int id){
        return context.getResources().getDrawable(id);
    }
}
