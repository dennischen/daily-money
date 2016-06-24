package com.bottleworks.dailymoney.context;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;

/**
 * provide life cycle and easy access to contexts
 *
 * @author dennis
 */
public class ContextsActivity extends AppCompatActivity {

    private static Activity firstActivity;
    protected I18N i18n;
    protected CalendarHelper calHelper;
    Bundle fakeExtra;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Logger.d("activity created:" + this);
        Contexts ins = Contexts.instance();
        if (firstActivity == null) {
            firstActivity = this;
            ins.initApplication(firstActivity, firstActivity);
        }

        ins.initActivity(this);
        refreshUtil(ins);
        ins.trackPageView(getTrackerPath());
    }

    private void refreshUtil(Contexts ins) {
        i18n = ins.getI18n();
        calHelper = ins.getCalendarHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firstActivity == this) {
            Contexts.instance().destroyApplication(firstActivity);
        }
        Logger.d("activity destroyed:" + this);
    }

    @SuppressWarnings("rawtypes")
    private String getTrackerPath() {
        Class clz = getClass();
        String name = clz.getSimpleName();
        String pkg = clz.getPackage() == null ? "" : clz.getPackage().getName();
        StringBuilder sb = new StringBuilder("/a/");
        int i;
        if ((i = pkg.lastIndexOf('.')) != -1) {
            pkg = pkg.substring(i + 1);
        }
        sb.append(pkg).append(".").append(name);
        return sb.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contexts ins = Contexts.instance();
        ins.initActivity(this);
        refreshUtil(ins);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Contexts.instance().cleanActivity(this);
    }

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

    protected Contexts getContexts() {
        return Contexts.instance();
    }

}
