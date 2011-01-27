package com.bottleworks.dailymoney.ui;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.context.Contexts;
/**
 * 
 * @author dennis
 *
 */
public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    boolean dirty = false;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.prefs);
    }
    
    protected void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }
    
    protected void onPause(){
        super.onPause();
        if(dirty){
            Contexts.instance().setPreferenceDirty();
        }
        dirty = false;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        dirty = true;
    }
}
