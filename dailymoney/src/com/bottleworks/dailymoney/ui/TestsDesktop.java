package com.bottleworks.dailymoney.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Html;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
/**
 * 
 * @author dennis
 *
 */
public class TestsDesktop extends AbstractDesktop {
    
    public TestsDesktop(Activity activity) {
        super(activity);
        
    }
    
    @Override
    public boolean isAvailable(){
        return Contexts.uiInstance().isPrefOpenTestsDesktop();
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_tests);
        icon = R.drawable.dt_tests;

        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                Contexts.uiInstance().getDataProvider().reset();
                GUIs.shortToast(activity,"reset data provider");
            }}, "rest data provider",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                Intent intent = null;
//                intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
                
                
//                intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("vnd.android.cursor.item/phone");
                
                activity.startActivityForResult(intent,1);
                
                
                
                
                GUIs.shortToast(activity,"send mail");
            }}, "start cal",R.drawable.dt_item_test));
        
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
//                Intent intent = new Intent(Intent.ACTION_SEND);
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                
//                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mail subject test "+new Date());
//                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Mail body test");
                intent.setType("text/html");
//                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mail html subject test");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<html><body><i>Mail body test</i>/"+ new Date()+"</body></html>"));
                File folder = new File(Environment.getExternalStorageDirectory(),"bwDailyMoney");
                File file1 = new File(folder,"accounts.csv");
                File file2 = new File(folder,"details.csv");
                ArrayList<Parcelable> attachment = new ArrayList<Parcelable>();
                attachment.add( Uri.fromFile(file1));
                attachment.add( Uri.fromFile(file2));
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachment);
                
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file1));
                
                activity.startActivity(Intent.createChooser(intent, "Email:"));
                
//                activity.startActivityForResult(intent,1);
                
                
                
                
                
                GUIs.shortToast(activity,"send mail");
            }}, "send mail",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(1);
            }}, "test data1",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(25);
            }}, "test data25",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(50);
            }}, "test data50",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(100);
            }}, "test data100",R.drawable.dt_item_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(200);
            }}, "test data200",R.drawable.dt_item_test));
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
    
    private void testCreateTestdata(final int loop) {
        GUIs.doBusy(activity,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(activity,"create test data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.uiInstance().getDataProvider();
                new DataCreator(idp,i18n).createTestData(loop);
            }});
        
    }
    

    protected void testJust() {
        Calendar.getInstance().setFirstDayOfWeek(Calendar.SUNDAY);
        System.out.println(">>>>>>>>>>>>."+Calendar.getInstance().getFirstDayOfWeek());
//        System.out.println(">>>>>>>>>>>>."+);
    }

}
