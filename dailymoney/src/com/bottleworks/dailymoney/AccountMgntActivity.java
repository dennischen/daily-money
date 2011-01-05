package com.bottleworks.dailymoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
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

import com.bottleworks.commons.ui.Contexts;
import com.bottleworks.commons.ui.ContextsActivity;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.IDialogFinishListener;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.DuplicateKeyException;
import com.bottleworks.dailymoney.data.IDataProvider;

/**
 * this activity manages the account (of detail) with tab widgets of android,
 * there are 4 type of account, income, expense, asset and debt.
 * 
 * @author dennis
 * @see {@link AccountType}
 */
public class AccountMgntActivity extends ContextsActivity implements OnTabChangeListener,OnItemClickListener, IDialogFinishListener {
    /** Called when the activity is first created. */
    private List<Account> listViewData = new ArrayList<Account>();
    private List<Map<String, Object>> listViewMapList = new ArrayList<Map<String, Object>>();

    private String lastTab = null;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);
        lastTab = tab.getTag();

        tab = tabs.newTabSpec(AccountType.EXPENSE.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        tab = tabs.newTabSpec(AccountType.ASSET.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        tab = tabs.newTabSpec(AccountType.DEBT.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        tab = tabs.newTabSpec(AccountType.OTHER.getType());
        tab.setIndicator(AccountType.getDisplay(i18n, tab.getTag()));
        tab.setContent(R.id.accmgnt_list);
        tabs.addTab(tab);

        // workaround, force refresh
        tabs.setCurrentTab(1);
        tabs.setCurrentTab(0);

        tabs.setOnTabChangedListener(this);

    }

    private static String[] bindingFrom = new String[] { "name", "initvalue", "id" };
    private static int[] bindingTo = new int[] { R.id.accmgnt_item_name, R.id.accmgnt_item_initvalue, R.id.accmgnt_item_id };

    private SimpleAdapter listViewAdapter;

    private void initialListView() {
        listViewAdapter = new SimpleAdapter(this, listViewMapList, R.layout.accmgnt_item, bindingFrom, bindingTo);
        listView = (ListView) findViewById(R.id.accmgnt_list);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        
        
    }

    private void loadData() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        listViewData = null;

        AccountType type = AccountType.find(lastTab);
        switch (type) {
        case INCOME:
            listViewData = idp.listAccount(AccountType.INCOME);
            break;
        case EXPENSE:
            listViewData = idp.listAccount(AccountType.EXPENSE);
            break;
        case ASSET:
            listViewData = idp.listAccount(AccountType.ASSET);
            break;
        case DEBT:
            listViewData = idp.listAccount(AccountType.DEBT);
            break;
        case OTHER:
            listViewData = idp.listAccount(AccountType.OTHER);
            break;
        default:
            listViewData = new ArrayList<Account>();
        }

        listViewMapList.clear();

        for (Account acc : listViewData) {
            Map<String, Object> row = new HashMap<String, Object>();
            listViewMapList.add(row);
            row.put(bindingFrom[0], acc.getName());
            row.put(bindingFrom[1], acc.getInitialValue());
            row.put(bindingFrom[2], acc.getId());
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
        loadData();
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
        Account acc = new Account("", lastTab, 0D);
        AccountEditorDialog dlg = new AccountEditorDialog(this, this, true, acc);
        dlg.show();
    }

    @Override
    public boolean onDialogFinish(Dialog dlg, View v, Object data) {
        switch (v.getId()) {
        case R.id.acceditor_ok:
            Account workingacc = ((AccountEditorDialog) dlg).getWorkingAccount();
            boolean modeCreate = ((AccountEditorDialog) dlg).isModeCreate();
            String name = workingacc.getName();
            IDataProvider idp = Contexts.instance().getDataProvider();
            Account namedAcc = idp.findAccountByNormalizedName(name);
            if (modeCreate) {
                if (namedAcc != null) {
                    GUIs.shortToast(
                            this,i18n.string(R.string.msg_account_existed, name,
                                    AccountType.getDisplay(i18n, namedAcc.getAccountType())));
                    return false;
                } else {
                    try {
                        idp.newAccount(workingacc);
                        GUIs.shortToast(this, i18n.string(R.string.msg_account_created, name,AccountType.getDisplay(i18n, workingacc.getAccountType())));
                    } catch (DuplicateKeyException e) {
                        GUIs.alert(this, i18n.string(R.string.cmsg_error, e.getMessage()));
                        return false;
                    }

                }
            } else {
                Account acc = ((AccountEditorDialog)dlg).getAccount();
                if (namedAcc != null && !namedAcc.getId().equals(acc.getId())) {
                    GUIs.shortToast(
                            this,i18n.string(R.string.msg_account_existed, name,
                                    AccountType.getDisplay(i18n, namedAcc.getAccountType())));
                    return false;
                } else {
                    idp.updateAccount(acc.getId(),workingacc);
                    GUIs.shortToast(this, i18n.string(R.string.msg_account_updated, name,AccountType.getDisplay(i18n, acc.getAccountType())));
                }
            }
            loadData();
            break;
        case R.id.acceditor_cancel:
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