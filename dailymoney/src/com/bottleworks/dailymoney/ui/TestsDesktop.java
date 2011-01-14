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
import com.bottleworks.dailymoney.calculator2.Calculator;
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
                intent = new Intent(activity,Calculator.class);
                intent.putExtra(Calculator.INTENT_START_VALUE,"12345");
                intent.putExtra(Calculator.INTENT_NEED_RESULT,true);
                activity.startActivityForResult(intent,999);
                GUIs.shortToast(activity,"Call Calculator");
            }}, "start cal",R.drawable.dt_item_test){
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                if(requestCode==999 && resultCode==Activity.RESULT_OK){
                    GUIs.shortToast(activity,"Calculator result = "+data.getExtras().getString(Calculator.INTENT_RESULT_VALUE));
                }
            }
        });
        
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
