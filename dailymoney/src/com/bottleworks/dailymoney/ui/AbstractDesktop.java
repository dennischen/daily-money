package com.bottleworks.dailymoney.ui;

import android.app.Activity;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.context.Contexts;
/**
 * 
 * @author dennis
 *
 */
public abstract class AbstractDesktop extends Desktop {
    protected I18N i18n;
    public AbstractDesktop(Activity activity) {
        super(activity);
        i18n = Contexts.instance().getI18n();
        init();
    }
    abstract protected void init();
}
