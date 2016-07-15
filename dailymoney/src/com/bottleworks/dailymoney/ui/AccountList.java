package com.bottleworks.dailymoney.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.IDataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountList extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TYPE_KEY = "type";
    private static final String[] bindingFrom = new String[]{"name", "initvalue", "id"};
    private static final int[] bindingTo = new int[]{R.id.accmgnt_item_name, R.id.accmgnt_item_initvalue, R.id.accmgnt_item_id};

    private List<Account> listViewData = new ArrayList<>();
    private List<Map<String, Object>> listViewMapList = new ArrayList<>();
    private SimpleAdapter listViewAdapter;
    private ListView listView;

    public static AccountList newInstance(AccountType accountType) {
        AccountList accountList = new AccountList();
        Bundle args = new Bundle();
        args.putSerializable(TYPE_KEY, accountType);
        accountList.setArguments(args);
        return accountList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.accmgnt_fragment, container, false);

        this.listViewAdapter = new SimpleAdapter(this.getContext(), listViewMapList, R.layout.accmgnt_item, bindingFrom, bindingTo);
        this.listViewAdapter.setViewBinder(new AccountListViewBinder());

        this.listView = (ListView) fragment.findViewById(R.id.accmgnt_list);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setOnItemClickListener(this);

        this.registerForContextMenu(this.listView);
        this.reloadData();

        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if (parent == this.listView) {
            doEditAccount(pos);
        }
    }

    public CharSequence getLabel(I18N i18n) {
        AccountType accountType = (AccountType) this.getArguments().getSerializable("type");
        return accountType.getDisplay(i18n);
    }

    public void reloadData() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        listViewData = null;

        AccountType type = (AccountType) this.getArguments().getSerializable("type");
        listViewData = idp.listAccount(type);
        listViewMapList.clear();

        for (Account acc : listViewData) {
            Map<String, Object> row = new HashMap<>();
            listViewMapList.add(row);
            AccountType accountType = AccountType.find(acc.getType());
            row.put(bindingFrom[0], new NamedItem(bindingFrom[0], accountType, acc.getName()));
            row.put(bindingFrom[1], new NamedItem(bindingFrom[1], accountType, Formats.double2String(acc.getInitialValue())));
            row.put(bindingFrom[2], new NamedItem(bindingFrom[2], accountType, acc.getId()));
        }

        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.accmgnt_list) {
            this.getActivity().getMenuInflater().inflate(R.menu.accmgnt_ctxmenu, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.accmgnt_menu_edit) {
            doEditAccount(info.position);
            return true;
        } else if (item.getItemId() == R.id.accmgnt_menu_delete) {
//            doDeleteAccount(info.position);
            return true;
        } else if (item.getItemId() == R.id.accmgnt_menu_copy) {
//            doCopyAccount(info.position);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void doEditAccount(int pos) {
        Account acc = this.listViewData.get(pos);
        Intent intent = new Intent(this.getContext(), AccountEditorActivity.class);
        intent.putExtra(AccountEditorActivity.INTENT_MODE_CREATE, false);
        intent.putExtra(AccountEditorActivity.INTENT_ACCOUNT, acc);
        this.startActivityForResult(intent, Constants.REQUEST_ACCOUNT_EDITOR_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ACCOUNT_EDITOR_CODE && resultCode == Activity.RESULT_OK) {
            GUIs.delayPost(new Runnable() {
                @Override
                public void run() {
                    reloadData();
                }
            });
        }
    }

    private class AccountListViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            NamedItem item = (NamedItem) data;
            AccountType at = (AccountType) item.getValue();

            TextView tv = (TextView) view;
            tv.setTextColor(getResources().getColor(at.getColor()));

            String name = item.getName();
            if (name.equals(bindingFrom[1])) {
                text = Contexts.instance().getI18n().string(R.string.label_initial_value) + " : " + text;
            }

            tv.setText(text);
            return true;
        }
    }

}
