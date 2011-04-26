package com.bottleworks.dailymoney.ui;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.context.ContextsActivity;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.Detail;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * 
 * @author dennis
 * 
 */
public class AccountDetailListActivity extends ContextsActivity {
       
    public static final String INTENT_START = "start";
    public static final String INTENT_END = "end";
    public static final String INTENT_TARGET = "target";
    public static final String INTENT_TARGET_INFO = "targetInfo";
    
    
    DetailListHelper detailListHelper;
    
    TextView infoView;
    
    
    private Date startDate;
    private Date endDate;
    private String info;
    private Object target;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accdetlist);
        initialIntent();
        initialContent();
        GUIs.delayPost(new Runnable() {
            @Override
            public void run() {
                reloadData();
            }
        },25);
    }
    

    private void initialIntent() {
        Bundle b = getIntentExtras(); 
        startDate = (Date)b.get(INTENT_START);
        endDate = (Date)b.get(INTENT_END);
        target = b.get(INTENT_TARGET);
        info = b.getString(INTENT_TARGET_INFO);
        info = info==null?" ":info+" ";
        
        DateFormat format = getContexts().getDateFormat();
        String fromStr = startDate==null?"":format.format(startDate);
        String toStr = endDate==null?"":format.format(endDate);

        info = info + i18n.string(R.string.label_accdetlist_dateinfo,fromStr,toStr); 
        
        if(target instanceof AccountType){
        }else if(target instanceof Account){
        }else{
            throw new IllegalStateException("unknow target type "+target);
        }
        
    }


    private void initialContent() {
        
        detailListHelper = new DetailListHelper(this, i18n,calHelper,true,new DetailListHelper.OnDetailListener() {
            @Override
            public void onDetailDeleted(Detail detail) {
                GUIs.shortToast(AccountDetailListActivity.this, i18n.string(R.string.msg_detail_deleted));
                reloadData();
                setResult(RESULT_OK);
            }
        });
        
        infoView = (TextView)findViewById(R.id.accdetlist_infobar);
        
        ListView listView = (ListView)findViewById(R.id.accdetlist_list);
        detailListHelper.setup(listView);
        registerForContextMenu(listView);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_DETAIL_EDITOR_CODE && resultCode==Activity.RESULT_OK){
            GUIs.delayPost(new Runnable(){
                @Override
                public void run() {
                    reloadData();
                    setResult(RESULT_OK);
                }});
            
        }
    }
    
    private void reloadData() {
        infoView.setText(info);
        final IDataProvider idp = getContexts().getDataProvider();
//        detailListHelper.reloadData(idp.listAllDetail());
        GUIs.doBusy(this,new GUIs.BusyAdapter() {
            @SuppressWarnings("unchecked")
            List<Detail> data = Collections.EMPTY_LIST;
            int count = 0;
            @Override
            public void run() {
                if(target instanceof Account){
                    data = idp.listDetail((Account)target,IDataProvider.LIST_DETAIL_MODE_BOTH,startDate,endDate,getContexts().getPrefMaxRecords());
                    count = idp.countDetail((Account)target,IDataProvider.LIST_DETAIL_MODE_BOTH,startDate, endDate);
                }else if(target instanceof AccountType){
                    data = idp.listDetail((AccountType)target,IDataProvider.LIST_DETAIL_MODE_BOTH,startDate,endDate,getContexts().getPrefMaxRecords());
                    count = idp.countDetail((AccountType)target,IDataProvider.LIST_DETAIL_MODE_BOTH,startDate, endDate);
                }
            }
            @Override
            public void onBusyFinish() {
                detailListHelper.reloadData(data);
                infoView.setText(info + i18n.string(R.string.label_count,count));
            }
        });    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.accdetlist_optmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.accdetlist_menu_new:
            detailListHelper.doNewDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.accdetlist_list) {
            getMenuInflater().inflate(R.menu.accdetlist_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.accdetlist_menu_edit:
            detailListHelper.doEditDetail(info.position);
            return true;
        case R.id.accdetlist_menu_delete:
            detailListHelper.doDeleteDetail(info.position);
            return true;
        case R.id.accdetlist_menu_copy:
            detailListHelper.doCopyDetail(info.position);
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
