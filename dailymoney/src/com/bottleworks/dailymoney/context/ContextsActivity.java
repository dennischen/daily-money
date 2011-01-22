package com.bottleworks.dailymoney.context;


import com.bottleworks.commons.util.CalendarHelper;
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
    protected CalendarHelper calHelper;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Contexts.uiInstance().initContext(this);
        i18n = new I18N(this);
        calHelper = Contexts.uiInstance().getCalendarHelper();
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
    
    
    Bundle fakeExtra;
    protected Bundle getIntentExtras(){
        if(getIntent()!=null && getIntent().getExtras()!=null){
            return getIntent().getExtras();
        }
        //if extra is null;
        if(fakeExtra==null){
            fakeExtra = new Bundle();
        }
        return fakeExtra;
    }
}
