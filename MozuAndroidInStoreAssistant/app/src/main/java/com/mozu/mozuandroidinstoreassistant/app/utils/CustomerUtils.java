package com.mozu.mozuandroidinstoreassistant.app.utils;

import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;

public class CustomerUtils {

    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";

    /**
     * a customer requires a phone number for primary billing and shipping addresses
     *
     * @param customerAccount account to validate
     * @return if a customer has a phone number for primary billing and shipping addresses
     */
    public static boolean isCustomerWithPhoneNumberInDefaultAddress(CustomerAccount customerAccount) {
        if (customerAccount == null || customerAccount.getContacts() == null || customerAccount.getContacts().size() == 0) {
            return false;
        }
        for (CustomerContact customerContact : customerAccount.getContacts()) {
            Phone phone = customerContact.getPhoneNumbers();
            for (ContactType type : customerContact.getTypes()) {
                if ((type.getName().equalsIgnoreCase(SHIPPING) || type.getName().equalsIgnoreCase(BILLING)) && type.getIsPrimary()) {
                    if (phone == null || ((phone.getHome() == null || phone.getHome().isEmpty()) &&
                            phone.getMobile() == null || phone.getMobile().isEmpty() &&
                            phone.getWork() == null || phone.getWork().isEmpty())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean isCustomerWithDefaultBillingAndShipping(CustomerAccount customerAccount) {
        boolean isShipping = false;
        boolean isBilling = false;
        if (customerAccount == null || customerAccount.getContacts() == null || customerAccount.getContacts().size() == 0) {
            return false;
        }

        for (CustomerContact customerContact : customerAccount.getContacts()) {
            for (ContactType type : customerContact.getTypes()) {
                if (type.getName().equalsIgnoreCase(SHIPPING) && type.getIsPrimary()) {
                    isShipping = true;
                }
                if (type.getName().equalsIgnoreCase(BILLING) && type.getIsPrimary()) {
                    isBilling = true;
                }

            }
        }

        return isBilling && isShipping;
    }

    public static boolean isCustomerWithDefaultBilling(CustomerAccount customerAccount) {
        boolean isBilling = false;
        if (customerAccount == null || customerAccount.getContacts() == null || customerAccount.getContacts().size() == 0) {
            return false;
        }

        for (CustomerContact customerContact : customerAccount.getContacts()) {
            for (ContactType type : customerContact.getTypes()) {
                if (type.getName().equalsIgnoreCase(BILLING) && type.getIsPrimary()) {
                    isBilling = true;
                }

            }
        }

        return isBilling;
    }

    public static boolean isCustomerWithDefaultShipping(CustomerAccount customerAccount) {
        boolean isShipping = false;
        if (customerAccount == null || customerAccount.getContacts() == null || customerAccount.getContacts().size() == 0) {
            return false;
        }

        for (CustomerContact customerContact : customerAccount.getContacts()) {
            for (ContactType type : customerContact.getTypes()) {
                if (type.getName().equalsIgnoreCase(SHIPPING) && type.getIsPrimary()) {
                    isShipping = true;
                }

            }
        }

        return isShipping;
    }
}
