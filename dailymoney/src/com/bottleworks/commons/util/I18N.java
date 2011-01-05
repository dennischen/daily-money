package com.bottleworks.commons.util;

import android.content.Context;

/**
 * 
 * @author dennis
 *
 */
public class I18N {
    Context context;
    public I18N(Context context){
        this.context = context;
    }
    
    public String string(int id){
        return context.getString(id);
    }
    
    public String string(int id,Object... args){
        return context.getString(id,args);
    }
}
