package com.bottleworks.dailymoney;

import android.os.Bundle;

import com.bottleworks.dailymoney.ui.ContextsActivity;

public class DailyMoneyActivity extends ContextsActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}