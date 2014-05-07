package com.mozu.mozuandroidinstoreassistant.app.loaders;

import android.provider.ContactsContract;

public interface ProfileQuery {
    String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
}
