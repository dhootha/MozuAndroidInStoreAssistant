package com.mozu.mozuandroidinstoreassistant.app.models;

public class StringUtils {

    public static boolean isNumber(String string) {
        boolean isNumber = true;

        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            isNumber = false;
        }

        return isNumber;
    }

    public static String FirstLetterUpper(String s) {
        if (s == null || s.length() < 1) return s;
        if (s.length() == 1) return s.toUpperCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
    }

}
