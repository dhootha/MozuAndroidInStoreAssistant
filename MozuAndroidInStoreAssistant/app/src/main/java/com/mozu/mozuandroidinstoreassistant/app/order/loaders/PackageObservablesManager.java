package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import android.util.Log;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.resources.commerce.orders.PackageResource;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PackageObservablesManager {

    private static PackageObservablesManager mPackageObservablesManager;
    private static PackageResource resource;

    private PackageObservablesManager() {
    }

    public static PackageObservablesManager getInstance(int tenantId, int siteId) {
        if (mPackageObservablesManager == null) {
            mPackageObservablesManager = new PackageObservablesManager();
        }
        if (resource == null) {
            resource = new PackageResource(new MozuApiContext(tenantId, siteId));
        }

        return mPackageObservablesManager;
    }

    public Observable<Package> createPackage(final Package pkg, final String orderId) {
        return Observable.create(new Observable.OnSubscribe<Package>() {
            @Override
            public void call(Subscriber<? super Package> subscriber) {
                try {
                    subscriber.onNext(resource.createPackage(pkg, orderId));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    Log.e("createPackage", e.toString());
                    subscriber.onError(e);
                }
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Package> updatePackage(final Package pkg, final String orderId, final String pkgId) {
        return Observable.create(new Observable.OnSubscribe<Package>() {
            @Override
            public void call(Subscriber<? super Package> subscriber) {
                try {
                    subscriber.onNext(resource.createPackage(pkg, orderId, pkgId));
                } catch (Exception e) {
                    Log.e("updatePackage", e.toString());
                    subscriber.onError(e);
                }
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
