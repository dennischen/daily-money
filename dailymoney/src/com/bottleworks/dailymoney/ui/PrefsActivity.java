package com.bottleworks.dailymoney.ui;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.bottleworks.dailymoney.context.Contexts;
import com.bottleworks.dailymoney.core.R;
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
        setSummary(PreferenceManager.getDefaultSharedPreferences(this), Constants.PREFS_LAST_BACKUP);
    }
    
    protected void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        setSummary(PreferenceManager.getDefaultSharedPreferences(this), Constants.PREFS_LAST_BACKUP);
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

    /**
     * set specified preference value to summery
     * 
     * @param sharedPreferences
     * @param key
     */
    private void setSummary(SharedPreferences sharedPreferences, String key) {
        if (Constants.PREFS_LAST_BACKUP.equalsIgnoreCase(key)) {
            Preference p = findPreference(key);
            p.setSummary(sharedPreferences.getString(Constants.PREFS_LAST_BACKUP, "Unknown"));
        }
    }
}
