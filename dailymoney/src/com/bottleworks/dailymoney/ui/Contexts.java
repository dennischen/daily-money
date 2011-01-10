package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.bottleworks.commons.util.CalendarHelper;
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
    
    boolean pref_useImpPovider = false;
    int pref_detailListLayout = 2;
    int pref_maxRecords = 100;//-1 is no limit
    int pref_firstdayWeek = 1;//sunday
    boolean pref_openTestsDesktop = false;
    String pref_workingFolder = "bwDailyMoney";
    
    private CalendarHelper calendarHelper = new CalendarHelper();
    
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
        try{
            pref_useImpPovider = prefs.getBoolean(Constants.PREFS_USE_INMENORY_PROVIDER, false);
        }catch(Exception x){}
        try{
            pref_detailListLayout = Integer.parseInt(prefs.getString(Constants.PREFS_DETAIL_LIST_LAYOUT, "2"));
        }catch(Exception x){}
        try{
            pref_firstdayWeek = Integer.parseInt(prefs.getString(Constants.PREFS_FIRSTDAY_WEEK, "1"));
        }catch(Exception x){}
        try{
            pref_maxRecords = Integer.parseInt(prefs.getString(Constants.PREFS_MAX_RECORDS, "100"));
        }catch(Exception x){}
        
        
        try{
            pref_openTestsDesktop = prefs.getBoolean(Constants.PREFS_OPEN_TESTS_DESKTOP, false);
        }catch(Exception x){}
        
        Logger.d("preference : use inmemory "+pref_useImpPovider);
        Logger.d("preference : detail layout "+pref_detailListLayout);
        Logger.d("preference : firstday of week "+pref_firstdayWeek);
        Logger.d("preference : max records "+pref_maxRecords);
        Logger.d("preference : open tests desktop "+pref_openTestsDesktop);
        
        calendarHelper.setFirstDayOfWeek(pref_firstdayWeek);
    }
    
    public String getWorkingFolder(){
        return pref_workingFolder;
    }
    
    
    public int getPrefDetailListLayout(){
        return pref_detailListLayout;
    }
    
    public int getPrefMaxRecords(){
        return pref_maxRecords;
    }
    
    public int getFirstdayWeek(){
        return pref_firstdayWeek;
    }
    
    public boolean isOpenTestsDesktop(){
        return pref_openTestsDesktop;
    }
    
    public CalendarHelper getCalendarHelper(){
        return calendarHelper;
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
        if(pref_useImpPovider){
            dataProvider = new InMemoryDataProvider();
        }else{
//            dataProvider = new InMemoryDataProvider();
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
    
    public DateFormat getLongDateFormat(){
        return android.text.format.DateFormat.getLongDateFormat(context);
    }
    
    public DateFormat getMediumDateFormat(){
        return android.text.format.DateFormat.getMediumDateFormat(context);
    }
    
    public DateFormat getTimeFormat(){
        return android.text.format.DateFormat.getTimeFormat(context);
    }
    public Drawable getDrawable(int id){
        return context.getResources().getDrawable(id);
    }
}
