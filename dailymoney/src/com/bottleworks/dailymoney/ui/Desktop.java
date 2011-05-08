package com.bottleworks.dailymoney.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
    
    public List<DesktopItem> getVisibleItems(){
        ArrayList<DesktopItem> list = new ArrayList<DesktopItem>();
        for(DesktopItem di:items){
            if(!di.isHidden()){
                list.add(di);
            }
        }
        return list;
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
