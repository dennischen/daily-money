package com.bottleworks.dailymoney;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
/**
 * this activity manage the account with tab widgets of android, there are 4 type of account, income, outcome, asset and debt.
 * @author dennis
 *
 */
public class AccountMgntActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context.initialContext(this);
        setContentView(R.layout.accountmgnt);

        initialTab();
    }

    private void initialTab() {
        TabHost tabs = (TabHost)findViewById(R.id.accountmgnt_tabs);
        tabs.setup();
        
        TabSpec tab = tabs.newTabSpec("ts1");
        tab.setIndicator(getString(R.string.label_income));
        tab.setContent(R.id.accountmgnt_income);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts2");
        tab.setIndicator(getString(R.string.label_outcome));
        tab.setContent(R.id.accountmgnt_outcome);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts3");
        tab.setIndicator(getString(R.string.label_asset));
        tab.setContent(R.id.accountmgnt_asset);
        tabs.addTab(tab);
        
        tab = tabs.newTabSpec("ts4");
        tab.setIndicator(getString(R.string.label_debt));
        tab.setContent(R.id.accountmgnt_debt);
        tabs.addTab(tab);
        
    }
}