package com.mozu.mozuandroidinstoreassistant.app.product.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.Product;
import com.mozu.api.resources.commerce.catalog.admin.ProductResource;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ProductAdminObservableManager {

    private static ProductAdminObservableManager instance;
    private static ProductResource adminProductResource;

    private ProductAdminObservableManager() {
    }

    public static ProductAdminObservableManager getInstance() {
        if (instance == null) {
            instance = new ProductAdminObservableManager();
        }
        return instance;
    }


    public Observable<Product> getMAPPriceObservable(final Integer tenatId, final Integer siteId, final String productCode) {
        return Observable.create(new Observable.OnSubscribe<com.mozu.api.contracts.productadmin.Product>() {
            @Override
            public void call(Subscriber<? super Product> subscriber) {
                try {
                    ProductResource productResource = new ProductResource(new MozuApiContext(tenatId, siteId));
                    com.mozu.api.contracts.productadmin.Product product = productResource.getProduct(productCode);
                    subscriber.onNext(product);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io());
    }
}
