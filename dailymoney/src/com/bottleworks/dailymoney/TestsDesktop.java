package com.bottleworks.dailymoney;

import java.io.File;
import java.io.StringWriter;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;

import com.bottleworks.commons.util.Files;
import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.csvreader.CsvWriter;

public class TestsDesktop extends AbstractDesktop {
    
    public TestsDesktop(Context context) {
        super(context);
        
    }
    
    @Override
    public boolean isAvailable(){
        return Contexts.instance().isPrefOpenTestsDesktop();
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_tests);
        icon = R.drawable.dt_tests;

        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testExportAccount();
            }}, "Export account",R.drawable.dt_item_test));
        
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testExportDetail();
            }}, "Export detail",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(1);
            }}, "Create test data",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(100);
            }}, "Create many test data",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testBusy();
            }}, "test busy",R.drawable.dt_item_test));
        
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testJust();
            }}, "just test",R.drawable.dt_item_test));
        
        DesktopItem padding = new DesktopItem(new Runnable(){
            @Override
            public void run() {
                
            }}, "padding",R.drawable.dt_item_test);
        
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
        addItem(padding);
    }
    
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
    
    private void testCreateTestdata(final int loop) {
        GUIs.doBusy(context,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"create test data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DataCreator(idp,i18n).createTestData(loop);
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
    
    

    protected void testJust() {
        Calendar.getInstance().setFirstDayOfWeek(Calendar.SUNDAY);
        System.out.println(">>>>>>>>>>>>."+Calendar.getInstance().getFirstDayOfWeek());
//        System.out.println(">>>>>>>>>>>>."+);
    }

}
