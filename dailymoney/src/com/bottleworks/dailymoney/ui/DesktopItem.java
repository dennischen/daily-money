package com.bottleworks.dailymoney.ui;

import android.content.Intent;

import com.bottleworks.dailymoney.core.R;
/**
 * 
 * @author dennis
 *
 */
public class DesktopItem {
    protected int icon;
    protected String label;
    Runnable run;

    public DesktopItem(Runnable run, String label) {
        this(run, label, R.drawable.dtitem);
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
