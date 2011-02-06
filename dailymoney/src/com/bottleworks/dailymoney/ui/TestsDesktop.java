package com.bottleworks.dailymoney.ui;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.core.R;
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
        return Contexts.instance().isPrefOpenTestsDesktop();
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_tests);
        icon = R.drawable.tab_tests;
        
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                Contexts.instance().getDataProvider().reset();
                GUIs.shortToast(activity,"reset data provider");
            }}, "rest data provider",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testFirstDayOfWeek();
            }}, "first day of week",R.drawable.dtitem_test){
        });
        
        
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(1);
            }}, "test data1",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(25);
            }}, "test data25",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(50);
            }}, "test data50",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(100);
            }}, "test data100",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateTestdata(200);
            }}, "test data200",R.drawable.dtitem_test));
        addItem(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testJust();
            }}, "just test",R.drawable.dtitem_test));
        
        DesktopItem padding = new DesktopItem(new Runnable(){
            @Override
            public void run() {
                
            }}, "padding",R.drawable.dtitem_test);
        
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
    
    protected void testFirstDayOfWeek() {
        CalendarHelper calHelper = Contexts.instance().getCalendarHelper();
        for(int i=0;i<100;i++){
            Date now = new Date();
            Date start = calHelper.weekStartDate(now);
            Date end = calHelper.weekEndDate(now);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
//            System.out.println("1>>>>>>>>>>> "+now);
            System.out.println("2>>>>>>>>>>> "+start);
//            System.out.println("3>>>>>>>>>>> "+end);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void testCreateTestdata(final int loop) {
        GUIs.doBusy(activity,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(activity,"create test data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DataCreator(idp,i18n).createTestData(loop);
            }});
        
    }


    protected void testJust() {
        CalendarHelper calHelper = Contexts.instance().getCalendarHelper();
        Date now = new Date();
        Date start = calHelper.weekStartDate(now);
        Date end = calHelper.weekEndDate(now);
        System.out.println(">>>>>>>>>>>>>>> "+now);
        System.out.println("1>>>>>>>>>>> "+now);
        System.out.println("2>>>>>>>>>>> "+start);
        System.out.println("3>>>>>>>>>>> "+end);
        System.out.println(">>>>>>>>>>>>>> "+now);
        
    }

}
