package com.bottleworks.dailymoney;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bottleworks.commons.util.GUIs;
import com.bottleworks.dailymoney.data.DataCreator;
import com.bottleworks.dailymoney.data.IDataProvider;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.ContextsActivity;

public class DataMaintenanceActivity extends ContextsActivity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamain);
        initialListener();
    }

    private void initialListener() {
        findViewById(R.id.datamain_import_csv).setOnClickListener(this);
        findViewById(R.id.datamain_export_csv).setOnClickListener(this);
        findViewById(R.id.datamain_reset).setOnClickListener(this);
        findViewById(R.id.datamain_create_default).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.datamain_import_csv:
            doImportCSV();
            break;
        case R.id.datamain_export_csv:
            doExportCSV();
            break;
        case R.id.datamain_reset:
            doReset();
            break;
        case R.id.datamain_create_default:
            doCreateDefault();
            break;
        }
    }

    private void doCreateDefault() {
        
        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.alert(DataMaintenanceActivity.this, R.string.msg_default_created);
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                new DataCreator(idp, i18n).createDefaultAccount();
            }
        };
        
        GUIs.confirm(this,i18n.string(R.string.qmsg_create_default),new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if(((Integer)data).intValue()==GUIs.OK_BUTTON){
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }

    private void doReset() {

        final GUIs.IBusyListener job = new GUIs.BusyAdapter() {
            @Override
            public void onBusyFinish() {
                GUIs.alert(DataMaintenanceActivity.this, R.string.msg_rested);
            }
            @Override
            public void run() {
                IDataProvider idp = Contexts.instance().getDataProvider();
                idp.reset();
            }
        };
        
        GUIs.confirm(this,i18n.string(R.string.qmsg_reset),new GUIs.OnFinishListener() {
            @Override
            public boolean onFinish(Object data) {
                if(((Integer)data).intValue()==GUIs.OK_BUTTON){
                    GUIs.doBusy(DataMaintenanceActivity.this, job);
                }
                return true;
            }
        });
    }

    private void doExportCSV() {
        // TODO Auto-generated method stub

    }

    private void doImportCSV() {
        // TODO Auto-generated method stub

    }
}
