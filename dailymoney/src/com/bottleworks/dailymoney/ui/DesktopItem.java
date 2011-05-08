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
    
    //a important item(>=0), will show to menu (the larger number will put to front of menu)
    int important;
    
    //a hidden item, show not show to desktop, but still show to menu if it is important
    boolean hidden;

    public DesktopItem(Runnable run, String label) {
        this(run, label, R.drawable.dtitem,-1);
    }

    public DesktopItem(Runnable run, String label, int icon) {
        this(run, label, icon,-1);
    }
    
    public DesktopItem(Runnable run, String label, int icon, int important) {
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

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    
    
    
    
}
