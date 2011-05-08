package com.bottleworks.dailymoney.ui;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;

import com.bottleworks.dailymoney.core.R;
import com.bottleworks.dailymoney.data.Detail;
/**
 * 
 * @author dennis
 *
 */
public class MainDesktop extends AbstractDesktop {

    public MainDesktop(Activity activity) {
        super(activity);
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_main);
        icon = R.drawable.tab_main;

        DesktopItem adddetdt = new DesktopItem(new Runnable() {
            public void run() {
                Detail d = new Detail("", "", new Date(), 0D, "");
                Intent intent = null;
                intent = new Intent(activity,DetailEditorActivity.class);
                intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE,true);
                intent.putExtra(DetailEditorActivity.INTENT_DETAIL,d);
                activity.startActivityForResult(intent,Constants.REQUEST_DETAIL_EDITOR_CODE);
            }
        }, i18n.string(R.string.dtitem_adddetail), R.drawable.dtitem_adddetail,999);

        Intent intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_DAY);
        DesktopItem daylist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_day), R.drawable.dtitem_detail_day);
        
        intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_WEEK);
        DesktopItem weeklist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_week), R.drawable.dtitem_detail_week);

        intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_MONTH);
        DesktopItem monthlist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_month), R.drawable.dtitem_detail_month);

        intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_YEAR);
        DesktopItem yearlist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_year), R.drawable.dtitem_detail_year);

        DesktopItem accmgntdt = new DesktopItem(new ActivityRun(activity, AccountMgntActivity.class),
                i18n.string(R.string.dtitem_accmgnt), R.drawable.dtitem_account);
        
        DesktopItem datamaindt = new DesktopItem(new ActivityRun(activity, DataMaintenanceActivity.class),
                i18n.string(R.string.dtitem_datamain), R.drawable.dtitem_datamain);
        
        DesktopItem prefdt = new DesktopItem(new ActivityRun(activity, PrefsActivity.class),
                i18n.string(R.string.dtitem_prefs), R.drawable.dtitem_prefs);
        
        intent = new Intent(activity, LocalWebViewActivity.class);
        intent.putExtra(LocalWebViewActivity.INTENT_URI_ID, R.string.path_how2use);
        DesktopItem how2use = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_how2use),-1,0);
        how2use.setHidden(true);
        
        DesktopItem aboutdt = new DesktopItem(new ActivityRun(activity, AboutActivity.class),
                i18n.string(R.string.dtitem_about), R.drawable.dtitem_about,0);
        
        addItem(adddetdt);
        addItem(daylist);
        addItem(weeklist);
        addItem(monthlist);
        addItem(yearlist);
        addItem(accmgntdt);
        addItem(datamaindt);
        addItem(prefdt);
        
        addItem(how2use);
        
        addItem(aboutdt);
    }

}
