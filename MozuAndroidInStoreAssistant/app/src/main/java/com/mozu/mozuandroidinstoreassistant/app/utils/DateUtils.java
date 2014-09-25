package com.mozu.mozuandroidinstoreassistant.app.utils;


import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    String standardFormat = "MM/dd/yy  hh:mm a";

    public static String getFormattedDate(long Millis){
       DateFormat dateFormat= new DateFormat();
       return dateFormat.format("MM/dd/yy  hh:mm a", new Date(Millis)).toString();

    }

}
