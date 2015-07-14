package com.mozu.mozuandroidinstoreassistant.app.product.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.resources.commerce.catalog.admin.products.LocationInventoryResource;

import rx.Observable;
import rx.Subscriber;

public class InventoryRetriever {

    public Observable<LocationInventoryCollection> getInventoryData(final String productCode, int tenantId, int siteId) {
        final LocationInventoryResource inventoryResource = new LocationInventoryResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<LocationInventoryCollection>() {
                    @Override
                    public void call(Subscriber<? super LocationInventoryCollection> subscriber) {
                        try {
                            subscriber.onNext(inventoryResource.getLocationInventories(productCode));
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }
}
