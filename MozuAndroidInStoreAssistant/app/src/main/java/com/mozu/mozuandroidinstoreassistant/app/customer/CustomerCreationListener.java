package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.contracts.customer.CustomerAccount;

public interface CustomerCreationListener {
    void onNextClicked(CustomerAccount customerAccount);

    void addNewAddress(CustomerAccount customerAccount);
}
