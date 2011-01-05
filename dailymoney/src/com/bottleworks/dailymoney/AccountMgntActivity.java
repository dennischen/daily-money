package com.bottleworks.dailymoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.util.GUIs;
import com.bottleworks.dailymoney.util.I18N;
import com.bottleworks.dailymoney.util.Logger;

/**
 * this activity manages the account (of detail) with tab widgets of android,
 * there are 4 type of account, income, outcome, asset and debt.
 * 
 * @author dennis
 * @see {@link AccountType}
 */
public class AccountMgntActivity extends Activity implements OnTabChangeListener {
    /** Called when the activity is first created. */
    private List<Account> listViewData = new ArrayList<Account>();
    private List<Map<String, Object>> listViewMap = new ArrayList<Map<String, Object>>();

    private String lastTab = null;
    private I18N i18n;
    
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i18n = new I18N(this);
        Contexts.initialContext(savedInstanceState);
        setContentView(R.layout.accmgnt);
        initialTab();
        initialListView();
        loadData();
    }

    private void initialTab() {
        TabHost tabs = (TabHost) findViewById(R.id.accmgnt_tabs);
        tabs.setup();

        TabSpec tab = tabs.newTabSpec(AccountType.INCOME.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        // tab.setContent(R.id.accmgnt_list_income);
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);
        lastTab = tab.getTag();

        tab = tabs.newTabSpec(AccountType.OUTCOME.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        // tab.setContent(R.id.accmgnt_list_outcome);
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        tab = tabs.newTabSpec(AccountType.ASSET.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        // tab.setContent(R.id.accmgnt_list_asset);
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        tab = tabs.newTabSpec(AccountType.DEBT.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        // tab.setContent(R.id.accmgnt_list_debt);
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec(AccountType.OTHER.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        // tab.setContent(R.id.accmgnt_list_debt);
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        // workaround, force refresh
        tabs.setCurrentTab(1);
        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(this);

    }

    private static String[] bindingFrom = new String[] { "name", "initvalue" };
    private static int[] bindingTo = new int[] { R.id.accmgnt_item_name, R.id.accmgnt_item_initvalue };

    private SimpleAdapter listViewAdapter;

    private void initialListView() {
        listViewAdapter = new SimpleAdapter(this, listViewMap, R.layout.accmgnt_item, bindingFrom, bindingTo);
        listView = (ListView) findViewById(R.id.accmgnt_list);
        listView.setAdapter(listViewAdapter);
        registerForContextMenu(listView);
    }

    private void loadData() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        // incomeList = idp.listAccount(AccountType.INCOME);
        // outcomeList = idp.listAccount(AccountType.OUTCOME);
        // assetList = idp.listAccount(AccountType.ASSET);
        // debtList = idp.listAccount(AccountType.DEBT);
        //
        // loadData((ListView)findViewById(R.id.accmgnt_list_income),incomeList);
        // loadData((ListView)findViewById(R.id.accmgnt_list_outcome),outcomeList);
        // loadData((ListView)findViewById(R.id.accmgnt_list_asset),assetList);
        // loadData((ListView)findViewById(R.id.accmgnt_list_debt),debtList);

        listViewData = null;

        AccountType type = AccountType.find(lastTab);
        switch (type) {
        case INCOME:
            listViewData = idp.listAccount(AccountType.INCOME);
            break;
        case OUTCOME:
            listViewData = idp.listAccount(AccountType.OUTCOME);
            break;
        case ASSET:
            listViewData = idp.listAccount(AccountType.ASSET);
            break;
        case DEBT:
            listViewData = idp.listAccount(AccountType.DEBT);
            break;
        default:
            listViewData = new ArrayList<Account>();
        }

        listViewMap.clear();

        for (Account acc : listViewData) {
            Map<String, Object> row = new HashMap<String, Object>();
            listViewMap.add(row);
            row.put(bindingFrom[0], acc.getName());
            row.put(bindingFrom[1], acc.getInitialValue());
        }

        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabChanged(String tabId) {
        Logger.d("switch to tab : " + tabId);
        lastTab = tabId;
        loadData();
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
        Logger.d("context menu selected :" + item.getItemId()+", pos "+info.position);
        switch (item.getItemId()) {
        case R.id.accmgnt_menu_edit:
            doEditAccount(info.position);
            return true;
        case R.id.accmgnt_menu_delete:
            doDeleteAccount(info.position);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    private void doDeleteAccount(int pos) {
        Account acc = (Account)listViewData.get(pos);
        String name = acc.getName();
        
        Contexts.instance().getDataProvider().deleteAccount(acc);
        loadData();
        GUIs.shortToast(this,i18n.string(R.string.msg_account_deleted,name));
        
    }

    private void doEditAccount(int pos) {
        Account acc = (Account)listViewData.get(pos);
        String name = acc.getName();
        
        GUIs.shortToast(this,i18n.string(R.string.msg_account_updated,name));
    }

    private void doNewAccount() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        Account acc = new Account(AccountType.INCOME.getType(), "new-income", 11D);
        String name = acc.getName();
        try {
            idp.newAccount(acc);
            loadData();
            GUIs.shortToast(this,i18n.string(R.string.msg_account_created,name));
        } catch (DuplicateKeyException e) {
            String msg = i18n.string(R.string.msg_duplicate_account,
                    AccountType.getDisplay(i18n, acc.getAccountType()), acc.getName());
            GUIs.alert(this, msg);
        }
    }
}