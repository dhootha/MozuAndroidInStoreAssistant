package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.resources.commerce.OrderResource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class OrderFetcher {
    private static final String CUSTOMER_ID_FILTER_BY = "customerAccountId eq ";
    private final int MAX_PAGE_COUNT = 50;
    private Integer mCustomerId;
    public Observable<List<Order>> getOrdersByCustomerId(Integer tenantId, Integer siteId) {
        final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<List<Order>>() {
                    @Override
                    public void call(Subscriber<? super List<Order>> subscriber) {
                        OrderCollection orderCollection;
                        try {
                            if (mCustomerId == null) {
                                subscriber.onError(new Throwable("No customerID provided"));
                            }
                            orderCollection = orderResource.getOrders(0, MAX_PAGE_COUNT, null, CUSTOMER_ID_FILTER_BY + String.valueOf(mCustomerId), null, null, null);
                            subscriber.onNext(orderCollection.getItems());
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

