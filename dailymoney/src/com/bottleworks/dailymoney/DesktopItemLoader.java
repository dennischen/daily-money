package com.bottleworks.dailymoney;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.bottleworks.commons.util.Files;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.DesktopActivity.DesktopItem;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.DefaultDataCreator;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.csvreader.CsvWriter;

/**
 * static desktop item loader
 * @author dennis
 *
 */
public class DesktopItemLoader {

    Context context;
    I18N i18n;
    public DesktopItemLoader(Context context,I18N i18n){
        this.context = context;
        this.i18n = i18n;
    }
    
    public List<DesktopItem> loadTestFunctions(){
        List<DesktopItem> fnsItems = new ArrayList<DesktopItem>();
        
        DesktopItem detaildt = new DesktopItem(new Runnable(){
            public void run(){
                GUIs.shortToast(context,"test detail editor");
                Detail d = new Detail("a","A","b","B",new Date(),10D,"test note");
                DetailEditorDialog dlg = new DetailEditorDialog(context,new DetailEditorDialog.OnFinishListener() {
                    @Override
                    public boolean onFinish(DetailEditorDialog dlg, View v, Object data) {
                        GUIs.shortToast(context,"detail created "+data);
                        return true;
                    }
                }, true, d);
                dlg.show();
                
            }
        },i18n.string(R.string.title_detmgnt),R.drawable.dt_item_detail);
        
        DesktopItem accdt = new DesktopItem(context,AccountMgntActivity.class,i18n.string(R.string.title_accmgnt),R.drawable.dt_item_account);
        DesktopItem prefdt = new DesktopItem(context,PrefsActivity.class,i18n.string(R.string.title_prefs),R.drawable.dt_item_prefs);
        
        DesktopItem testdt = new DesktopItem(context,TestActivity.class,"Test Activity",R.drawable.dt_item_test);
        
        fnsItems.add(detaildt);
        fnsItems.add(accdt);
        fnsItems.add(prefdt);
        
        
        
        /** test */
        fnsItems.add(testdt);
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testResetDataprovider();
            }}, "Reset dataprovider",R.drawable.dt_item_test));
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateDefaultdata();
            }}, "Create default data",R.drawable.dt_item_test));
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCSV();
            }}, "test csv",R.drawable.dt_item_test));
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testBusy();
            }}, "test busy",R.drawable.dt_item_test));
        
        return fnsItems;
    }
    
    
    public List<DesktopItem> loadTestReports(){
        List<DesktopItem> reportsItems = new ArrayList<DesktopItem>();
        
        DesktopItem accdt = new DesktopItem(context,AccountMgntActivity.class,i18n.string(R.string.title_accmgnt),R.drawable.dt_item_account);
        DesktopItem prefdt = new DesktopItem(context,PrefsActivity.class,i18n.string(R.string.title_prefs),R.drawable.dt_item_prefs);
        
        DesktopItem testdt = new DesktopItem(context,TestActivity.class,"Test Activity",R.drawable.dt_item_test);
        
        reportsItems.add(testdt);
        reportsItems.add(accdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        reportsItems.add(prefdt);
        
        return reportsItems;
    }
    
    
    /*
     * test
     */
    
    private void testResetDataprovider() {
        GUIs.doBusy(context,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"reset data provider");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                idp.reset();
            }});

    }
    
    private void testCreateDefaultdata() {
        GUIs.doBusy(context,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"create default data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DefaultDataCreator(idp,i18n).createDefaultAccounts();
            }});
        
    }
    
    private void testBusy() {
        GUIs.shortToast(context,"I am busy");
        GUIs.doBusy(context,new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"I am not busy now");
            }
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }});
        
    }
    
    private void testCSV(){
        try{
            StringWriter sw = new StringWriter();
            CsvWriter csvw = new CsvWriter(sw,',');
            
            for(Account a:Contexts.instance().getDataProvider().listAccount(null)){
                csvw.writeRecord(new String[]{a.getId(),a.getName(),a.getType(),Formats.double2String(a.getInitialValue())});
            }
            csvw.close();
            String msg = sw.toString();
            if(msg.length()==0){
                GUIs.longToast(context, "no account");
            }else{
                File sd = Environment.getExternalStorageDirectory();
                File folder = new File(sd,"bwDailyMoney");
                if(!folder.exists()){
                    folder.mkdir();
                }
                File file = new File(folder,"account-export.csv");
                if(!file.exists()){
                    file.createNewFile();
                }
                Files.saveString(msg, file, "utf8");
                GUIs.longToast(context, "csv save to "+file.getAbsolutePath()+", content, \n "+msg);
            }
        }catch(Exception x){
            GUIs.longToast(context, "error "+x.getMessage());
            x.printStackTrace();
        }
    }
}
