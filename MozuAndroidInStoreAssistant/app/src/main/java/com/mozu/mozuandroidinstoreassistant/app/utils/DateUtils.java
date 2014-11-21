package com.mozu.mozuandroidinstoreassistant.app.utils;


import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    private static String standardDateFormat = "MM/dd/yy";
    private static String standardTimeFormat = "hh:mm a";
    private static String standardDateTimeFormat = standardDateFormat+" "+standardTimeFormat;


    public static String getFormattedDateTime(long Millis){
       return DateFormat.format(standardDateTimeFormat, new Date(Millis)).toString();

    }

    public static String getFormattedDate(long Millis){
        return DateFormat.format(standardDateFormat, new Date(Millis)).toString();

    }

}
