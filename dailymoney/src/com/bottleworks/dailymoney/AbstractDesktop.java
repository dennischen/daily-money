package com.bottleworks.dailymoney;

import android.content.Context;

import com.bottleworks.commons.util.I18N;
import com.bottleworks.dailymoney.ui.Contexts;
import com.bottleworks.dailymoney.ui.Desktop;

public abstract class AbstractDesktop extends Desktop {
    protected I18N i18n;
    public AbstractDesktop(Context context) {
        super(context);
        i18n = Contexts.instance().getI18n();
        init();
    }
    abstract protected void init();
}
