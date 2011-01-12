package com.bottleworks.dailymoney;

import com.bottleworks.dailymoney.context.ContextsActivity;

import android.os.Bundle;

/**
 * 
 * @author dennis
 *
 */
public class DailyMoneyActivity extends ContextsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}