package com.bottleworks.dailymoney;

import android.content.Context;
/**
 * 
 * @author dennis
 *
 */
public class ReportsDesktop extends AbstractDesktop {

    public ReportsDesktop(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_reports);
        icon = R.drawable.dt_reports;
    }

}
