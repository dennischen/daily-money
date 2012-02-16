/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * the source is come from andorid - 2.1 calculator2
 */
package com.bottleworks.dailymoney.calculator2;

import com.bottleworks.dailymoney.core.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Config;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Calculator extends Activity implements OnClickListener {
    
    public static final String INTENT_START_VALUE = "cal2_startValue";
    public static final String INTENT_NEED_RESULT = "cal2_needResult";
    public static final String INTENT_RESULT_VALUE = "cal2_resultValue";
    
    
    EventListener mListener = new EventListener();
    private CalculatorDisplay mDisplay;
    private Persist mPersist;
    private History mHistory;
    private Logic mLogic;
    private PanelSwitcher mPanelSwitcher;

    private static final int CMD_CLEAR_HISTORY  = 1;
    private static final int CMD_BASIC_PANEL    = 2;
    private static final int CMD_ADVANCED_PANEL = 3;

    private static final int HVGA_HEIGHT_PIXELS = 480;
    private static final int HVGA_WIDTH_PIXELS  = 320;

    static final int BASIC_PANEL    = 0;
    static final int ADVANCED_PANEL = 1;

    private static final String LOG_TAG = "Calculator";
    private static final boolean DEBUG  = false;
    private static final boolean LOG_ENABLED = DEBUG ? Config.LOGD : Config.LOGV;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.cal2_main);

        mPersist = new Persist(this);
        mHistory = mPersist.history;

        mDisplay = (CalculatorDisplay) findViewById(R.id.cal2_display);

        mLogic = new Logic(this, mHistory, mDisplay, (Button) findViewById(R.id.cal2_equal));
        HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory, mLogic);
        mHistory.setObserver(historyAdapter);
        View view;
        mPanelSwitcher = (PanelSwitcher) findViewById(R.id.cal2_panelswitch);
                                       
        mListener.setHandler(mLogic, mPanelSwitcher);

        mDisplay.setOnKeyListener(mListener);


        if ((view = findViewById(R.id.cal2_del)) != null) {
            view.setOnLongClickListener(mListener);
        }
        
        
        
        /**modify by dennis, provide initial value  **/
        boolean needresult = getIntent().getExtras().getBoolean(INTENT_NEED_RESULT,false);
        String startValue = getIntent().getExtras().getString(INTENT_START_VALUE);
        
        if(startValue!=null){
            mLogic.setNumbericResult(startValue);
        }
        
        if(needresult){
            findViewById(R.id.cal2_span).setVisibility(View.GONE);
            findViewById(R.id.cal2_ok).setOnClickListener(this);
            findViewById(R.id.cal2_close).setOnClickListener(this);
        }else{
            findViewById(R.id.cal2_ok).setVisibility(View.GONE);
            findViewById(R.id.cal2_close).setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item;
        
        item = menu.add(0, CMD_CLEAR_HISTORY, 0, R.string.cal2_clear_history);
        item.setIcon(R.drawable.cal2_clear_history);
        
        item = menu.add(0, CMD_ADVANCED_PANEL, 0, R.string.cal2_advanced);
        item.setIcon(R.drawable.cal2_advanced);
        
        item = menu.add(0, CMD_BASIC_PANEL, 0, R.string.cal2_basic);
        item.setIcon(R.drawable.cal2_simple);

        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(CMD_BASIC_PANEL).setVisible(mPanelSwitcher != null && 
                          mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL);
        
        menu.findItem(CMD_ADVANCED_PANEL).setVisible(mPanelSwitcher != null && 
                          mPanelSwitcher.getCurrentIndex() == BASIC_PANEL);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case CMD_CLEAR_HISTORY:
            mHistory.clear();
            break;

        case CMD_BASIC_PANEL:
            if (mPanelSwitcher != null && 
                mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL) {
                mPanelSwitcher.moveRight();
            }
            break;

        case CMD_ADVANCED_PANEL:
            if (mPanelSwitcher != null && 
                mPanelSwitcher.getCurrentIndex() == BASIC_PANEL) {
                mPanelSwitcher.moveLeft();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        // as work-around for ClassCastException in TextView on restart
        // avoid calling superclass, to keep icicle empty
    }

    @Override
    public void onPause() {
        super.onPause();
        mLogic.updateHistory();
        mPersist.save();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mPanelSwitcher.getCurrentIndex() == ADVANCED_PANEL) {
            mPanelSwitcher.moveRight();
            return true;
        } else {
            return super.onKeyDown(keyCode, keyEvent);
        }
    }

    static void log(String message) {
        if (LOG_ENABLED) {
            Log.v(LOG_TAG, message);
        }
    }

    /**
     * The font sizes in the layout files are specified for a HVGA display.
     * Adjust the font sizes accordingly if we are running on a different
     * display.
     */
    public void adjustFontSize(TextView view) {
        float fontPixelSize = view.getTextSize();
        Display display = getWindowManager().getDefaultDisplay();
        int h = Math.min(display.getWidth(), display.getHeight());
        float ratio = 0; 
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            ratio = (float)h/HVGA_WIDTH_PIXELS;
        }else{
            ratio = (float)h/HVGA_HEIGHT_PIXELS;
        }
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixelSize*ratio);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cal2_ok) {
            String result = mLogic.getNumbericResult();
            Intent intent = new Intent();
            intent.putExtra(INTENT_RESULT_VALUE,result);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.cal2_close) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
