package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import android.util.Log;

import com.mozu.api.MozuApiContext;
import com.mozu.api.resources.commerce.customer.accounts.CustomerContactResource;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class CustomerManager {

    private static CustomerManager instance;

    private CustomerManager() {

    }

    public static CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public Observable<Integer> getDeleteAddressObservable(Integer tenantId, final Integer siteId, final Integer accountId, final Integer contactId) {
        final CustomerContactResource resource = new CustomerContactResource(new MozuApiContext(tenantId, siteId));
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    resource.deleteAccountContact(accountId, contactId);
                    subscriber.onNext(contactId);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("delete", e.toString());
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io());
    }


}
