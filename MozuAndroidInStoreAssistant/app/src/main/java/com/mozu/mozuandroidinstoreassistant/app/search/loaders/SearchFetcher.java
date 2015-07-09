package com.mozu.mozuandroidinstoreassistant.app.search.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.OrderCollection;
import com.mozu.api.contracts.customer.CustomerAccountCollection;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductSearchResultResource;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.mozuandroidinstoreassistant.app.models.StringUtils;

import rx.Observable;
import rx.Subscriber;

public class SearchFetcher {

    private static final String ORDER_ID_FILTER_BY = "orderNumber eq ";
    private static final int ITEMS_PER_PAGE = 3;
    private String mQueryString;

    public static final String PRODUCT_SORT_BY = "productname asc";

    public void setQueryString(String queryString){
        mQueryString = queryString;
    }

    public Observable<OrderCollection> searchOrder( Integer tenantId, Integer siteId) {
        final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<OrderCollection>() {
                    @Override
                    public void call(Subscriber<? super OrderCollection> subscriber) {
                        try {

                            OrderCollection orderCollection;
                            if (StringUtils.isNumber(mQueryString)) {
                                orderCollection = orderResource.getOrders(0, ITEMS_PER_PAGE, null, ORDER_ID_FILTER_BY + mQueryString, null, null, null);
                            } else {
                                orderCollection = orderResource.getOrders(0, ITEMS_PER_PAGE, null, null, mQueryString, null, null);
                            }
                            subscriber.onNext(orderCollection);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }

    public Observable<ProductSearchResult> searchProduct(Integer tenantId, Integer siteId) {

        final ProductSearchResultResource productSearchResultResource = new ProductSearchResultResource(new MozuApiContext(tenantId, siteId));

        return Observable
                .create(new Observable.OnSubscribe<ProductSearchResult>() {
                    @Override
                    public void call(Subscriber<? super ProductSearchResult> subscriber) {
                        try {
                            ProductSearchResult result = productSearchResultResource.search(mQueryString, null,
                                    null, null, null, null, null, null, null, null, null, null, null,
                                    PRODUCT_SORT_BY, ITEMS_PER_PAGE, 0, null, null);
                            Thread.sleep(5000);
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }


    public Observable<CustomerAccountCollection> searchCustomer(Integer tenantId, Integer siteId) {
        final CustomerAccountResource customerResource = new CustomerAccountResource(new MozuApiContext(tenantId, siteId));
        return Observable
                .create(new Observable.OnSubscribe<CustomerAccountCollection>() {
                    @Override
                    public void call(Subscriber<? super CustomerAccountCollection> subscriber) {
                        CustomerAccountCollection customerCollection;
                        try {
                            String filter = "firstName sw " + mQueryString + " or lastName sw " + mQueryString + " or emailAddress cont " + mQueryString;
                            customerCollection = customerResource.getAccounts(0, ITEMS_PER_PAGE, null, filter, null, null, null, false, null);
                            subscriber.onNext(customerCollection);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
    }


}
