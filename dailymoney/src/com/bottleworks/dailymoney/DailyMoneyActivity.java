package com.bottleworks.dailymoney;

import android.os.Bundle;

import com.bottleworks.dailymoney.ui.ContextsActivity;
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