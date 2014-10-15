package com.mozu.mozuandroidinstoreassistant.app.data.customer;

import com.mozu.api.contracts.customer.CustomerContact;

public class CustomerContactDataItem {

    private CustomerContact customerContact;
    private String mTypeMessage;

    public CustomerContact getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(CustomerContact customerContact) {
        this.customerContact = customerContact;
    }

    public String getTypeMessage() {
        return mTypeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        mTypeMessage = typeMessage;
    }
}
