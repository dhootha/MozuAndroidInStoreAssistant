package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import android.util.Log;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentAction;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.resources.commerce.orders.FulfillmentActionResource;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class FulfillmentActionObservablesManager {


    private static FulfillmentActionObservablesManager mFulfillmentActionObservablesManager;
    private static FulfillmentActionResource resource;

    private FulfillmentActionObservablesManager() {
    }

    public static FulfillmentActionObservablesManager getInstance(int tenantId, int siteId) {
        if (mFulfillmentActionObservablesManager == null) {
            mFulfillmentActionObservablesManager = new FulfillmentActionObservablesManager();
        }
        if (resource == null) {
            resource = new FulfillmentActionResource(new MozuApiContext(tenantId, siteId));
        }

        return mFulfillmentActionObservablesManager;
    }

    public Observable<Order> performFulfillmentAction(final FulfillmentAction action, final String orderId) {
        return Observable.create(new Observable.OnSubscribe<Order>() {
            @Override
            public void call(Subscriber<? super Order> subscriber) {
                try {
                    subscriber.onNext(resource.performFulfillmentAction(action, orderId));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("fulfillment action", e.toString());
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io());
    }
}
