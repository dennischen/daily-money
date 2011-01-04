package com.bottleworks.dailymoney.util;

import android.content.Context;

public class I18N {
    Context context;
    public I18N(Context context){
        this.context = context;
    }
    
    public String getString(int id){
        return context.getString(id);
    }
    
    public String getString(int id,Object... args){
        return context.getString(id,args);
    }
}
