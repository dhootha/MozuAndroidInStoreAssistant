package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerAttributeCollection;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.api.resources.commerce.customer.accounts.CustomerAttributeResource;


import rx.Observable;
import rx.Subscriber;

public class CustomerAccountFetcher {
    private Integer mCustomerId;

    public Observable<CustomerAccount> getCustomerAccount(Integer tenantId, Integer siteId) {
        final CustomerAccountResource customerAccountResource = new CustomerAccountResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<CustomerAccount>() {
                    @Override
                    public void call(Subscriber<? super CustomerAccount> subscriber) {
                        try {
                            if (mCustomerId == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }
                            CustomerAccount customerAccount = customerAccountResource.getAccount(mCustomerId);
                            if (customerAccount != null) {
                                subscriber.onNext(customerAccount);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable("Failed to fetch data for customerId:" + mCustomerId));
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }

    public void setCustomerId(Integer customerId) {
        mCustomerId = customerId;
    }

    public Observable<CustomerAttributeCollection> getCustomerAccountAttributes(Integer tenantId, Integer siteId) {
        final CustomerAttributeResource customerAttributeResource = new CustomerAttributeResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<CustomerAttributeCollection>() {
                    @Override
                    public void call(Subscriber<? super CustomerAttributeCollection> subscriber) {
                        try {
                            if (mCustomerId == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }
                            CustomerAttributeCollection customerAttributeCollection = customerAttributeResource.getAccountAttributes(mCustomerId);
                            if (customerAttributeCollection != null) {
                                subscriber.onNext(customerAttributeCollection);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Throwable("Failed to fetch data for customerId:" + mCustomerId));
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }


}

