package com.bottleworks.commons.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bottleworks.dailymoney.data.SymbolPosition;
/**
 * 
 * @author dennis
 *
 */
public class Formats {

    private static DecimalFormat doubleFormat = new DecimalFormat("#0.###");
    private static DecimalFormat moneyFormat = new DecimalFormat("###,###,###,##0.###");
    
    /** format should not be changed if i start a export/import function **/
    private static DateFormat norDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
    private static DateFormat norDateFormatOld = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat norDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
    private static DateFormat norDatetimeFormatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DecimalFormat norDoubleFormat = new DecimalFormat("#0.###");
    
    
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
    
    public static String normalizeDouble2String(Double d){
        return normalizeDouble2String(d==null?0D:d);
    }
    
    public static String normalizeDouble2String(double d){
        return norDoubleFormat.format(d);
    }
    
    public static double normalizeString2Double(String d) throws ParseException{
        return norDoubleFormat.parse(d).doubleValue();

    }

    public static String normalizeDate2String(Date date) {
        return norDateFormat.format(date);
    }
    
    public static Date normalizeString2Date(String date) throws ParseException {
        try{
            return norDateFormat.parse(date);
        }catch(ParseException x){
            return norDateFormatOld.parse(date);
        }
    }
    
    public static String normalizeDatetime2String(Date date) {
        return norDatetimeFormat.format(date);
    }
    
    public static Date normalizeString2Datetime(String date) throws ParseException {
        try{
            return norDatetimeFormat.parse(date);
        }catch(ParseException x){
            return norDatetimeFormatOld.parse(date);
        }
    }

    public static String money2String(Double money,String symbol,SymbolPosition pos) {
        return money2String(money==null?0D:money.doubleValue(),symbol,pos);
    }
    
    public static String money2String(double money,String symbol,SymbolPosition pos) {
        StringBuilder sb = new StringBuilder();
        if(SymbolPosition.FRONT.equals(pos) && symbol!=null){
            sb.append(symbol);
        }
        sb.append(moneyFormat.format(money));
        if(SymbolPosition.AFTER.equals(pos) && symbol!=null){
            sb.append(symbol);
        }
        return sb.toString();
    }

    public static String int2String(int d) {
        return DecimalFormat.getIntegerInstance().format(d);
    }

    public static int string2Int(String d) {
        try {
            return DecimalFormat.getInstance().parse(d).intValue();
        } catch (ParseException e) {
            Logger.e(e.getMessage(), e);
            return 0;
        }
    }

}
