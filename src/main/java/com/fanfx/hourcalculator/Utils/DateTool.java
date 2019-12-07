package com.fanfx.hourcalculator.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTool {
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    public static Date combine(Date ymd,Date hm){
        if (ymd==null||hm==null){
            return null;
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String yyyymmdd = YYYY_MM_DD.format(ymd);
        String hhmm = sdf2.format(hm);
        return paresDate(yyyymmdd + " " + hhmm,"yyyy-MM-dd HH:mm");
    }

    public static Date paresDate(String str,String format){
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(str);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static String dateToYyyymmdd(Date date){
        if (date==null){
            return null;
        }
        return YYYY_MM_DD.format(date);
    }

    public static Date addMin(Date date,int min){
        if (date==null){
            return null;
        }
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(Calendar.MINUTE, min);
        return cd.getTime();
    }


}
