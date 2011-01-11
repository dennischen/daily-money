package com.bottleworks.dailymoney.ui;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.AccountMgntActivity;
import com.bottleworks.dailymoney.DataMaintenanceActivity;
import com.bottleworks.dailymoney.DetailEditorDialog;
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

    public MainDesktop(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        label = i18n.string(R.string.dt_main);
        icon = R.drawable.dt_main;

        DesktopItem adddetdt = new DesktopItem(new Runnable() {
            public void run() {
                Detail d = new Detail("", "", new Date(), 0D, "");
                DetailEditorDialog dlg = new DetailEditorDialog(context, new DetailEditorDialog.OnFinishListener() {
                    @Override
                    public boolean onFinish(DetailEditorDialog dlg, View v, Object data) {
                        switch (v.getId()) {
                        case R.id.deteditor_ok:
                            Detail dt = (Detail) data;
                            Contexts.instance().getDataProvider().newDetail(dt);
                            break;
                        case R.id.deteditor_close:
                            GUIs.shortToast(context, i18n.string(R.string.msg_created_detail, dlg.getCounter()));
                        }
                        return true;
                    }
                }, true, d);
                dlg.show();
            }
        }, i18n.string(R.string.dtitem_adddetail), R.drawable.dt_item_adddetail);

        Intent intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_WEEK);
        DesktopItem weeklist = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_detlist_week), R.drawable.dt_item_detail_week);

        intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_MONTH);
        DesktopItem monthlist = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_detlist_month), R.drawable.dt_item_detail_month);

        intent = new Intent(context, DetailListActivity.class);
        intent.putExtra(DetailListActivity.INTENT_MODE, DetailListActivity.MODE_YEAR);
        DesktopItem yearlist = new DesktopItem(new IntentRun(context, intent),
                i18n.string(R.string.dtitem_detlist_year), R.drawable.dt_item_detail_year);

        DesktopItem accmgntdt = new DesktopItem(new ActivityRun(context, AccountMgntActivity.class),
                i18n.string(R.string.dtitem_accmgnt), R.drawable.dt_item_account);
        
        DesktopItem datamaindt = new DesktopItem(new ActivityRun(context, DataMaintenanceActivity.class),
                i18n.string(R.string.dtitem_datamain), R.drawable.dt_item_datamain);
        
        DesktopItem prefdt = new DesktopItem(new ActivityRun(context, PrefsActivity.class),
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
