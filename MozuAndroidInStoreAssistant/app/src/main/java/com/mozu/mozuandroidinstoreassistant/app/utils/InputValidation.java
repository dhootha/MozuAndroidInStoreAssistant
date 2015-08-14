package com.mozu.mozuandroidinstoreassistant.app.utils;


import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;


public class InputValidation {

    private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN =
            Pattern.compile("[\\+]?[0-9.-]{8,15}");

    public static boolean isEmailValid(String email) {
        Log.d("email", email);
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPhoneNumberValid(String phone) {
        Log.d("email", phone);
        phone = phone.replaceAll("\\s+", "");
        return GLOBAL_PHONE_NUMBER_PATTERN.matcher(phone).matches();
    }
}
