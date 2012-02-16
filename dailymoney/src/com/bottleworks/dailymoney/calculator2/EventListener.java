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

import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;

class EventListener implements View.OnKeyListener, 
                               View.OnClickListener, 
                               View.OnLongClickListener {
    Logic mHandler;
    PanelSwitcher mPanelSwitcher;
    
    void setHandler(Logic handler, PanelSwitcher panelSwitcher) {
        mHandler = handler;
        mPanelSwitcher = panelSwitcher;
    }
    
    //@Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cal2_del) {
            mHandler.onDelete();
        } else if (id == R.id.cal2_equal) {
            mHandler.onEnter();
        } else {
            if (view instanceof Button) {
                String text = ((Button) view).getText().toString();
                if (text.length() >= 2) {
                    // add paren after sin, cos, ln, etc. from buttons
                    text += '(';
                }
                mHandler.insert(text);
                if (mPanelSwitcher != null && 
                    mPanelSwitcher.getCurrentIndex() == Calculator.ADVANCED_PANEL) {
                    mPanelSwitcher.moveRight();
                }                    
            }
        }
    }

    //@Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        if (id == R.id.cal2_del) {
            mHandler.onClear();
            return true;
        }
        return false;
    }
    
    private static final char[] EQUAL = {'='};

    //@Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        int action = keyEvent.getAction();
        
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean eat = mHandler.eatHorizontalMove(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
            return eat;
        }

        //Work-around for spurious key event from IME, bug #1639445
        if (action == KeyEvent.ACTION_MULTIPLE && keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            return true; // eat it
        }

        //Calculator.log("KEY " + keyCode + "; " + action);
        
        if (keyEvent.getMatch(EQUAL, keyEvent.getMetaState()) == '=') {
            if (action == KeyEvent.ACTION_UP) {
                mHandler.onEnter();
            }
            return true;
        }

        if (keyCode != KeyEvent.KEYCODE_DPAD_CENTER &&
            keyCode != KeyEvent.KEYCODE_DPAD_UP &&
            keyCode != KeyEvent.KEYCODE_DPAD_DOWN &&
            keyCode != KeyEvent.KEYCODE_ENTER) {
            return false;
        }

        /* 
           We should act on KeyEvent.ACTION_DOWN, but strangely
           sometimes the DOWN event isn't received, only the UP.
           So the workaround is to act on UP...
           http://b/issue?id=1022478
         */

        if (action == KeyEvent.ACTION_UP) {
            switch (keyCode) {                
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mHandler.onEnter();
                break;
                
            case KeyEvent.KEYCODE_DPAD_UP:
                mHandler.onUp();
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:            
                mHandler.onDown();
                break;
            }
        }
        return true;
    }
}
