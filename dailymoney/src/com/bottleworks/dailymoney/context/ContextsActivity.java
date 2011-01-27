package com.bottleworks.dailymoney.context;

import android.app.Activity;
import android.os.Bundle;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;

/**
 * provide life cycle and easy access to contexts
 * 
 * @author dennis
 * 
 */
public class ContextsActivity extends Activity {

    protected I18N i18n;
    protected CalendarHelper calHelper;
    
    private static Activity firstActivity;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        Contexts ins = Contexts.instance();
        if(firstActivity==null){
            firstActivity = this;
            ins.initApplication(firstActivity, firstActivity);
        }
        
        ins.initActivity(this);
        i18n = ins.getI18n();
        calHelper = ins.getCalendarHelper();
        ins.trackPageView(getTrackerPath());
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(firstActivity==this){
            Contexts.instance().destroyApplication(firstActivity);
        }
    }

    @SuppressWarnings("rawtypes")
    private String getTrackerPath() {
        Class clz = getClass();
        String name = clz.getSimpleName();
        String pkg = clz.getPackage()==null?"":clz.getPackage().getName();
        StringBuilder sb = new StringBuilder("/a/");
        int i;
        if((i = pkg.lastIndexOf('.')) !=-1){
            pkg = pkg.substring(i+1); 
        }
        sb.append(pkg).append(".").append(name);
        return sb.toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Contexts.instance().initActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Contexts.instance().cleanActivity(this);
    }

    Bundle fakeExtra;

    protected Bundle getIntentExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            return getIntent().getExtras();
        }
        // if extra is null;
        if (fakeExtra == null) {
            fakeExtra = new Bundle();
        }
        return fakeExtra;
    }

    protected Contexts getContexts(){
        return Contexts.instance();
    }

}
