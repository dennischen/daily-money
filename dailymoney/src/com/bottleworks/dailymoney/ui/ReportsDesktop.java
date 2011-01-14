package com.bottleworks.dailymoney.ui;

import android.app.Activity;
import android.content.Intent;

import com.bottleworks.dailymoney.R;
import com.bottleworks.dailymoney.reports.BalanceActivity;
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
        
        Intent intent = new Intent(activity, BalanceActivity.class);
        DesktopItem balance = new DesktopItem(new IntentRun(activity, intent),
                "Balance", R.drawable.dt_item_detail_week);
        
        addItem(balance);
    }

}
