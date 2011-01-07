package com.bottleworks.dailymoney;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.DefaultDataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;
import com.csvreader.CsvWriter;
/**
 * 
 * @author dennis
 *
 */
public class DesktopActivity extends ContextsActivity implements OnTabChangeListener, OnItemClickListener {

    
    private static final String TAB_FUNCTIONS = "functions";
    private static final String TAB_REPORTS = "reports";
    
    private String currTab = null;

    private GridView gridView;

    private DesktopItemAdapter gridViewAdapter;

    List<DesktopItem> dataItems;
    List<DesktopItem> reportsItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desktop);
        initialDesktopData();
        initialTab();
        initialContent();
        loadData();
    }

    private void initialDesktopData() {
        dataItems = new ArrayList<DesktopItem>();
        reportsItems = new ArrayList<DesktopItem>();
        
        DesktopItem detaildt = new DesktopItem(new Runnable(){
            public void run(){
                GUIs.shortToast(DesktopActivity.this,"not implement yet");
            }
        },i18n.string(R.string.title_detailmgnt),R.drawable.dt_item_detail);
        
        DesktopItem accdt = new DesktopItem(AccountMgntActivity.class,i18n.string(R.string.title_accmgnt),R.drawable.dt_item_account);
        DesktopItem prefdt = new DesktopItem(PrefsActivity.class,i18n.string(R.string.title_prefs),R.drawable.dt_item_prefs);
        
        DesktopItem testdt = new DesktopItem(TestActivity.class,"Test Activity",R.drawable.dt_item_test);
        
        dataItems.add(detaildt);
        dataItems.add(accdt);
        dataItems.add(prefdt);
        
        
        
        /** test */
        dataItems.add(testdt);
        dataItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testResetDataprovider();
            }}, "Reset dataprovider",R.drawable.dt_item_test));
        dataItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCreateDefaultdata();
            }}, "Create default data",R.drawable.dt_item_test));
        dataItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testCSV();
            }}, "test csv",R.drawable.dt_item_test));
        dataItems.add(new DesktopItem(new Runnable(){
            @Override
            public void run() {
                testBusy();
            }}, "test busy",R.drawable.dt_item_test));
        
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

        
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initialTab() {
        TabHost tabs = (TabHost) findViewById(R.id.dt_tabs);
        tabs.setup();

        TabSpec tab = tabs.newTabSpec(TAB_FUNCTIONS);
        tab.setIndicator(i18n.string(R.string.label_functions),getResources().getDrawable(R.drawable.dt_tab_functions));
        tab.setContent(R.id.dt_grid);
        tabs.addTab(tab);
        currTab = tab.getTag();

        tab = tabs.newTabSpec(TAB_REPORTS);
        tab.setIndicator(i18n.string(R.string.label_reports),getResources().getDrawable(R.drawable.dt_tab_reports));
        tab.setContent(R.id.dt_grid);
        tabs.addTab(tab);

        // workaround, force refresh
        tabs.setCurrentTab(1);
        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(this);

    }

    private void initialContent() {

        gridViewAdapter = new DesktopItemAdapter(this);
        gridView = (GridView) findViewById(R.id.dt_grid);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(this);
        // registerForContextMenu(gridView);

    }

    private void loadData() {
        gridViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabChanged(String tabId) {
        Logger.d("switch to tab : " + tabId);
        currTab = tabId;
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.accmgnt_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("option menu selected :" + item.getItemId());
        // switch (item.getItemId()) {
        // case R.id.accmgnt_menu_new:
        // doNewAccount();
        // return true;
        // }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
         if(parent == gridView){
             getCurrentDesktopItems().get(pos).run();
         }
    }

    class DesktopItem {
        int icon;
        String label;
        Runnable run;

        public DesktopItem(Class<? extends Activity> activity, String label, int icon) {
            this(new ActivityRun(activity), label, icon);
        }

        public DesktopItem(Runnable run, String label, int icon) {
            this.run = run;
            this.label = label;
            this.icon = icon;
        }

        public DesktopItem(Class<? extends Activity> activity, String label) {
            this(new ActivityRun(activity), label);
        }

        public DesktopItem(Runnable run, String label) {
            this.run = run;
            this.label = label;
            this.icon = R.drawable.dt_item;
        }

        public void run() {
            run.run();
        }
    }

    class ActivityRun implements Runnable {
        Class<? extends Activity> activity;

        ActivityRun(Class<? extends Activity> activity) {
            this.activity = activity;
        }

        public void run() {
            startActivity(new Intent(DesktopActivity.this, activity));
        }
    }
    
    List<DesktopItem> getCurrentDesktopItems(){
        if(TAB_FUNCTIONS.equals(currTab)){
            return dataItems;
        }else if(TAB_REPORTS.equals(currTab)){
            return reportsItems;
        }
        return Collections.EMPTY_LIST;
    }
    
    public class DesktopItemAdapter extends BaseAdapter {
        private Context mContext;

        public DesktopItemAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return getCurrentDesktopItems().size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            TextView tv;
            LinearLayout view;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                
                view = new LinearLayout(DesktopActivity.this);
                view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
                GUIs.inflateView(DesktopActivity.this, view, R.layout.desktop_item);
            } else {
                view = (LinearLayout)convertView;
            }

            iv = (ImageView)view.findViewById(R.id.dt_icon);
            tv = (TextView)view.findViewById(R.id.dt_label);
            
            DesktopItem item = getCurrentDesktopItems().get(position);
            iv.setImageResource(item.icon);
            tv.setText(item.label);
            return view;
        }

    }
    
    
    /*
     * test
     */
    
    private void testResetDataprovider() {
        GUIs.doBusy(this,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(DesktopActivity.this,"reset data provider");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                idp.reset();
            }});

    }
    
    private void testCreateDefaultdata() {
        GUIs.doBusy(this,new GUIs.BusyAdapter(){
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(DesktopActivity.this,"create default data");
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DefaultDataCreator(idp,i18n).createDefaultAccounts();
            }});
        
    }
    
    private void testBusy() {
        GUIs.shortToast(DesktopActivity.this,"I am busy");
        GUIs.doBusy(this,new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.shortToast(DesktopActivity.this,"I am not busy now");
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
            GUIs.longToast(this, sw.toString());
        }catch(Exception x){
            x.printStackTrace();
        }
    }

}
