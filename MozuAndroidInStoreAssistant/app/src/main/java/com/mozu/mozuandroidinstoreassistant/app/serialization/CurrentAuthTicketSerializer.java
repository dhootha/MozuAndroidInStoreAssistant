package com.mozu.mozuandroidinstoreassistant.app.serialization;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mozu.api.security.AuthenticationProfile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CurrentAuthTicketSerializer extends Serializer {

    private static final String AUTH_TICKET_FILE = "/DoRPN1rKFLy0JsKYrI7F.txt";

    public CurrentAuthTicketSerializer(Context context) {
        super(context.getFilesDir() + AUTH_TICKET_FILE);
    }

    public void serializeAuthProfileSynchronously(AuthenticationProfile authTicket) throws JsonProcessingException, IOException {
       String jsonToWrite = getMapper().writeValueAsString(authTicket);

        writeJsonToFile(jsonToWrite);
    }

    public AuthenticationProfile deserializeAuthProfileSynchronously() throws IOException {
        InputStream inputStream = new FileInputStream(getFileName());

        AuthenticationProfile authenticationProfile = getMapper().readValue(inputStream, AuthenticationProfile.class);

        inputStream.close();

        return authenticationProfile;
    }
}
