package com.mozu.mozuandroidinstoreassistant.app.serialization;

import android.content.Context;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mozu.mozuandroidinstoreassistant.app.models.UserPreferences;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserPreferencesSerializer extends Serializer {

    public static final String USER_PREF_FILE = "3QzKtQign8qK4RHFFdeE.txt";

    public UserPreferencesSerializer(Context context) {
        super(context.getFilesDir() + USER_PREF_FILE);
    }

    public void serializeUserPrefs(List<UserPreferences> userPrefsList) throws IOException {
        String json = getMapper().writeValueAsString(userPrefsList);

        writeJsonToFile(json);
    }

    public List<UserPreferences> deseraializeUserPrefs() throws IOException {
        InputStream inputStream = new FileInputStream(getFileName());

        List<UserPreferences> prefsList = getMapper().readValue(inputStream, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, UserPreferences.class));

        inputStream.close();

        return prefsList;
    }
}
