package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;

import java.util.List;

public interface UserPreferencesDiskInteractorListener {

    void finishedReading(List<UserPreferences> prefs);
    void failedToWrite();

}
