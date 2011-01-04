package com.bottleworks.dailymoney;

import android.app.Activity;
import android.os.Bundle;

public class DailyMoneyActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context.initialContext(this);
        setContentView(R.layout.main);
    }
}