package com.bottleworks.dailymoney.ui;

import java.util.Calendar;

import android.content.Context;

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
    
    public TestsDesktop(Context context) {
        super(context);
        
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
            }}, "rest data provider",R.drawable.dt_item_test));
        
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
        GUIs.doBusy(context,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(context,"create test data");
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
