package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.customer.credit.Credit;
import com.mozu.api.contracts.customer.credit.CreditCollection;
import com.mozu.api.resources.commerce.customer.CreditResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class StoreCreditFetcher {
    private static final String CUSTOMER_ID_FILTER_BY = "customerId eq ";
    private final int MAX_PAGE_COUNT = 50;
    private Integer mCustomerId;
    public Observable<List<Credit>> getCreditsByCustomerId(Integer tenantId, Integer siteId) {
        final CreditResource creditResource = new CreditResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<List<Credit>>() {
                    @Override
                    public void call(Subscriber<? super List<Credit>> subscriber) {
                        CreditCollection creditCollection;
                        try {
                            if (mCustomerId == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }
                            creditCollection = creditResource.getCredits(0, MAX_PAGE_COUNT, null, CUSTOMER_ID_FILTER_BY + String.valueOf(mCustomerId), null);
                            subscriber.onNext(creditCollection.getItems());
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }

    public void setCustomerId(Integer customerId){
        mCustomerId = customerId;

    }

}

