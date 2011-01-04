package com.bottleworks.dailymoney;

import com.bottleworks.dailymoney.util.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TestActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Contexts.initialContext(savedInstanceState);
        setContentView(R.layout.test);
        
        initialListener();
    }

    private void initialListener() {
        findViewById(R.id.test_accountMgnt).setOnClickListener(this);
        findViewById(R.id.test_userMgnt).setOnClickListener(this);
        findViewById(R.id.test_addDetail).setOnClickListener(this);
        findViewById(R.id.test_listDetail).setOnClickListener(this);
        findViewById(R.id.test_updateDetail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        
        case R.id.test_accountMgnt:
            testAccountMgnt();
            break;
        case R.id.test_userMgnt:
            testUserMgnt();
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

    private void testUpdateDetail() {
        Logger.d("testUpdateDetail");
        
    }

    private void testListDatail() {
        Logger.d("testListDatail");
        
    }

    private void testAddDetail() {
        Logger.d("testAddDetail");
        
    }

    private void testUserMgnt() {
        Logger.d("testUserMgnt");
        
    }

    private void testAccountMgnt() {
        Logger.d("testAccountMgnt");
        startActivity(new Intent(this,AccountMgntActivity.class));
        
    }


}