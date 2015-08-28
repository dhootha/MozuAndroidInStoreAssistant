package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import android.util.Log;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.resources.commerce.orders.PickupResource;

import rx.Observable;
import rx.Subscriber;

public class PickupObservablesManager {

    private static PickupObservablesManager mPickupObservablesManager;
    private static PickupResource resource;

    private PickupObservablesManager() {
    }

    public static PickupObservablesManager getInstance(int tenantId, int siteId) {
        if (mPickupObservablesManager == null) {
            mPickupObservablesManager = new PickupObservablesManager();
        }
        if (resource == null) {
            resource = new PickupResource(new MozuApiContext(tenantId, siteId));
        }

        return mPickupObservablesManager;
    }

    public Observable<Pickup> createPickup(final Pickup pickup, final String orderId) {
        return Observable.create(new Observable.OnSubscribe<Pickup>() {
            @Override
            public void call(Subscriber<? super Pickup> subscriber) {
                try {
                    subscriber.onNext(resource.createPickup(pickup, orderId));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("createPickup", e.toString());
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Pickup> updatePickup(final Pickup pkg, final String orderId) {
        return Observable.create(new Observable.OnSubscribe<Pickup>() {
            @Override
            public void call(Subscriber<? super Pickup> subscriber) {
                try {
                    subscriber.onNext(resource.updatePickup(pkg, orderId, pkg.getId()));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("updatePickup", e.toString());
                    subscriber.onError(e);
                }
            }
        });
    }
}
