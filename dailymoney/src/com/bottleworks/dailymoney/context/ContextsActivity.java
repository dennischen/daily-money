package com.bottleworks.dailymoney.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bottleworks.commons.util.CalendarHelper;
import com.bottleworks.commons.util.I18N;
import com.bottleworks.commons.util.Logger;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.os.Bundle;

/**
 * provide life cycle and easy access to contexts
 * 
 * @author dennis
 * 
 */
public class ContextsActivity extends Activity {

    protected I18N i18n;
    protected CalendarHelper calHelper;
    private GoogleAnalyticsTracker tracker;
    private static final String ANALYTICS_CDOE = "UA-20850113-1"; // code of
                                                                  // daily money
                                                                  // in google
                                                                  // analytics
    private static final int ANALYTICS_DISPATH_DELAY = 60;// dispatch every 60s

    private static final ExecutorService trackSingleExecutor = Executors.newSingleThreadExecutor();
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Contexts ins = Contexts.uiInstance();
        ins.initContext(this);
        i18n = new I18N(this);
        calHelper = ins.getCalendarHelper();
        if (ins.isPrefAllowAnalytics()) {
            final String path = "/activity/" + getClass().getName();
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        tracker = GoogleAnalyticsTracker.getInstance();
                        tracker.start(ANALYTICS_CDOE, ANALYTICS_DISPATH_DELAY, ContextsActivity.this);
                        Logger.d("track "+path);
                        tracker.trackPageView(path);
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });

        }
    }

    protected void trackAnalyticsEvent(final String category,final String action,final String label,final int value) {
        if (tracker != null) {
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        if (tracker != null) {
                            tracker.trackEvent(category, action, label, value);
                        }
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });
        }
    }

    protected void trackAnalyticsPageView(final String path) {
        if (tracker != null) {
            trackSingleExecutor.submit(new Runnable() {
                public void run() {
                    try {
                        if (tracker != null) {
                            Logger.d("track "+path);
                            tracker.trackPageView(path);
                        }
                    } catch (Throwable t) {
                        Logger.e(t.getMessage(), t);
                    }
                }
            });
        }
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the tracker when it is no longer needed.
        try {
            if (tracker != null) {
                tracker.dispatch();
                tracker.stop();
                tracker = null;
            }
        } catch (Throwable t) {
            Logger.e(t.getMessage(), t);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contexts.uiInstance().initContext(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Contexts.uiInstance().cleanContext(this);
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


}
