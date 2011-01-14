package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Desktop;
import com.bottleworks.dailymoney.ui.MainDesktop;
import com.bottleworks.dailymoney.ui.ReportsDesktop;
import com.bottleworks.dailymoney.ui.TestsDesktop;
import com.bottleworks.dailymoney.ui.Desktop.DesktopItem;
/**
 * 
 * @author dennis
 *
 */
public class DesktopActivity extends ContextsActivity implements OnTabChangeListener, OnItemClickListener {

 

    
    private String currTab = null;

    private GridView gridView;

    private DesktopItemAdapter gridViewAdapter;

    List<Desktop> desktops = new ArrayList<Desktop>();
    
    String appinfo;

    private DateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE"); 
    
    DesktopItem lastClickedItem;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desktop);
        initialApplicationInfo();
        initialDesktopItem();
        initialTab();
        initialContent();
        loadData();
        
    }


    private void initialApplicationInfo() {
        appinfo = i18n.string(R.string.app_name);
        String ver = Contexts.uiInstance().getApplicationVersionName();
        appinfo += " ver : "+ver;
        
        if(Contexts.uiInstance().isFirstTime()){
            IDataProvider idp = Contexts.uiInstance().getDataProvider();
            if(idp.listAccount(null).size()==0){
                //cause of this function is not ready in previous version, so i check the size for old user
                new DataCreator(idp,i18n).createDefaultAccount();
            }
            GUIs.longToast(this,R.string.msg_firsttime_use_hint);
        }
        
    }


    private void initialDesktopItem() {
        
        Desktop[] dts = new Desktop[]{new MainDesktop(this),new ReportsDesktop(this),new TestsDesktop(this)};
        
        for(Desktop dt:dts){
            if(dt.isAvailable()){
                desktops.add(dt);
            }
        }
    }

    private void initialTab() {
        TabHost tabs = (TabHost) findViewById(R.id.dt_tabs);
        tabs.setup();

        
        for(Desktop d:desktops){
            TabSpec tab = tabs.newTabSpec(d.getLabel());
            tab.setIndicator(d.getLabel(),getResources().getDrawable(d.getIcon()));
            tab.setContent(R.id.dt_grid);
            tabs.addTab(tab);
            if(currTab==null){
                currTab = tab.getTag();
            }
        }

        if(desktops.size()>1){
            // workaround, force refresh
            tabs.setCurrentTab(1);
            tabs.setCurrentTab(0);
        }

        tabs.setOnTabChangedListener(this);

    }

    private void initialContent() {

        gridViewAdapter = new DesktopItemAdapter();
        gridView = (GridView) findViewById(R.id.dt_grid);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(this);
        // registerForContextMenu(gridView);

    }

    private void loadAppInfo(){
        Date now = new Date();
        String date = Contexts.uiInstance().getDateFormat().format(now)+" "+dayOfWeekFormat.format(now)+" - ";
        ((TextView)findViewById(R.id.dt_info)).setText(date + appinfo);
    }
    
    private void loadData() {
        loadAppInfo();
        for(Desktop d:desktops){
            if(d.getLabel().equals(currTab)){
                d.refresh();
                break;
            }
        }
        
        gridViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabChanged(String tabId) {
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
             lastClickedItem = getCurrentDesktopItems().get(pos);
             lastClickedItem.run();
         }
    }
    
    
    @SuppressWarnings("unchecked")
    List<DesktopItem> getCurrentDesktopItems(){
        for(Desktop d:desktops){
            if(d.getLabel().equals(currTab)){
                return d.getItems();
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(lastClickedItem!=null){
            lastClickedItem.onActivityResult(requestCode, resultCode, data);
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
            iv.setImageResource(item.getIcon());
            tv.setText(item.getLabel());
            return view;
        }

    }
    
    

}
