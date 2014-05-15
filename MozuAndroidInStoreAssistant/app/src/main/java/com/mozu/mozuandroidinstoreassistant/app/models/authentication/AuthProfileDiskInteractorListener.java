package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.security.AuthenticationProfile;

public interface AuthProfileDiskInteractorListener {

    void authProfileReadFromDisk(AuthenticationProfile profile);
    void failedToReadAuthProfile();

}
