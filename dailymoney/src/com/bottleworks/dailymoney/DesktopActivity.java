package com.bottleworks.dailymoney;

import java.util.Collections;
import java.util.Date;
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

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.ui.ContextsActivity;
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
        initialDesktopItem();
        initialTab();
        initialContent();
        loadData();
    }


    private void initialDesktopItem() {
        DesktopTestItemLoader loader = new DesktopTestItemLoader(this,i18n);
        dataItems = loader.loadFunctions();
        reportsItems = loader.loadReports();
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

        gridViewAdapter = new DesktopItemAdapter();
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
    
    
    List<DesktopItem> getCurrentDesktopItems(){
        if(TAB_FUNCTIONS.equals(currTab)){
            return dataItems;
        }else if(TAB_REPORTS.equals(currTab)){
            return reportsItems;
        }
        return Collections.EMPTY_LIST;
    }

    static class DesktopItem {
        int icon;
        String label;
        Runnable run;

        public DesktopItem(Runnable run, String label) {
            this(run,label,R.drawable.dt_item);
        }

        public DesktopItem(Runnable run, String label, int icon) {
            this.run = run;
            this.label = label;
            this.icon = icon;
        }

        public void run() {
            run.run();
        }
    }

    public  static class IntentRun implements Runnable {
        Intent intent;
        Context context;

        IntentRun(Context context,Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public void run() {
            context.startActivity(intent);
        }
    }
    
    public  static class ActivityRun implements Runnable {
        Class activity;
        Context context;

        ActivityRun(Context context,Class activity) {
            this.context = context;
            this.activity = activity;
        }

        public void run() {
            context.startActivity(new Intent(context,activity));
        }
    }
    
    public class DesktopItemAdapter extends BaseAdapter {

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
    
    
    

}
