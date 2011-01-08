package com.bottleworks.dailymoney;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * 
 * @author dennis
 * 
 */
public class DetailListActivity extends ContextsActivity {

    
//    private static String[] bindingFrom = new String[] { "layout","from", "to", "money" , "note", "date" };
//    
//    private static int[] bindingTo = new int[] { R.id.detlist_item_layout,R.id.detlist_item_from, R.id.detlist_item_to, R.id.detlist_item_money,R.id.detlist_item_note,R.id.detlist_item_date };
//    
//    
//    private List<Detail> listViewData = new ArrayList<Detail>();
//    
//    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();
//
//    private ListView listView;
//    
//    private SimpleAdapter listViewAdapter;
//    
//    private Map<String,Account> accountCache = new HashMap<String,Account>();
    
    DetailListHelper detailListHelper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detlist);
        initialContent();
    }

    @Override
    public void onResume() {
        super.onResume();
        GUIs.postResume(new Runnable() {
            @Override
            public void run() {
                reloadData();
            }
        });
        
    }


    private void initialContent() {
        detailListHelper = new DetailListHelper(this, i18n,true, new DetailListHelper.OnDetailChangedListener() {
            @Override
            public void onDetailChanged(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_updated));
                reloadData();
            }

            @Override
            public void onDetailDeleted(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_deleted));
                reloadData();
            }
        });
        ListView listView = (ListView)findViewById(R.id.detlist_list);
        detailListHelper.setup(listView);
        registerForContextMenu(listView);
    }
    

    private void reloadData() {
        final IDataProvider idp = Contexts.instance().getDataProvider();
//        detailListHelper.reloadData(idp.listAllDetail());
        GUIs.doBusy(this,new GUIs.BusyAdapter() {
            List<Detail> data = null;
            @Override
            public void run() {
                data = idp.listAllDetail();
            }
            @Override
            public void onBusyFinish() {
                detailListHelper.reloadData(data);
            }
        });
        
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.detlist_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("option menu selected :" + item.getItemId());
        switch (item.getItemId()) {
        case R.id.detlist_menu_new:
            detailListHelper.doNewAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.detlist_list) {
            getMenuInflater().inflate(R.menu.detlist_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Logger.d("context menu selected :" + item.getItemId() + ", pos " + info.position);
        switch (item.getItemId()) {
        case R.id.detlist_menu_edit:
            detailListHelper.doEditAccount(info.position);
            return true;
        case R.id.detlist_menu_delete:
            detailListHelper.doDeleteAccount(info.position);
            return true;
        case R.id.detlist_menu_copy:
            detailListHelper.doCopyAccount(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
