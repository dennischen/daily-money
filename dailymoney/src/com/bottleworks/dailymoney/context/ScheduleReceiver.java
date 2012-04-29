package com.bottleworks.dailymoney.context;

import java.io.IOException;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bottleworks.commons.util.Files;
import com.bottleworks.dailymoney.ui.Constants;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = Calendar.getInstance();
        if (Constants.BACKUP_JOB.equals(intent.getAction())) {
            try {
                int count = 0;
                count += Files.copyDatabases(getContexts().getDbFolder(), getContexts().getSdFolder(), now.getTime());
                count += Files.copyPrefFile(getContexts().getPrefFolder(), getContexts().getSdFolder(), now.getTime());
                if (count > 0) {
                    getContexts().setLastBackup(context, now.getTime());
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    protected Contexts getContexts() {
        return Contexts.instance();
    }

}
