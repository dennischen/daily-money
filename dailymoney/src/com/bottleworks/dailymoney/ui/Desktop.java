package com.bottleworks.dailymoney.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dennis
 */
public class Desktop extends Fragment {

    protected String label;
    protected int icon;

    List<DesktopItem> items = new ArrayList<>();

    public void addItem(DesktopItem item) {
        items.add(item);
    }

    public String getLabel() {
        return label;
    }

    public int getIcon() {
        return icon;
    }

    public List<DesktopItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<DesktopItem> getVisibleItems() {
        ArrayList<DesktopItem> list = new ArrayList<>();
        for (DesktopItem di : items) {
            if (!di.isHidden()) {
                list.add(di);
            }
        }
        return list;
    }

    public boolean isAvailable() {
        return true;
    }

    public static class IntentRun implements Runnable {
        Intent intent;
        Context context;

        public IntentRun(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public void run() {
            context.startActivity(intent);
        }
    }

    public static class ActivityRun implements Runnable {
        Class<? extends Activity> activity;
        Context context;

        public ActivityRun(Context context, Class<? extends Activity> activity) {
            this.context = context;
            this.activity = activity;
        }

        public void run() {
            context.startActivity(new Intent(context, activity));
        }
    }

}
