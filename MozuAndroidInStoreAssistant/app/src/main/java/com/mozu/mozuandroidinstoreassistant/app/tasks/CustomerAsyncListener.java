package com.mozu.mozuandroidinstoreassistant.app.tasks;

import com.mozu.api.contracts.customer.CustomerAccount;

public interface CustomerAsyncListener {

    void customerRetrieved(CustomerAccount customer);
    void onError(String errorMessage);

}
