package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by chris_pound on 8/11/15.
 */
public class CustomerAccountCreationObserver {

    public static Observable<CustomerAccount> getCustomerAccountCreationObserverable(final Integer tenantId, final Integer siteId, final CustomerAccount customerAccount) {

        return Observable.create(new Observable.OnSubscribe<CustomerAccount>() {
            @Override
            public void call(Subscriber<? super CustomerAccount> subscriber) {
                if(siteId == null || tenantId == null) {
                    subscriber.onError(new Exception("Tenant and Site id must not be null"));
                }
                else if(customerAccount == null) {
                    subscriber.onError(new Exception("Address must not be null"));
                }
                else {
                    CustomerAccountResource customerAccountResource = new CustomerAccountResource(new MozuApiContext(tenantId, siteId));
                    try {
                        subscriber.onNext(customerAccountResource.addAccount(customerAccount));
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
