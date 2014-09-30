package com.mozu.mozuandroidinstoreassistant.app.data.customer;

import com.mozu.api.contracts.customer.CustomerContact;

public class CustomerContactDataItem {

    private CustomerContact customerContact;
    private boolean isPrimary;

    public CustomerContact getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(CustomerContact customerContact) {
        this.customerContact = customerContact;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
