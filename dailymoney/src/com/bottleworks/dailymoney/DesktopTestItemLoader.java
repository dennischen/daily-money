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
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.csvreader.CsvWriter;

/**
 * static desktop item loader
 * @author dennis
 *
 */
public class DesktopTestItemLoader {

    Context context;
    I18N i18n;
    public DesktopTestItemLoader(Context context,I18N i18n){
        this.context = context;
        this.i18n = i18n;
    }
    
    public List<DesktopItem> loadFunctions(){
        List<DesktopItem> fnsItems = new ArrayList<DesktopItem>();
        
        DesktopItem detlistdt = new DesktopItem(context,DetailListActivity.class,i18n.string(R.string.dt_detlist),R.drawable.dt_item_detail);
        DesktopItem adddetdt =  new DesktopItem(new Runnable(){
            public void run(){
              Detail d = new Detail("","",new Date(),0D,"");
              DetailEditorDialog dlg = new DetailEditorDialog(context,new DetailEditorDialog.OnFinishListener() {
                  @Override
                  public boolean onFinish(DetailEditorDialog dlg, View v, Object data) {
                      switch(v.getId()){
                      case R.id.deteditor_ok:
                          Detail dt = (Detail)data;
                          Contexts.instance().getDataProvider().newDetail(dt);
                          break;
                      case R.id.deteditor_close:
                          GUIs.shortToast(context,i18n.string(R.string.msg_created_detail,dlg.getCounter()));
                      }
                      return true;
                  }
              }, true, d);
              dlg.show();
            }
        },i18n.string(R.string.dt_adddetail),R.drawable.dt_item_adddetail);
        
        DesktopItem accmgntdt = new DesktopItem(context,AccountMgntActivity.class,i18n.string(R.string.dt_accmgnt),R.drawable.dt_item_account);
        DesktopItem prefdt = new DesktopItem(context,PrefsActivity.class,i18n.string(R.string.dt_prefs),R.drawable.dt_item_prefs);

        
        fnsItems.add(adddetdt);
        fnsItems.add(detlistdt);
        fnsItems.add(accmgntdt);
        fnsItems.add(prefdt);

        /** test */

       
        
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testExportAccount();
            }}, "Export account",R.drawable.dt_item_test));
        
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testExportDetail();
            }}, "Export detail",R.drawable.dt_item_test));
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testResetDataprovider();
            }}, "Reset dataprovider",R.drawable.dt_item_test));
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata();
            }}, "Create test data",R.drawable.dt_item_test));
        
        fnsItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testBusy();
            }}, "test busy",R.drawable.dt_item_test));
        
        return fnsItems;
    }
 

    public List<DesktopItem> loadReports(){
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
    
   
    
    private void testExportDetail(){
        try{
            StringWriter sw = new StringWriter();
            CsvWriter csvw = new CsvWriter(sw,',');
            
            for(Detail d:Contexts.instance().getDataProvider().listAllDetail()){
                csvw.writeRecord(new String[]{Integer.toString(d.getId()),d.getFrom(),
                        d.getTo(),Formats.normalizeDate2String(d.getDate()),Formats.normalizeDouble2String(d.getMoney()),d.getNote()});
            }
            csvw.close();
            String msg = sw.toString();
            if(msg.length()==0){
                GUIs.longToast(context, "no detail");
            }else{
                File sd = Environment.getExternalStorageDirectory();
                File folder = new File(sd,"bwDailyMoney");
                if(!folder.exists()){
                    folder.mkdir();
                }
                File file = new File(folder,"detail-export.csv");
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
    
    private void testCreateTestdata() {
        GUIs.doBusy(context,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"create test data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DataCreator(idp,i18n).createTestData();
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
    
    private void testExportAccount(){
        try{
            StringWriter sw = new StringWriter();
            CsvWriter csvw = new CsvWriter(sw,',');
            
            for(Account a:Contexts.instance().getDataProvider().listAccount(null)){
                csvw.writeRecord(new String[]{a.getId(),a.getName(),a.getType(),Formats.normalizeDouble2String(a.getInitialValue())});
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
