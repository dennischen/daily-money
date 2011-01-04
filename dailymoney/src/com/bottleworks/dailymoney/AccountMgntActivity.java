package com.bottleworks.dailymoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.util.Logger;
/**
 * this activity manages the account (of detail) with tab widgets of android, 
 * there are 4 type of account, income, outcome, asset and debt. 
 * @author dennis
 * @see {@link AccountType}
 */
public class AccountMgntActivity extends Activity implements OnTabChangeListener{
    /** Called when the activity is first created. */
    
    List<Account> incomeList;
    List<Account> outcomeList;
    List<Account> assetList;
    List<Account> debtList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context.initialContext(savedInstanceState);
        setContentView(R.layout.accmgnt);
        initialTab();
        loadDataToView();
    }



    private void initialTab() {
        TabHost tabs = (TabHost)findViewById(R.id.accmgnt_tabs);
        tabs.setup();
        
        TabSpec tab = tabs.newTabSpec("ts1");
        tab.setIndicator(getString(R.string.label_income));
        tab.setContent(R.id.accmgnt_income);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts2");
        tab.setIndicator(getString(R.string.label_outcome));
        tab.setContent(R.id.accmgnt_outcome);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts3");
        tab.setIndicator(getString(R.string.label_asset));
        tab.setContent(R.id.accmgnt_asset);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts4");
        tab.setIndicator(getString(R.string.label_debt));
        tab.setContent(R.id.accmgnt_debt);
        tabs.addTab(tab);
        
        tabs.setOnTabChangedListener(this);
        
    }
    
    private void loadDataToView() {
        IDataProvider idp = Context.instance().getDataProvider();
        incomeList = idp.listAccount(AccountType.INCOME);
        outcomeList = idp.listAccount(AccountType.OUTCOME);
        assetList = idp.listAccount(AccountType.ASSET);
        debtList = idp.listAccount(AccountType.DEBT);
        
        loadDataToView((ListView)findViewById(R.id.accmgnt_income),incomeList);
        loadDataToView((ListView)findViewById(R.id.accmgnt_outcome),outcomeList);
        loadDataToView((ListView)findViewById(R.id.accmgnt_asset),assetList);
        loadDataToView((ListView)findViewById(R.id.accmgnt_debt),debtList);
    }

    private static String[] bindingFrom = new String[]{"display","initvalue"};
    private static int[] bindingTo = new int[]{R.id.accmgnt_item_display,R.id.accmgnt_item_initvalue};
    
    private void loadDataToView(ListView view, List<Account> list) {

        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>(list.size());
        for(Account acc:list){
            Map<String,Object> row = new HashMap<String,Object>();
            data.add(row);
            row.put(bindingFrom[0],acc.getDisplay());
            row.put(bindingFrom[1],acc.getInitialValue());
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.accmgnt_item, bindingFrom, bindingTo);
        view.setAdapter(adapter);
    }



    @Override
    public void onTabChanged(String tabId) {
        Logger.d("switch to tab "+tabId);
    }
}