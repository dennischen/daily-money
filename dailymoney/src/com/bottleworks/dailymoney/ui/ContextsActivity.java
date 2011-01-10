package com.bottleworks.dailymoney.ui;


import com.bottleworks.commons.util.I18N;

import android.app.Activity;
import android.os.Bundle;

/**
 * provide life cyce and easy access to contexts
 * @author dennis
 *
 */
public class ContextsActivity extends Activity {
    
    
    protected I18N i18n;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        i18n = Contexts.instance().initContext(this).getI18n();
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        i18n = Contexts.instance().initContext(this).getI18n();
    }
    
    @Override
    protected void onPause(){
        super.onPause();
    }
}
