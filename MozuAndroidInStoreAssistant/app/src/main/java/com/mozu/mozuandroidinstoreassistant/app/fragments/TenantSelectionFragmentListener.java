package com.mozu.mozuandroidinstoreassistant.app.fragments;

import com.mozu.api.security.Scope;

public interface TenantSelectionFragmentListener {

    void tenantWasChosen(Scope chosenScope);

}
