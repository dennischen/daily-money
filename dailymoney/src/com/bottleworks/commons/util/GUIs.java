package com.bottleworks.commons.util;

import com.bottleworks.dailymoney.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * 
 * @author dennis
 *
 */
public class GUIs {

    
    public static final int NO_ICON_RES=0x0;
    
    static public void alert(Context context,String title,String msg,String btn,int icon){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        
        alertDialog.setButton(btn, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           }
        });
        if(icon!=NO_ICON_RES){
            alertDialog.setIcon(icon);
        }
        alertDialog.show();
    }
    
    static public void alert(Context context,String msg){
        alert(context,context.getString(R.string.clabel_alert),msg,context.getString(R.string.cact_ok),NO_ICON_RES);
    }
    
    
    
    static public void shortToast(Context context,String msg){
        toast(context,msg,Toast.LENGTH_SHORT);
    }
    
    static public void shortToast(Context context,int res){
        Toast.makeText(context,res,Toast.LENGTH_SHORT).show();
    }
    
    static public void longToast(Context context,String msg){
        toast(context,msg,Toast.LENGTH_LONG);
    }
    
    static public void longToast(Context context,int res){
        Toast.makeText(context,res,Toast.LENGTH_LONG).show();
    }
    
    static public void toast(Context context,String msg,int length){
        Toast.makeText(context,msg,length).show();
    }
    
    static public void toast(Context context,int res,int length){
        Toast.makeText(context,res,length).show();
    }
}
