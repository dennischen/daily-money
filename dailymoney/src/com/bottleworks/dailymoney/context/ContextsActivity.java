package com.bottleworks.dailymoney.context;


import com.bottleworks.commons.util.I18N;

import android.app.Activity;
import android.os.Bundle;

/**
 * provide life cycle and easy access to contexts
 * @author dennis
 *
 */
public class ContextsActivity extends Activity {
    
    
    protected I18N i18n;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Contexts.uiInstance().initContext(this);
        i18n = new I18N(this);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        Contexts.uiInstance().initContext(this);
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        Contexts.uiInstance().cleanContext(this);
    }
}
