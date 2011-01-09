package com.bottleworks.commons.util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Calendars {

    // Sun, 06 Nov 1994 08:49:37 GMT
    static SimpleDateFormat RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",
            new DateFormatSymbols(Locale.ENGLISH));
    static public final TimeZone UTC0 = TimeZone.getTimeZone("UTC0");
    static public final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0:00");
    
    
    public static Calendar calendar(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }
    
    public static Date tomorrow(Date d){
        Calendar cal = calendar(d);
        cal.add(Calendar.DATE,1);
        return cal.getTime();
    }
    
    public static Date dateAfter(Date d, int i){
        Calendar cal = calendar(d);;
        cal.add(Calendar.DATE,i);
        return cal.getTime();
    }
    
    public static Date dateBefore(Date d, int i){
        Calendar cal = calendar(d);
        cal.add(Calendar.DATE,-i);
        return cal.getTime();
    }
    
    public static Date yearAfter(Date d,int i){
        Calendar cal = calendar(d);
        cal.add(Calendar.YEAR,i);
        return cal.getTime();
    }
    
    public static Date yearBefore(Date d,int i){
        Calendar cal = calendar(d);
        cal.add(Calendar.YEAR,-i);
        return cal.getTime();
    }
    
    public static Date monthAfter(Date d,int i){
        Calendar cal = calendar(d);
        cal.add(Calendar.MONTH,i);
        return cal.getTime();
    }
    
    public static Date monthBefore(Date d,int i){
        Calendar cal = calendar(d);
        cal.add(Calendar.MONTH,-i);
        return cal.getTime();
    }
    
    public static Date today() {
        return Calendar.getInstance().getTime();
    }
    
    public static Date yesterday(Date d){
        Calendar cal = calendar(d);
        cal.add(Calendar.DATE,-1);
        return cal.getTime();
    }
    
    public static Date toDayStart(Date d,TimeZone tz){
        if(tz==null){
            tz = TimeZone.getDefault();
        }
        
        Calendar cal = calendar(d);
        cal.setTimeZone(tz);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }
    
    public static Date toDayEnd(Date d,TimeZone tz){
        if(tz==null){
            tz = TimeZone.getDefault();
        }
        
        Calendar cal = calendar(d);
        cal.setTimeZone(tz);
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,999);
        return cal.getTime();
    }
    
    public static String getRFC1123(Date date){
        return RFC1123.format(date);
    }
    
    public static Date parseRFC1123(String str) throws Exception{
        return RFC1123.parse(str);
    }
    
    public static int weekOfMonth(Date d){
        Calendar cal = calendar(d);
        return cal.get(Calendar.WEEK_OF_MONTH); 
    }
    public static int weekOfYear(Date d){
        Calendar cal = calendar(d);
        return cal.get(Calendar.WEEK_OF_YEAR); 
    }

    public static Date monthStartDate(Date d) {
        Calendar cal = calendar(d);
        cal.set(Calendar.DAY_OF_MONTH, 1); 
        return toDayStart(cal.getTime(),null);
    }

    public static Date monthEndDate(Date d) {
        Calendar cal = calendar(d);
        int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last); 
        return toDayEnd(cal.getTime(),null);
    }

    public static Date yearStartDate(Date d) {
        Calendar cal = calendar(d);
        cal.set(Calendar.DAY_OF_YEAR, 1); 
        return toDayStart(cal.getTime(),null);
    }
    
    public static Date yearEndDate(Date d) {
        Calendar cal = calendar(d);
        int last = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_YEAR, last); 
        return toDayEnd(cal.getTime(),null);
    }

    public static Date weekStartDate(Date d) {
        Calendar cal = calendar(d);
        cal.set(Calendar.DAY_OF_WEEK, 1); 
        return toDayStart(cal.getTime(),null);
    }

    public static Date weekEndDate(Date d) {
        Calendar cal = calendar(d);
        int last = cal.getActualMaximum(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_WEEK, last); 
        return toDayEnd(cal.getTime(),null);
    }
}
