package com.bottleworks.dailymoney;

import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;

/**
 * 
 * @author dennis
 * 
 */
public class DetailListActivity extends ContextsActivity {
    
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
        detailListHelper = new DetailListHelper(this, i18n,false, new DetailListHelper.OnDetailListener() {
            @Override
            public boolean onDetailUpdated(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_updated));
                reloadData();
                return true;
            }

            @Override
            public boolean onDetailDeleted(Detail detail) {
                GUIs.shortToast(DetailListActivity.this, i18n.string(R.string.msg_detail_deleted));
                return false;
            }

            @Override
            public boolean onDetailCreated(Detail detail) {
                return false;
            }

            @Override
            public boolean onEditorClosed(int counter) {
                if(counter>0){
                    GUIs.shortToast(DetailListActivity.this,i18n.string(R.string.msg_created_detail,counter));
                    reloadData();
                    return true;
                }
                return false;
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
            detailListHelper.doNewDetail();
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
            detailListHelper.doEditDetail(info.position);
            return true;
        case R.id.detlist_menu_delete:
            detailListHelper.doDeleteDetail(info.position);
            return true;
        case R.id.detlist_menu_copy:
            detailListHelper.doCopyDetail(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
