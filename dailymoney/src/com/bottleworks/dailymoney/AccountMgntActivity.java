package com.bottleworks.dailymoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;
import com.bottleworks.dailymoney.ui.NamedItem;

/**
 * this activity manages the account (of detail) with tab widgets of android,
 * there are 4 type of account, income, expense, asset and liability.
 * 
 * @author dennis
 * @see {@link AccountType}
 */
public class AccountMgntActivity extends ContextsActivity implements OnTabChangeListener,OnItemClickListener, AccountEditorDialog.OnFinishListener {
    
    private static String[] bindingFrom = new String[] { "name", "initvalue", "id" };
    
    private static int[] bindingTo = new int[] { R.id.accmgnt_item_name, R.id.accmgnt_item_initvalue, R.id.accmgnt_item_id };
    
    
    private List<Account> listViewData = new ArrayList<Account>();
    
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private String currTab = null;

    private ListView listView;
    
    private SimpleAdapter listViewAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accmgnt);
        initialTab();
        initialContent();
        
        reloadData();
    }

    private void initialTab() {
        TabHost tabs = (TabHost) findViewById(R.id.accmgnt_tabs);
        tabs.setup();

        
        AccountType[] ata = AccountType.getSupportedType();
        Resources r = getResources();
        for(AccountType at:ata){
            TabSpec tab = tabs.newTabSpec(at.getType());
            tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()),r.getDrawable(at.getDrawable()));
            tab.setContent(R.id.accmgnt_list);
            tabs.addTab(tab);
            if(currTab==null){
                currTab = tab.getTag();
            }
        }
        // workaround, force refresh
        if(ata.length>1){
            tabs.setCurrentTab(1);
            tabs.setCurrentTab(0);
        }

        tabs.setOnTabChangedListener(this);

    }

    private void initialContent() {
        listViewAdapter = new SimpleAdapter(this, listViewMapList, R.layout.accmgnt_item, bindingFrom, bindingTo);
        listViewAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){

            @Override
            public boolean setViewValue(View view, Object data, String text) {
                NamedItem item = (NamedItem)data;
                String name = item.getName();
                Account acc = (Account)item.getValue();
                //not textview, not initval
                if(!(view instanceof TextView)){
                    return false;
                }
                AccountType at = AccountType.find(acc.getType());
                TextView tv = (TextView)view;
                
                if(at==AccountType.INCOME){
                    tv.setTextColor(getResources().getColor(R.color.income_fge)); 
                }else if(at==AccountType.EXPENSE){
                    tv.setTextColor(getResources().getColor(R.color.expense_fge));
                }else if(at==AccountType.ASSET){
                    tv.setTextColor(getResources().getColor(R.color.asset_fge));
                }else if(at==AccountType.LIABILITY){
                    tv.setTextColor(getResources().getColor(R.color.liability_fge));
                }else if(at==AccountType.OTHER){
                    tv.setTextColor(getResources().getColor(R.color.other_fge));
                }else{
                    tv.setTextColor(getResources().getColor(R.color.unknow_fge));
                }
                
                //reset
                tv.setVisibility(View.VISIBLE);
                
                if(!name.equals(bindingFrom[1])){
                    return false;
                }
                if(at==AccountType.INCOME || at==AccountType.EXPENSE){
                    tv.setVisibility(View.INVISIBLE);
                    return true;
                }
                
                text = i18n.string(R.string.label_initial_value)+" : "+data.toString();
                tv.setText(text);
                return true;
            }});
        
        listView = (ListView) findViewById(R.id.accmgnt_list);
        listView.setAdapter(listViewAdapter);
        
        
//        listView.setOnItemClickListener(this);
        
        registerForContextMenu(listView);
        
        
    }

    private void reloadData() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        listViewData = null;

        AccountType type = AccountType.find(currTab);
        listViewData = idp.listAccount(type);
        listViewMapList.clear();

        for (Account acc : listViewData) {
            Map<String, Object> row = new HashMap<String, Object>();
            listViewMapList.add(row);
            row.put(bindingFrom[0], new NamedItem(bindingFrom[0],acc,acc.getName()));
            row.put(bindingFrom[1], new NamedItem(bindingFrom[1],acc,Formats.double2String(acc.getInitialValue())));
            row.put(bindingFrom[2], new NamedItem(bindingFrom[2],acc,acc.getId()));
        }

        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabChanged(String tabId) {
        Logger.d("switch to tab : " + tabId);
        currTab = tabId;
        reloadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.accmgnt_optmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.d("option menu selected :" + item.getItemId());
        switch (item.getItemId()) {
        case R.id.accmgnt_menu_new:
            doNewAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.accmgnt_list) {
            getMenuInflater().inflate(R.menu.accmgnt_ctxmenu, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Logger.d("context menu selected :" + item.getItemId() + ", pos " + info.position);
        switch (item.getItemId()) {
        case R.id.accmgnt_menu_edit:
            doEditAccount(info.position);
            return true;
        case R.id.accmgnt_menu_delete:
            doDeleteAccount(info.position);
            return true;
        case R.id.accmgnt_menu_copy:
            doCopyAccount(info.position);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    private void doDeleteAccount(int pos) {
        Account acc = (Account) listViewData.get(pos);
        String name = acc.getName();

        Contexts.instance().getDataProvider().deleteAccount(acc.getId());
        reloadData();
        GUIs.shortToast(this, i18n.string(R.string.msg_account_deleted, name));

    }

    private void doEditAccount(int pos) {
        Account acc = (Account) listViewData.get(pos);
        AccountEditorDialog dlg = new AccountEditorDialog(this, this, false, acc);
        dlg.show();
    }
    
    private void doCopyAccount(int pos) {
        Account acc = (Account) listViewData.get(pos);
        AccountEditorDialog dlg = new AccountEditorDialog(this, this, true, acc);
        dlg.show();
    }

    private void doNewAccount() {
        Account acc = new Account(currTab, "", 0D);
        AccountEditorDialog dlg = new AccountEditorDialog(this, this, true, acc);
        dlg.show();
    }

    @Override
    public boolean onFinish(AccountEditorDialog dlg, View v, Object data) {
        switch (v.getId()) {
        case R.id.acceditor_ok:
            Account workingacc = (Account)data;
            boolean modeCreate = dlg.isModeCreate();
            String name = workingacc.getName();
            String type = workingacc.getType();
            IDataProvider idp = Contexts.instance().getDataProvider();
            Account namedAcc = idp.findAccount(type,name);
            if (modeCreate) {
                if (namedAcc != null) {
                    GUIs.alert(
                            this,i18n.string(R.string.msg_account_existed, name,
                                    AccountType.getDisplay(i18n, namedAcc.getType())));
                    return false;
                } else {
                    try {
                        idp.newAccount(workingacc);
                        GUIs.shortToast(this, i18n.string(R.string.msg_account_created, name,AccountType.getDisplay(i18n, workingacc.getType())));
                    } catch (DuplicateKeyException e) {
                        GUIs.alert(this, i18n.string(R.string.cmsg_error, e.getMessage()));
                        return false;
                    }

                }
            } else {
                Account oacc = ((AccountEditorDialog)dlg).getAccount();
                if (namedAcc != null && !namedAcc.getId().equals(oacc.getId())) {
                    GUIs.alert(
                            this,i18n.string(R.string.msg_account_existed, name,
                                    AccountType.getDisplay(i18n, namedAcc.getType())));
                    return false;
                } else {
                    idp.updateAccount(oacc.getId(),workingacc);
                    GUIs.shortToast(this, i18n.string(R.string.msg_account_updated, name,AccountType.getDisplay(i18n, oacc.getType())));
                }
                reloadData();
            }
            break;
        case R.id.acceditor_cancel:
            break;
        case R.id.acceditor_close:
            GUIs.shortToast(this,i18n.string(R.string.msg_created_account,dlg.getCounter()));
            reloadData();
            break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if(parent == listView){
            doEditAccount(pos);
        }
    }

}