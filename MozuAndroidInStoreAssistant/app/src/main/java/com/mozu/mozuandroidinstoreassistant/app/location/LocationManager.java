package com.mozu.mozuandroidinstoreassistant.app.location;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.location.LocationCollection;
import com.mozu.api.resources.commerce.admin.LocationResource;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocationManager {

    private static LocationManager mInstance;

    private LocationManager() {
    }

    public static LocationManager getInstance() {
        if (mInstance == null) {
            mInstance = new LocationManager();
        }
        return mInstance;
    }

    public Observable<LocationCollection> getAllLocations(Integer tenantId, Integer siteId) {
        final LocationResource resource = new LocationResource(new MozuApiContext(tenantId, siteId));
        return Observable.create(new Observable.OnSubscribe<LocationCollection>() {
            @Override
            public void call(Subscriber<? super LocationCollection> subscriber) {
                try {
                    LocationCollection locations = resource.getLocations();
                    subscriber.onNext(locations);
                } catch (Exception e) {
                    subscriber.onError(e);
                    Crashlytics.log(e.toString());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<LocationCollection> getAllPickupLocations(Integer tenantId, Integer siteId) {
        final com.mozu.api.resources.commerce.LocationResource resource = new com.mozu.api.resources.commerce.LocationResource(new MozuApiContext(tenantId, siteId));
        return Observable.create(new Observable.OnSubscribe<LocationCollection>() {
            @Override
            public void call(Subscriber<? super LocationCollection> subscriber) {
                try {
                    LocationCollection locations = resource.getInStorePickupLocations();
                    subscriber.onNext(locations);
                } catch (Exception e) {
                    subscriber.onError(e);
                    Crashlytics.log(e.toString());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
