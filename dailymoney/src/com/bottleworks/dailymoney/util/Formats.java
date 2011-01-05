package com.bottleworks.dailymoney.util;

import java.text.DecimalFormat;
import java.text.ParseException;
/**
 * 
 * @author dennis
 *
 */
public class Formats {

    static DecimalFormat doubleFormat = new DecimalFormat("#0.###");
    
    
    public static String double2String(Double d){
        return double2String(d==null?0D:d);
    }
    
    public static String double2String(double d){
        return doubleFormat.format(d);
    }
    
    public static double string2Double(String d){
        try {
            return doubleFormat.parse(d).doubleValue();
        } catch (ParseException e) {
            Logger.e(e.getMessage(),e);
            return 0D;
        }
    }
}
