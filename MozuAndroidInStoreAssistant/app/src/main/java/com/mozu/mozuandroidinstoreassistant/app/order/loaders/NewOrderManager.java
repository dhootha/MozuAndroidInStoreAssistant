package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductSearchResultResource;
import com.mozu.api.resources.commerce.orders.OrderItemResource;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by santhosh_mankala on 8/17/15.
 */
public class NewOrderManager {

    public static final String PRODUCT_SORT_BY = "productname asc";
    private static final int ITEMS_PER_PAGE = 20;
    public static int count = 0;
    private static NewOrderManager mNewOrderManager;
    private AsyncSubject<ProductSearchResult> mProductSearchSubject;
    private String mSearch;

    private NewOrderManager() {
    }

    public static NewOrderManager getInstance() {
        if (mNewOrderManager == null) {
            mNewOrderManager = new NewOrderManager();
        }

        return mNewOrderManager;
    }

    public Observable<ProductSearchResult> getProductSuggestion(String search, int tenantId, int siteId) {
        if (mProductSearchSubject == null || !search.equals(mSearch)) {
            mProductSearchSubject = AsyncSubject.create();
            getProductSearchSuggestion(tenantId, siteId, search).subscribeOn(Schedulers.io()).subscribe(mProductSearchSubject);
        }
        return mProductSearchSubject;

    }

    private Observable<ProductSearchResult> getProductSearchSuggestion(Integer tenantId, Integer siteId, final String query) {
        final ProductSearchResultResource productSearchResultResource = new ProductSearchResultResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<ProductSearchResult>() {
                    @Override
                    public void call(Subscriber<? super ProductSearchResult> subscriber) {
                        try {
                            ProductSearchResult result = productSearchResultResource.search(query, null,
                                    null, null, null, null, null, null, null, null, null, null, null,
                                    PRODUCT_SORT_BY, ITEMS_PER_PAGE, 0, null, null, null, null, null);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(result);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                });
    }

    public Observable<Order> createOrder(final Integer tenantId, final Integer siteId, final Order order) {
        return Observable.create(new Observable.OnSubscribe<Order>() {
            @Override
            public void call(Subscriber<? super Order> subscriber) {
                final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
                try {
                    Order createdOrder;
                    createdOrder = orderResource.createOrder(order);
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext(createdOrder);
                        subscriber.onCompleted();
                    }

                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    public Observable<Order> getOrderUpdate(final Integer tenantId, final Integer siteId, final OrderItem orderItem, final String orderId) {


        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final OrderItemResource orderItemResource = new OrderItemResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = orderItemResource.createOrderItem(orderItem, orderId);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(updatedOrder);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                });
    }

    public Observable<Order> getOrderCustomerUpdate(final Integer tenantId, final Integer siteId, final Order order, final String orderId) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = orderResource.updateOrder(order, orderId);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(updatedOrder);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                });
    }

}
