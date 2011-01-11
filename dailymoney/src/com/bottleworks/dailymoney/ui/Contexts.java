package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.Constants;
import com.bottleworks.dailymoney.R;
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
    int pref_maxRecords = -1;//-1 is no limit
    int pref_firstdayWeek = 1;//sunday
    boolean pref_openTestsDesktop = false;
    String pref_workingFolder = "bwDailyMoney";
    boolean pref_exportDatedCSV = true;
    
    private CalendarHelper calendarHelper = new CalendarHelper();
    
    private boolean prefsDirty = true;
    
    public static final boolean DEBUG = true; 
    
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
    
    
    
    public String getApplicationVersionName(){
        if(context instanceof Activity){
            Application app = ((Activity)context).getApplication();
            String name = app.getPackageName();
            PackageInfo pi;
            try {
                pi = app.getPackageManager().getPackageInfo(name,0);
                return pi.versionName;
            } catch (NameNotFoundException e) {
            }
        }
        return "";
    }
    
    public int getApplicationVersionCode(){
        if(context instanceof Activity){
            Application app = ((Activity)context).getApplication();
            String name = app.getPackageName();
            PackageInfo pi;
            try {
                pi = app.getPackageManager().getPackageInfo(name,0);
                return pi.versionCode;
            } catch (NameNotFoundException e) {
            }
        }
        return 0;
    }
    
    private void reloadPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try{
            pref_useImpPovider = prefs.getBoolean(Constants.PREFS_USE_INMENORY_PROVIDER, pref_useImpPovider);
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_detailListLayout = Integer.parseInt(prefs.getString(Constants.PREFS_DETAIL_LIST_LAYOUT, String.valueOf(pref_detailListLayout)));
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_firstdayWeek = Integer.parseInt(prefs.getString(Constants.PREFS_FIRSTDAY_WEEK,  String.valueOf(pref_firstdayWeek)));
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_maxRecords = Integer.parseInt(prefs.getString(Constants.PREFS_MAX_RECORDS,String.valueOf(pref_maxRecords)));
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_openTestsDesktop = prefs.getBoolean(Constants.PREFS_OPEN_TESTS_DESKTOP, false);
        }catch(Exception x){Logger.e(x.getMessage());}
        
        try{
            pref_workingFolder = prefs.getString(Constants.PREFS_WORKING_FOLDER, pref_workingFolder);
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_exportDatedCSV = prefs.getBoolean(Constants.PREFS_EXPORT_DATED_CSV, pref_exportDatedCSV);
        }catch(Exception x){Logger.e(x.getMessage());}
        if(DEBUG){
            Logger.d("preference : use inmemory "+pref_useImpPovider);
            Logger.d("preference : detail layout "+pref_detailListLayout);
            Logger.d("preference : firstday of week "+pref_firstdayWeek);
            Logger.d("preference : max records "+pref_maxRecords);
            Logger.d("preference : open tests desktop "+pref_openTestsDesktop);
            Logger.d("preference : open working_folder"+pref_workingFolder);
            Logger.d("preference : open export dated csv"+pref_exportDatedCSV);
        }
        calendarHelper.setFirstDayOfWeek(pref_firstdayWeek);
    }
    
    public String getPrefWorkingFolder(){
        return pref_workingFolder;
    }
    
    public boolean isPrefExportDatedCSV(){
        return pref_exportDatedCSV;
    }
    
    public int getPrefDetailListLayout(){
        return pref_detailListLayout;
    }
    
    public int getPrefMaxRecords(){
        return pref_maxRecords;
    }
    
    public int getPrefFirstdayWeek(){
        return pref_firstdayWeek;
    }
    
    public boolean isPrefOpenTestsDesktop(){
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
            dataProvider = new SQLiteDataProvider(new SQLiteHelper(context,"dm.db"),calendarHelper);
        }
        
        dataProvider.init();
        if(DEBUG){
            Logger.d("initDataProvider :"+dataProvider);
        }
    }
    public void cleanDataProvider(Context context){
        if(dataProvider!=null){
            if(DEBUG){
                Logger.d("cleanDataProvider :"+dataProvider);
            }
            dataProvider.destroyed();
            dataProvider = null;
        }
    }
    
    public int getOrientation(){
        if(context==null){
            return Configuration.ORIENTATION_UNDEFINED;
        }
        return context.getResources().getConfiguration().orientation;
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
