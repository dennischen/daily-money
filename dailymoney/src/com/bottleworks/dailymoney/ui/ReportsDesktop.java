package com.bottleworks.dailymoney.ui;

import android.app.Activity;

import com.bottleworks.dailymoney.R;
/**
 * 
 * @author dennis
 *
 */
public class ReportsDesktop extends AbstractDesktop {

    public ReportsDesktop(Activity activity) {
        super(activity);
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_reports);
        icon = R.drawable.dt_reports;
    }

}
