package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.api.resources.commerce.customer.accounts.CustomerContactResource;

import rx.Observable;
import rx.Subscriber;

public class AddCustomerContactObservable {

    public static Observable<CustomerContact> getCustomerContactCreationObserverable(final Integer tenantId, final Integer siteId, final Integer customerAccountId, final CustomerContact customerContact) {

        return Observable.create(new Observable.OnSubscribe<CustomerContact>() {
            @Override
            public void call(Subscriber<? super CustomerContact> subscriber) {
                if (siteId == null || tenantId == null || customerContact == null || customerAccountId == null) {
                    subscriber.onError(new Exception("Values must not be null"));
                } else {
                    CustomerContactResource customerContactResource = new CustomerContactResource(new MozuApiContext(tenantId, siteId));
                    try {
                        subscriber.onNext(customerContactResource.addAccountContact(customerContact, customerAccountId));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        subscriber.onError(e);
                    }
                }

            }
        });
    }

    public static Observable<CustomerContact> getCustomerContactUpdateObservable(final Integer tenantId, final Integer siteId, final Integer customerAccountId, final Integer customerContactId, final CustomerContact customerContact) {

        return Observable.create(new Observable.OnSubscribe<CustomerContact>() {
            @Override
            public void call(Subscriber<? super CustomerContact> subscriber) {
                if (siteId == null || tenantId == null || customerContact == null || customerAccountId == null) {
                    subscriber.onError(new Exception("Values must not be null"));
                } else {
                    CustomerContactResource customerContactResource = new CustomerContactResource(new MozuApiContext(tenantId, siteId));
                    try {
                        subscriber.onNext(customerContactResource.updateAccountContact(customerContact, customerAccountId, customerContactId));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        subscriber.onError(e);
                    }
                }

            }
        });
    }
}
