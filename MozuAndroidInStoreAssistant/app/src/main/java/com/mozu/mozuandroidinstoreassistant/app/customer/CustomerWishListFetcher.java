package com.mozu.mozuandroidinstoreassistant.app.customer;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.wishlists.Wishlist;
import com.mozu.api.contracts.commerceruntime.wishlists.WishlistCollection;
import com.mozu.api.resources.commerce.WishlistResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class CustomerWishListFetcher {
    private static final String CUSTOMER_ID_FILTER_BY = "customerAccountId eq ";
    private final int MAX_PAGE_COUNT = 50;
    private Integer mCustomerId;
    public Observable<List<Wishlist>> getWishListsByCustomerId(Integer tenantId, Integer siteId) {
        final WishlistResource wishlistResource = new WishlistResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<List<Wishlist>>() {
                    @Override
                    public void call(Subscriber<? super List<Wishlist>> subscriber) {
                        WishlistCollection wishlistCollection;
                        try {
                            if (mCustomerId == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }
                            wishlistCollection = wishlistResource.getWishlists(0, MAX_PAGE_COUNT, null, CUSTOMER_ID_FILTER_BY + String.valueOf(mCustomerId), null, null, null);
                            subscriber.onNext(wishlistCollection.getItems());
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

