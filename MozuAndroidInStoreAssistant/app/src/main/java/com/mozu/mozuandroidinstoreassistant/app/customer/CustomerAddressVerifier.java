package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.contracts.core.Address;

/**
 * Created by chris_pound on 8/7/15.
 */
public interface CustomerAddressVerifier {
    void verifyAddressIsValid(Address address);
}
