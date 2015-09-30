package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.contracts.core.Address;

public interface CustomerAddressVerifier {
    void verifyAddressIsValid(Address address);
}
