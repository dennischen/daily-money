package com.bottleworks.dailymoney.context;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
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

    private static Contexts uiContexts;
//    private static Contexts serviceContexts;
    
    private Context context;
    
    private IDataProvider dataProvider;
    private I18N i18n;
    
    boolean pref_useImpPovider = false;
    int pref_detailListLayout = 2;
    int pref_maxRecords = -1;//-1 is no limit
    int pref_firstdayWeek = 1;//sunday
    boolean pref_openTestsDesktop = false;
    String pref_workingFolder = "bwDailyMoney";
    boolean pref_backupCSV = true;
    
    private CalendarHelper calendarHelper = new CalendarHelper();
    
    private boolean prefsDirty = true;
    
    public static final boolean DEBUG = true; 
    
    private Contexts(){
    }
    
    /** get a Contexts instance for activity use **/
    static public Contexts uiInstance(){
        if(uiContexts == null){
            synchronized(Contexts.class){
                if(uiContexts==null){
                    uiContexts = new Contexts();
                }
            }
        }
        return uiContexts;
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
    
    public boolean shareHtmlContent(String subject,String html){
        return shareHtmlContent(subject,html,null);
    }
    public boolean shareHtmlContent(String subject,String html,List<File> attachments){
        return shareContent(subject,html,true,attachments);
    }
    
    
    public boolean shareTextContent(String subject,String text){
        return shareTextContent(subject,text,null);
    }
    public boolean shareTextContent(String subject,String text,List<File> attachments){
        return shareContent(subject,text,false,attachments);
    }

    
    public boolean shareContent(String subject,String content,boolean htmlContent,List<File> attachments){
        if(!(context instanceof Activity)){
            return false;
        }
        
        Intent intent;
        if(attachments == null || attachments.size()<=1){
            intent = new Intent(Intent.ACTION_SEND);
        }else{
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        }
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if(htmlContent){
            intent.setType("text/html");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(content));
        }else{
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        }
        
        ArrayList<Parcelable> parcels = new ArrayList<Parcelable>();
        if (attachments != null) {
            for (File f : attachments) {
                parcels.add(Uri.fromFile(f));
            }
        }

        if(parcels.size()==1){
            intent.putExtra(Intent.EXTRA_STREAM, parcels.get(0));
        }else if(parcels.size()>1){
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, parcels);
        }
        try{
            ((Activity)context).startActivity(Intent.createChooser(intent, i18n.string(R.string.clabel_share)));
        }catch(Exception x){
            Logger.e(x.getMessage(),x);
            return false;
        }
        return true;
    }
    
    
    
    /**
     * return true is this is first time you call this api in this application
     */
    public boolean isFirstTime(){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if(!prefs.contains("app_firsttime")){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("app_firsttime",Formats.normalizeDate2String(new Date()));
                editor.commit();
                return true;
            }
        }catch(Exception x){}
        return false;
    }
    
    /**
     * return true is this is first time you call this api in this application and current version
     */
    public boolean isFirstVersionTime(){
        int curr = getApplicationVersionCode();
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int last = prefs.getInt("app_lastver",-1);
            if(curr!=last){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("app_lastver",curr);
                editor.commit();
                return true;
            }
        }catch(Exception x){}
        return false;
    }
    
    /**
     * for ui context only
     * @return
     */
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
    
    /**
     * for ui context only
     * @return
     */
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
            pref_backupCSV = prefs.getBoolean(Constants.PREFS_BACKUP_CSV, pref_backupCSV);
        }catch(Exception x){Logger.e(x.getMessage());}
        if(DEBUG){
            Logger.d("preference : use inmemory "+pref_useImpPovider);
            Logger.d("preference : detail layout "+pref_detailListLayout);
            Logger.d("preference : firstday of week "+pref_firstdayWeek);
            Logger.d("preference : max records "+pref_maxRecords);
            Logger.d("preference : open tests desktop "+pref_openTestsDesktop);
            Logger.d("preference : open working_folder"+pref_workingFolder);
            Logger.d("preference : backup csv"+pref_backupCSV);
        }
        calendarHelper.setFirstDayOfWeek(pref_firstdayWeek);
    }
    
    public String getPrefWorkingFolder(){
        return pref_workingFolder;
    }
    
    public boolean isPrefBackupCSV(){
        return pref_backupCSV;
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
