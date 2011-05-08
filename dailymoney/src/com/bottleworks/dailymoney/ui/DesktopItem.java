package com.bottleworks.dailymoney.ui;

import android.content.Intent;

import com.bottleworks.dailymoney.core.R;
/**
 * 
 * @author dennis
 *
 */
public class DesktopItem {
    //a non-hidden item should always has icon
    protected int icon;
    
    //a item should always has label
    protected String label;
    Runnable run;
    
    //a important item, will show to menu
    boolean important;
    
    //a hidden item, show not show to desktop, but still show to menu if it is importnat
    boolean hidden;

    public DesktopItem(Runnable run, String label) {
        this(run, label, R.drawable.dtitem,false);
    }

    public DesktopItem(Runnable run, String label, int icon) {
        this(run, label, icon,false);
    }
    
    public DesktopItem(Runnable run, String label, int icon, boolean important) {
        this.run = run;
        this.label = label;
        this.icon = icon;
        this.important = important;
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

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    
    
    
    
}
