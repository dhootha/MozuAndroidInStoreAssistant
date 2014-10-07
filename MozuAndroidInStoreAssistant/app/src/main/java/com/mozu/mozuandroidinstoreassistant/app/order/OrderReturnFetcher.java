package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnCollection;
import com.mozu.api.resources.commerce.ReturnResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class OrderReturnFetcher {
    private static final String CUSTOMER_ID_FILTER_BY = "customerAccountId eq ";
    private final int MAX_PAGE_COUNT = 50;
    private String mOrderNumber;
    public Observable<List<Return>> getOrderReturns(Integer tenantId, Integer siteId) {
        final ReturnResource returnResource = new ReturnResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<List<Return>>() {
                    @Override
                    public void call(Subscriber<? super List<Return>> subscriber) {
                        ReturnCollection returnCollection;
                        try {
                            if (mOrderNumber == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }

                            returnCollection = returnResource.getReturns(0, 200, null, "originalorderId eq " + mOrderNumber + " or returnorderid eq " + mOrderNumber, null);
                            if (returnCollection != null) {
                                subscriber.onNext(returnCollection.getItems());
                            } else {
                                subscriber.onError(new Throwable("No returns Available"));
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }

    public void setOrderNumber(String orderNumber){
        mOrderNumber = orderNumber;
    }

}

