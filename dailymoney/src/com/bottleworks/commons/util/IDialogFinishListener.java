package com.bottleworks.commons.util;

import android.app.Dialog;
import android.view.View;

/**
 * helps for dialog operation
 * @author dennis
 *
 */
public interface IDialogFinishListener {

    
    public boolean onDialogFinish(Dialog dlg,View v,Object data);
}
