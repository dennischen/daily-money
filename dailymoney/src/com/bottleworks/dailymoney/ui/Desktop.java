package com.bottleworks.dailymoney.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bottleworks.dailymoney.R;

/**
 * 
 * @author dennis
 * 
 */
public class Desktop {

    protected String label;
    protected int icon;
    protected Activity activity;

    List<DesktopItem> items = new ArrayList<DesktopItem>();

    public Desktop(Activity activity){
        this(activity,"",0);
    }
    
    public Desktop(Activity activity,String label, int icon) {
        this(activity,label, icon, null);
    }

    public Desktop(Activity activity,String label, int icon, List<DesktopItem> items) {
        this.activity = activity;
        this.label = label;
        this.icon = icon;
        if (items != null) {
            this.items.addAll(items);
        }
    }

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



    public static class DesktopItem {
        protected int icon;
        protected String label;
        Runnable run;

        public DesktopItem(Runnable run, String label) {
            this(run, label, R.drawable.dt_item);
        }

        public DesktopItem(Runnable run, String label, int icon) {
            this.run = run;
            this.label = label;
            this.icon = icon;
        }

        public void run() {
            run.run();
        }

        public int getIcon() {
            return icon;
        }

        public String getLabel() {
            return label;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            
        }

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

    public void refresh() {
    }
    
    public boolean isAvailable(){
        return true;
    }

}
