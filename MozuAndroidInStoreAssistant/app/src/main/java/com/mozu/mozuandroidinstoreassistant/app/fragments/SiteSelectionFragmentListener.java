package com.mozu.mozuandroidinstoreassistant.app.fragments;

import com.mozu.api.contracts.tenant.Site;

public interface SiteSelectionFragmentListener {

    void siteWasChosen(Site chosenSite);
    void siteNotChosenButExited();
}
