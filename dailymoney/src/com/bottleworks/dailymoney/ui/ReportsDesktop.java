package com.bottleworks.dailymoney.ui;

import android.content.Context;
import android.content.Intent;

import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.ui.report.BalanceActivity;
import com.bottleworks.dailymoney.ui.report.SearchActivity;

/**
 * @author dennis
 */
public class ReportsDesktop extends AbstractDesktop {

    public ReportsDesktop() {
        label = i18n.string(R.string.dt_reports);
        icon = R.drawable.tab_reports;
    }

    @Override
    protected void init(Context context) {
        Intent intent;

        intent = new Intent(context, BalanceActivity.class);
        intent.putExtra(BalanceActivity.INTENT_TOTAL_MODE, false);
        intent.putExtra(BalanceActivity.INTENT_MODE, BalanceActivity.MODE_MONTH);
        DesktopItem monthBalance = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_report_monthly_balance), R.drawable.dtitem_balance_month);
        addItem(monthBalance);

        intent = new Intent(context, BalanceActivity.class);
        intent.putExtra(BalanceActivity.INTENT_TOTAL_MODE, false);
        intent.putExtra(BalanceActivity.INTENT_MODE, BalanceActivity.MODE_YEAR);
        DesktopItem yearBalance = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_report_yearly_balance), R.drawable.dtitem_balance_year);
        addItem(yearBalance);

        intent = new Intent(context, BalanceActivity.class);
        intent.putExtra(BalanceActivity.INTENT_TOTAL_MODE, true);
        intent.putExtra(BalanceActivity.INTENT_MODE, BalanceActivity.MODE_MONTH);
        DesktopItem totalBalance = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_report_cumulative_balance), R.drawable.dtitem_balance_cumulative_month, 99);
        addItem(totalBalance);

        intent = new Intent(context, SearchActivity.class);
        DesktopItem search = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_report_search), R.drawable.dtitem_account, 99);
        addItem(search);
    }

}
