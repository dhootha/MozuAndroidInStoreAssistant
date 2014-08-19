package com.mozu.mozuandroidinstoreassistant.app.models;

public class StringUtils {

    public static boolean isNumber(String string) {
        boolean isNumber = true;

        try {
            Integer.parseInt(string);
        } catch(NumberFormatException e) {
            isNumber = false;
        }

        return isNumber;
    }

}
