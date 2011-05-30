package com.bottleworks.dailymoney.context;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Book;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.data.IMasterDataProvider;
import com.bottleworks.dailymoney.data.SQLiteDataProvider;
import com.bottleworks.dailymoney.data.SQLiteDataHelper;
import com.bottleworks.dailymoney.data.SQLiteMasterDataHelper;
import com.bottleworks.dailymoney.data.SQLiteMasterDataProvider;
import com.bottleworks.dailymoney.data.SymbolPosition;
import com.bottleworks.dailymoney.ui.Constants;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * Helps me to do some quick access in context/ui thread
 * @author dennis
 *
 */
public class Contexts {

    private static Contexts instance;
    
    private Object appInitialObject;
    private Context appContext;
    private Activity uiActivity;
    
    private IDataProvider dataProvider;
    private IMasterDataProvider masterDataProvider;
    private I18N i18n;
    
    int pref_workingBookId = 0;//the book user selected
    int pref_detailListLayout = 2;
    int pref_maxRecords = -1;//-1 is no limit
    int pref_firstdayWeek = 1;//sunday
    int pref_startdayMonth = 1;//
    boolean pref_openTestsDesktop = false;
    String pref_workingFolder = "bwDailyMoney";
    boolean pref_backupCSV = true;
    String pref_password = "";
    boolean pref_allowAnalytics = true;
    String pref_csvEncoding = "UTF8";
    
    private CalendarHelper calendarHelper = new CalendarHelper();
    
    private static final ExecutorService trackSingleExecutor = Executors.newSingleThreadExecutor();
    
    //analytics code
    private static final String ANALYTICS_CDOE = "UA-20850113-1";
    private static final int ANALYTICS_DISPATH_DELAY = 60;// dispatch queue at least 60s
    
    private GoogleAnalyticsTracker tracker;
    
    private String currencySymbol = "$";
    
    private boolean prefsDirty = true;
    
    public static final boolean DEBUG = true; 
    
    private Contexts(){
    }
    
    /** get a Contexts instance for activity use **/
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

    
    boolean initActivity(Activity activity){
        if(appContext==null){
            initApplication(activity,activity);
        }
        if(this.uiActivity != activity){
            Logger.d(">>>initial activity "+activity);
            this.uiActivity = activity;
            if(prefsDirty){
                reloadPreference();
                prefsDirty = false;
            }
            initMasterDataProvider(uiActivity);
            initDataProvider(uiActivity);
            return true;
        }
        return false;
    }
    
    boolean cleanActivity(Activity activity){
        if(this.uiActivity == activity){
            this.uiActivity = null;
            cleanDataProvider(uiActivity);
            cleanMasterDataProvider(uiActivity);
            Logger.d(">>>cleanup activity "+activity);
            return true;
        }
        return false;
    }
    
    synchronized boolean initApplication(Object appInitialObject,Context context){
        if(appContext==null){
            Logger.d(">>initialial application context with:"+appInitialObject);
            this.appInitialObject = appInitialObject;
            appContext = context.getApplicationContext();
            this.i18n = new I18N(appContext);
            initTracker(appContext);
            return true;
        }else{
            Logger.w("application context was initialized :"+appInitialObject);
        }
        return false;
    }
    
    synchronized boolean destroyApplication(Object appInitialObject){
        if(this.appInitialObject!=null && this.appInitialObject.equals(appInitialObject)){
            cleanTracker();
            Logger.d(">>destroyed application context :"+appInitialObject);
            appContext = null;
            appInitialObject = null;
            return true;
        }
        return false;
    }
    
    private void initTracker(final Context context) {
        if (isPrefAllowAnalytics()) {
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        Logger.d("initial google tracker");
                        tracker = GoogleAnalyticsTracker.getInstance();
                        tracker.setProductVersion(i18n.string(R.string.app_surface), getApplicationVersionName());
                        tracker.start(ANALYTICS_CDOE, ANALYTICS_DISPATH_DELAY, context);
                        
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });
        }
    }

    
    private void cleanTracker() {
     // Stop the tracker when it is no longer needed.
        try {
            if (tracker != null) {
                //don't dispatch, let the queue do it next time to reduce network
                tracker.dispatch();
                tracker.stop();
                tracker = null;
                Logger.d("clean google tracker");
            }
        } catch (Throwable t) {
            Logger.e(t.getMessage(), t);
        }
    }
    
    protected void trackEvent(final String category,final String action,final String label,final int value) {
        if (tracker != null) {
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        if (tracker != null) {
                            tracker.trackEvent(category, action, label, value);
                        }
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });
        }
    }

    protected void trackPageView(final String path) {
        if (tracker != null) {
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        if (tracker != null) {
                            Logger.d("track "+path);
                            tracker.trackPageView(path);
                        }
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });
        }
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
    
    public String getCurrencySymbol(){
        return currencySymbol;
    }

    
    public boolean shareContent(String subject,String content,boolean htmlContent,List<File> attachments){
        if(uiActivity == null){
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
            uiActivity.startActivity(Intent.createChooser(intent, i18n.string(R.string.clabel_share)));
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
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
        if(uiActivity!=null){
            Application app = (uiActivity).getApplication();
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
        if(uiActivity!=null){
            Application app = (uiActivity).getApplication();
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        
        try{
            pref_workingBookId = Integer.parseInt(prefs.getString(Constants.PREFS_WORKING_BOOK_ID, String.valueOf(pref_workingBookId)));
        }catch(Exception x){Logger.e(x.getMessage());}
        if(pref_workingBookId<0){
            pref_workingBookId = 0;
        }
        
        try{
            String pd1  = prefs.getString(Constants.PREFS_PASSWORD, pref_password);
            String pd2  = prefs.getString(Constants.PREFS_PASSWORDVD, pref_password);
            if(pd1.equals(pd2)){
                pref_password = pd1;
            }else{
                pref_password = "";
            }
        }catch(Exception x){Logger.e(x.getMessage());}
        
        
        try{
            pref_detailListLayout = Integer.parseInt(prefs.getString(Constants.PREFS_DETAIL_LIST_LAYOUT, String.valueOf(pref_detailListLayout)));
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_firstdayWeek = Integer.parseInt(prefs.getString(Constants.PREFS_FIRSTDAY_WEEK,  String.valueOf(pref_firstdayWeek)));
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_startdayMonth = Integer.parseInt(prefs.getString(Constants.PREFS_STARTDAY_MONTH,  String.valueOf(pref_startdayMonth)));
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
        try{
            pref_allowAnalytics = prefs.getBoolean(Constants.PREFS_ALLOW_ANALYTICS, pref_allowAnalytics);
        }catch(Exception x){Logger.e(x.getMessage());}
        try{
            pref_csvEncoding = prefs.getString(Constants.PREFS_CSV_ENCODING, pref_csvEncoding);
        }catch(Exception x){Logger.e(x.getMessage());}
        
        
        if(DEBUG){
            Logger.d("preference : working book "+pref_workingBookId);
            Logger.d("preference : detail layout "+pref_detailListLayout);
            Logger.d("preference : firstday of week "+pref_firstdayWeek);
            Logger.d("preference : startday of month "+pref_startdayMonth);
            Logger.d("preference : max records "+pref_maxRecords);
            Logger.d("preference : open tests desktop "+pref_openTestsDesktop);
            Logger.d("preference : open working_folder "+pref_workingFolder);
            Logger.d("preference : backup csv "+pref_backupCSV);
            Logger.d("preference : csv encoding "+pref_csvEncoding);
        }
        calendarHelper.setFirstDayOfWeek(getPrefFirstdayWeek());
        calendarHelper.setStartDayOfMonth(getPrefStartdayMonth());
    }
    
    public int getWorkingBookId(){
        return pref_workingBookId;
    }
    
    public void setWorkingBookId(int id){
        if(id<0){
            id = 0;
        }
        pref_workingBookId = id;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.PREFS_WORKING_BOOK_ID,id);
        editor.commit();
    }
    
    public String getPrefPassword(){
        return pref_password;
    }
    
    public boolean isPrefAllowAnalytics(){
        return pref_allowAnalytics;
    }
    
    public String getPrefCSVEncoding(){
        return pref_csvEncoding;
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
    public int getPrefStartdayMonth(){
        return pref_startdayMonth>28?28:(pref_startdayMonth<1?1:pref_startdayMonth);
    }
    
    public boolean isPrefOpenTestsDesktop(){
        return pref_openTestsDesktop;
    }
    
    public CalendarHelper getCalendarHelper(){
        return calendarHelper;
    }


    public I18N getI18n() {
        return i18n;
    }

    private void initDataProvider(Context context) {
        String dbname = "dm.db";
        if(pref_workingBookId>0){
            dbname = "dm_"+pref_workingBookId+".db";
        }
        dataProvider = new SQLiteDataProvider(new SQLiteDataHelper(context,dbname),calendarHelper);
        dataProvider.init();
        if(DEBUG){
            Logger.d("initDataProvider :"+dataProvider);
        }
    }
    
    /** to reset a deat provider for a book **/
    public boolean deleteData(Book book){
        if(book.getId()==0 || book.getId()==pref_workingBookId){
            return false;
        }
        String dbname = "dm_"+book.getId()+".db";
        boolean r = appContext.deleteDatabase(dbname);
        return r;
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
    
    private void initMasterDataProvider(Context context) {
        String dbname = "dm_master.db";
        masterDataProvider = new SQLiteMasterDataProvider(new SQLiteMasterDataHelper(context,dbname),calendarHelper);
        masterDataProvider.init();
        if(DEBUG){
            Logger.d("masterDataProvider :"+masterDataProvider);
        }
        //create selected book if not exist;
        int sbid = getWorkingBookId();
        Book book = masterDataProvider.findBook(sbid);
        if(book==null){
            String name = i18n.string(R.string.title_book)+sbid;
            book = new Book(name,i18n.string(R.string.label_default_book_symbol),SymbolPosition.FRONT,"");
            masterDataProvider.newBookNoCheck(getWorkingBookId(), book);
        }
        currencySymbol = book.getSymbol();
    }
    public void cleanMasterDataProvider(Context context){
        if(masterDataProvider!=null){
            if(DEBUG){
                Logger.d("cleanmasterDataProvider :"+masterDataProvider);
            }
            masterDataProvider.destroyed();
            masterDataProvider = null;
        }
    }
    
    public int getOrientation(){
        if(appContext==null){
            return Configuration.ORIENTATION_UNDEFINED;
        }
        return appContext.getResources().getConfiguration().orientation;
    }
    
    public IDataProvider getDataProvider(){
        if(dataProvider==null){
            throw new IllegalStateException("no available dataProvider, di you get data provider out of life cycle");
        }
        return dataProvider;
    }
    
    public IMasterDataProvider getMasterDataProvider(){
        if(masterDataProvider==null){
            throw new IllegalStateException("no available dataProvider, di you get data provider out of life cycle");
        }
        return masterDataProvider;
    }

    public void setPreferenceDirty() {
        prefsDirty = true;
    }
    
    public DateFormat getDateFormat(){
        return android.text.format.DateFormat.getDateFormat(appContext);
    }
    
    public DateFormat getLongDateFormat(){
        return android.text.format.DateFormat.getLongDateFormat(appContext);
    }
    
    public DateFormat getMediumDateFormat(){
        return android.text.format.DateFormat.getMediumDateFormat(appContext);
    }
    
    public DateFormat getTimeFormat(){
        return android.text.format.DateFormat.getTimeFormat(appContext);
    }
    public Drawable getDrawable(int id){
        return appContext.getResources().getDrawable(id);
    }
    
    public String toFormattedMoneyString(double money){
        IMasterDataProvider imdp = getMasterDataProvider();
        Book book = imdp.findBook(getWorkingBookId());
        return Formats.money2String(money, book.getSymbol(), book.getSymbolPosition());
    }
}
