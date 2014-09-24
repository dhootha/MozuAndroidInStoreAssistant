package com.mozu.mozuandroidinstoreassistant.app.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productruntime.Category;
import com.mozu.api.contracts.productruntime.CategoryCollection;
import com.mozu.api.resources.commerce.catalog.storefront.CategoryResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class CategoryFetcher {

    public Observable<List<Category>> getCategoryInformation(Integer tenantId, Integer siteId) {
        final CategoryResource categoryResource = new CategoryResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<List<Category>>() {
                    @Override
                    public void call(Subscriber<? super List<Category>> subscriber) {
                        List<Category> allCategories;
                        CategoryCollection categoryCollection;
                        try {
                            categoryCollection = categoryResource.getCategoryTree();
                            allCategories = categoryCollection.getItems();
                            subscriber.onNext(allCategories);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }

}
