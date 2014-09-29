package com.mozu.mozuandroidinstoreassistant.app.utils;


import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    private static String standardDateFormat = "MM/dd/yy";
    private static String standardTimeFormat = "hh:mm a";
    private static String standardDateTimeFormat = standardDateFormat+" "+standardTimeFormat;


    public static String getFormattedDateTime(long Millis){
       DateFormat dateFormat= new DateFormat();
       return dateFormat.format(standardDateTimeFormat, new Date(Millis)).toString();

    }

    public static String getFormattedDate(long Millis){
        DateFormat dateFormat= new DateFormat();
        return dateFormat.format(standardDateFormat, new Date(Millis)).toString();

    }

}
