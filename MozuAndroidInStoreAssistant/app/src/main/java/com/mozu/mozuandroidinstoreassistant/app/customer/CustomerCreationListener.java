package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.contracts.customer.CustomerAccount;

/**
 * Created by chris_pound on 8/12/15.
 */
public interface CustomerCreationListener {
    void onNextClicked(CustomerAccount customerAccount);

    void onCustomerSaved();
}
