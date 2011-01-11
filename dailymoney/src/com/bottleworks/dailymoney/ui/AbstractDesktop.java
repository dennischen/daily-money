package com.bottleworks.dailymoney.ui;

import android.content.Context;

import com.bottleworks.commons.util.I18N;
/**
 * 
 * @author dennis
 *
 */
public abstract class AbstractDesktop extends Desktop {
    protected I18N i18n;
    public AbstractDesktop(Context context) {
        super(context);
        i18n = Contexts.instance().getI18n();
        init();
    }
    abstract protected void init();
}
