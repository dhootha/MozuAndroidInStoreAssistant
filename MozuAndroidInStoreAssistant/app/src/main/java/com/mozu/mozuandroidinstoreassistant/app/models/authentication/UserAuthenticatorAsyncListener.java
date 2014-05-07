package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.security.AuthenticationProfile;

public interface UserAuthenticatorAsyncListener {

    void userAuthenticated(AuthenticationProfile profile);
    void authenticationFailed(String errorMessage);
    void authProfileReadFromDisk(AuthenticationProfile profile);

    void errored(String errorMessage);
}
