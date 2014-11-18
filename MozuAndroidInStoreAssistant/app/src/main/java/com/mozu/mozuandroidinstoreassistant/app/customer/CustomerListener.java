package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.contracts.customer.CustomerAccount;

public interface CustomerListener {

    void customerSelected(CustomerAccount customer);

}
