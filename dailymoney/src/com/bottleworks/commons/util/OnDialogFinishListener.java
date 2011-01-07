package com.bottleworks.commons.util;

import android.app.Dialog;
import android.view.View;

/**
 * helps for dialog operation
 * @author dennis
 *
 */
public interface OnDialogFinishListener {

    
    public boolean onDialogFinish(Dialog dlg,View v,Object data);
}
