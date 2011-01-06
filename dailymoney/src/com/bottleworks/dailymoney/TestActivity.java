package com.bottleworks.dailymoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.DefaultDataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;

public class TestActivity extends ContextsActivity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initialListener();
        
        IDataProvider idp = Contexts.instance().getDataProvider();
        new DefaultDataCreator(idp,i18n).createDefaultAccounts();
    }

    private void initialListener() {
        findViewById(R.id.test_resetDataprovider).setOnClickListener(this);
        findViewById(R.id.test_createDefaultdata).setOnClickListener(this);
        findViewById(R.id.test_accountMgnt).setOnClickListener(this);
        findViewById(R.id.test_prefs).setOnClickListener(this);
        findViewById(R.id.test_addDetail).setOnClickListener(this);
        findViewById(R.id.test_listDetail).setOnClickListener(this);
        findViewById(R.id.test_updateDetail).setOnClickListener(this);
        
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.test_resetDataprovider:
            testResetDataprovider();            
            break;
        case R.id.test_createDefaultdata:
            testCreateDefaultdata();            
            break;
        case R.id.test_prefs:
            testPrefs();
            break;
        case R.id.test_accountMgnt:
            testAccountMgnt();
            break;
        case R.id.test_addDetail:
            testAddDetail();
            break;
        case R.id.test_listDetail:
            testListDatail();
            break;
        case R.id.test_updateDetail:
            testUpdateDetail();
            break;
            
        }
    }

    private void testResetDataprovider() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        idp.reset();
        GUIs.shortToast(this,"reset data provider");
    }
    
    private void testCreateDefaultdata() {
        IDataProvider idp = Contexts.instance().getDataProvider();
        new DefaultDataCreator(idp,i18n).createDefaultAccounts();
        GUIs.shortToast(this,"create default data");
    }

    private void testUpdateDetail() {
        
    }

    private void testListDatail() {
        Logger.d("testListDatail");
        
    }

    private void testAddDetail() {
        Logger.d("testAddDetail");
        
    }

    private void testAccountMgnt() {
        Logger.d("testAccountMgnt");
        
        startActivity(new Intent(this,AccountMgntActivity.class));
    }

    private void testPrefs() {
        Logger.d("testPrefs");
        
        startActivity(new Intent(this,PrefsActivity.class));
    }

}