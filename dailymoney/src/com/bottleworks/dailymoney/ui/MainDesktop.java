package com.bottleworks.dailymoney.ui;

import android.content.Context;
import android.content.Intent;

import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Detail;

import java.util.Date;

/**
 * @author dennis
 */
public class MainDesktop extends AbstractDesktop {

    public MainDesktop() {
        label = i18n.string(R.string.dt_main);
        icon = R.drawable.tab_main;
    }

    @Override
    protected void init(final Context context) {
        DesktopItem adddetdt = new DesktopItem(new Runnable() {
            public void run() {
                Detail d = new Detail("", "", new Date(), 0D, "");
                Intent intent = null;
                intent = new Intent(context, DetailEditorActivity.class);
                intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE, true);
                intent.putExtra(DetailEditorActivity.INTENT_DETAIL, d);
                MainDesktop.this.getActivity().startActivityForResult(intent, Constants.REQUEST_DETAIL_EDITOR_CODE);
            }
        }, i18n.string(R.string.dtitem_adddetail), R.drawable.dtitem_adddetail, 999);

        Intent intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_DAY);
        String title = i18n.string(R.string.dtitem_detlist_day);
        intent.putExtra(DetailListActivity.INTENT_MODE_TITLE, title);
        DesktopItem daylist = new DesktopItem(new IntentRun(context, intent),
                title, R.drawable.dtitem_detail_day);

        intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_WEEK);
        title = i18n.string(R.string.dtitem_detlist_week);
        intent.putExtra(DetailListActivity.INTENT_MODE_TITLE, title);
        DesktopItem weeklist = new DesktopItem(new IntentRun(context, intent),
                title, R.drawable.dtitem_detail_week);

        intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_MONTH);
        title = i18n.string(R.string.dtitem_detlist_month);
        intent.putExtra(DetailListActivity.INTENT_MODE_TITLE, title);
        DesktopItem monthlist = new DesktopItem(new IntentRun(context, intent),
                title, R.drawable.dtitem_detail_month);

        intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_YEAR);
        title = i18n.string(R.string.dtitem_detlist_year);
        intent.putExtra(DetailListActivity.INTENT_MODE_TITLE, title);
        DesktopItem yearlist = new DesktopItem(new IntentRun(context, intent),
                title, R.drawable.dtitem_detail_year);

        DesktopItem accmgntdt = new DesktopItem(new ActivityRun(context, AccountMgntActivity.class),
                i18n.string(R.string.dtitem_accmgnt), R.drawable.dtitem_account);

        DesktopItem bookmgntdt = new DesktopItem(new ActivityRun(context, BookMgntActivity.class),
                i18n.string(R.string.dtitem_books), R.drawable.dtitem_books);

        DesktopItem datamaindt = new DesktopItem(new ActivityRun(context, DataMaintenanceActivity.class),
                i18n.string(R.string.dtitem_datamain), R.drawable.dtitem_datamain);

        DesktopItem prefdt = new DesktopItem(new ActivityRun(context, PrefsActivity.class),
                i18n.string(R.string.dtitem_prefs), R.drawable.dtitem_prefs);

        intent = new Intent(context, LocalWebViewActivity.class);
        intent.putExtra(LocalWebViewActivity.INTENT_URI_ID, R.string.path_how2use);
        DesktopItem how2use = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_how2use), -1, 0);
        how2use.setHidden(true);

        DesktopItem aboutdt = new DesktopItem(new ActivityRun(context, AboutActivity.class),
                i18n.string(R.string.dtitem_about), R.drawable.dtitem_about, 0);

        addItem(adddetdt);
        addItem(daylist);
        addItem(weeklist);
        addItem(monthlist);
        addItem(yearlist);
        addItem(accmgntdt);
        addItem(datamaindt);
        addItem(prefdt);
        addItem(bookmgntdt);

        addItem(how2use);

        addItem(aboutdt);
    }

}
