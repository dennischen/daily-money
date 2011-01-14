package com.bottleworks.dailymoney.ui;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;

import com.bottleworks.dailymoney.AccountMgntActivity;
import com.bottleworks.dailymoney.Constants;
import com.bottleworks.dailymoney.DataMaintenanceActivity;
import com.bottleworks.dailymoney.DetailEditorActivity;
import com.bottleworks.dailymoney.DetailListActivity;
import com.bottleworks.dailymoney.PrefsActivity;
import com.bottleworks.dailymoney.R;
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
        icon = R.drawable.dt_main;

        DesktopItem adddetdt = new DesktopItem(new Runnable() {
            public void run() {
                Detail d = new Detail("", "", new Date(), 0D, "");
                Intent intent = null;
                intent = new Intent(activity,DetailEditorActivity.class);
                intent.putExtra(DetailEditorActivity.INTENT_MODE_CREATE,true);
                intent.putExtra(DetailEditorActivity.INTENT_DETAIL,d);
                activity.startActivityForResult(intent,Constants.REQUEST_DETAIL_EDITOR_CODE);
            }
        }, i18n.string(R.string.dtitem_adddetail), R.drawable.dt_item_adddetail);

        Intent intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_WEEK);
        DesktopItem weeklist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_week), R.drawable.dt_item_detail_week);

        intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_MONTH);
        DesktopItem monthlist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_month), R.drawable.dt_item_detail_month);

        intent = new Intent(activity, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_YEAR);
        DesktopItem yearlist = new DesktopItem(new IntentRun(activity, intent),
                i18n.string(R.string.dtitem_detlist_year), R.drawable.dt_item_detail_year);

        DesktopItem accmgntdt = new DesktopItem(new ActivityRun(activity, AccountMgntActivity.class),
                i18n.string(R.string.dtitem_accmgnt), R.drawable.dt_item_account);
        
        DesktopItem datamaindt = new DesktopItem(new ActivityRun(activity, DataMaintenanceActivity.class),
                i18n.string(R.string.dtitem_datamain), R.drawable.dt_item_datamain);
        
        DesktopItem prefdt = new DesktopItem(new ActivityRun(activity, PrefsActivity.class),
                i18n.string(R.string.dtitem_prefs), R.drawable.dt_item_prefs);
        

        addItem(adddetdt);
        addItem(weeklist);
        addItem(monthlist);
        addItem(yearlist);
        addItem(accmgntdt);
        addItem(datamaindt);
        addItem(prefdt);
    }

}
