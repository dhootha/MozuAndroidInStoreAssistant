package com.mozu.mozuandroidinstoreassistant.app.models.authentication;

import com.mozu.api.security.AuthenticationProfile;

public interface RefreshAuthProfileListener {

    void authProfileRefreshed(AuthenticationProfile profile);

}
