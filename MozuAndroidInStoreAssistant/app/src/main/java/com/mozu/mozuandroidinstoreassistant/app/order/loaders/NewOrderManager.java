package com.mozu.mozuandroidinstoreassistant.app.order.loaders;


import android.support.v4.util.ArrayMap;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.ShippingRate;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderAction;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.contracts.location.LocationCollection;
import com.mozu.api.contracts.productadmin.DiscountCollection;
import com.mozu.api.contracts.productadmin.LocationInventory;
import com.mozu.api.contracts.productadmin.LocationInventoryCollection;
import com.mozu.api.contracts.productadmin.ProductVariationPagedCollection;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.api.resources.commerce.LocationResource;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.catalog.admin.DiscountResource;
import com.mozu.api.resources.commerce.catalog.admin.products.LocationInventoryResource;
import com.mozu.api.resources.commerce.catalog.admin.products.ProductVariationResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductSearchResultResource;
import com.mozu.api.resources.commerce.customer.CustomerAccountResource;
import com.mozu.api.resources.commerce.orders.AppliedDiscountResource;
import com.mozu.api.resources.commerce.orders.OrderItemResource;
import com.mozu.api.resources.commerce.orders.ShipmentResource;
import com.mozu.mozuandroidinstoreassistant.app.data.product.FulfillmentInfo;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderStrings;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

public class NewOrderManager {

    public static final String PRODUCT_SORT_BY = "productname asc";
    private static final int ITEMS_PER_PAGE = 20;
    public static int count = 0;
    private static NewOrderManager mNewOrderManager;
    private AsyncSubject<ProductSearchResult> mProductSearchSubject;
    private AsyncSubject<Order> mOrderSubject;
    private AsyncSubject<CustomerAccount> mCustomerSubject;

    private String mSearch;
    private Integer mCustomerId;
    private AsyncSubject<ArrayMap<String, String>> mLocationSubject;

    private NewOrderManager() {
    }

    public static NewOrderManager getInstance() {
        if (mNewOrderManager == null) {
            mNewOrderManager = new NewOrderManager();
        }
        return mNewOrderManager;
    }

    public void invalidateProductSearch() {
        mProductSearchSubject = null;
    }

    public void invalidateOrderData() {
        mOrderSubject = null;
    }

    public void invalidateCustomerInfo() {
        mCustomerSubject = null;
    }


    public Observable<Order> getOrderData(int tenantId, int siteId, String orderId, boolean hardReset) {
        if (hardReset || mOrderSubject == null) {
            mOrderSubject = AsyncSubject.create();
            getOrderObservable(tenantId, siteId, orderId).subscribeOn(Schedulers.io()).subscribe(mOrderSubject);
        }
        return mOrderSubject;
    }

    public Observable<CustomerAccount> getCustomerData(int tenantId, int siteId, Integer customerId) {
        if (mCustomerSubject == null || !customerId.equals(mCustomerId)) {
            mCustomerId = customerId;
            mCustomerSubject = AsyncSubject.create();
            getCustomerInfoObservable(tenantId, siteId, customerId).subscribeOn(Schedulers.io()).subscribe(mCustomerSubject);
        }
        return mCustomerSubject;
    }

    public Observable<ProductSearchResult> getProductSuggestion(String search, int tenantId, int siteId) {
        if (mProductSearchSubject == null || search == null || !search.equals(mSearch)) {
            mSearch = search;
            mProductSearchSubject = AsyncSubject.create();
            getProductSearchSuggestion(tenantId, siteId, search).subscribeOn(Schedulers.io()).subscribe(mProductSearchSubject);
        }
        return mProductSearchSubject;
    }

    public Observable<ArrayMap<String, String>> getLocationsData(int tenantId, int siteId, boolean hardReset) {
        if (mLocationSubject == null || hardReset) {
            mLocationSubject = AsyncSubject.create();
            getLocations(tenantId, siteId).subscribeOn(Schedulers.io()).subscribe(mLocationSubject);
        }
        return mLocationSubject;
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
                                    PRODUCT_SORT_BY, ITEMS_PER_PAGE, 0, null, null, null, null, null, null);
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

    public Observable<Order> getOrderItemCreateObservable(final Integer tenantId, final Integer siteId, final OrderItem orderItem, final String orderId) {
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
                }).subscribeOn(Schedulers.io());
    }

    public Observable<Order> getDeleteOrderItemObservable(final Integer tenantId, final Integer siteId, final String orderItemId, final String orderId) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final OrderItemResource orderItemResource = new OrderItemResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = orderItemResource.deleteOrderItem(orderId, orderItemId);
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
                }).subscribeOn(Schedulers.io());
    }

    public Observable<Order> createOrder(final Integer tenantId, final Integer siteId, final Order order) {
        return Observable.create(new Observable.OnSubscribe<Order>() {
            @Override
            public void call(Subscriber<? super Order> subscriber) {
                final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
                try {
                    Order createdOrder;
                    createdOrder = orderResource.createOrder(order);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(createdOrder);
                        subscriber.onCompleted();
                    }

                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Order> getOrderItemUpdateQuantityObservable(final Integer tenantId, final Integer siteId, final OrderItem orderItem, final String orderId, final Integer quantity, final FulfillmentInfo fulFillmentType) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder = null;
                            final OrderItemResource orderItemResource = new OrderItemResource(new MozuApiContext(tenantId, siteId));
                            if (quantity != null) {
                                updatedOrder = orderItemResource.updateItemQuantity(orderId, orderItem.getId(), quantity);
                            }
                            if (fulFillmentType != null) {
                                orderItem.setFulfillmentLocationCode(fulFillmentType.mLocation);
                                orderItem.setFulfillmentMethod(fulFillmentType.mType);
                                updatedOrder = orderItemResource.updateItemFulfillment(orderItem, orderId, orderItem.getId());
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(updatedOrder);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<FulfillmentInfo>> getInventory(final Integer tenantId, final Integer siteId, final String productCode, final ArrayMap<String, String> locationMap) {
        return Observable
                .create(new Observable.OnSubscribe<List<FulfillmentInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<FulfillmentInfo>> subscriber) {
                        try {
                            List<FulfillmentInfo> fulfillmentInfoList = new ArrayList<>();
                            LocationInventoryResource locationInventoryResource = new LocationInventoryResource(new MozuApiContext(tenantId, siteId));
                            LocationInventoryCollection locationInventoryCollection = locationInventoryResource.getLocationInventories(productCode);
                            for (LocationInventory locationInventory : locationInventoryCollection.getItems()) {
                                if (locationInventory.getStockAvailable() > 0) {
                                    String locationType = locationMap.get(locationInventory.getLocationCode());
                                    if (locationType != null) {
                                        FulfillmentInfo fulfillmentInfo = new FulfillmentInfo(locationType, locationInventory.getLocationCode());
                                        fulfillmentInfoList.add(fulfillmentInfo);
                                    }

                                }
                            }


                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(fulfillmentInfoList);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io());

    }


    public Observable<ArrayMap<String, String>> getLocations(final Integer tenantId, final Integer siteId) {
        return Observable
                .create(new Observable.OnSubscribe<ArrayMap<String, String>>() {
                    @Override
                    public void call(Subscriber<? super ArrayMap<String, String>> subscriber) {
                        try {
                            ArrayMap<String, String> locations = new ArrayMap<String, String>();
                            LocationResource locationResource = new LocationResource(new MozuApiContext(tenantId, siteId));
                            LocationCollection locationCollection = locationResource.getInStorePickupLocations();
                            if (locationCollection != null) {
                                for (Location location : locationCollection.getItems()) {
                                    if (location.getSupportsInventory()) {
                                        locations.put(location.getCode(), OrderStrings.PICKUP);
                                    }
                                }
                            }

                            Location directShipLocation = locationResource.getDirectShipLocation();
                            if (directShipLocation != null) {
                                locations.put(directShipLocation.getCode(), OrderStrings.SHIP);
                            }

                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(locations);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Order> getOrderObservable(final Integer tenantId, final Integer siteId, final String orderId) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
                            Order order = orderResource.getOrder(orderId);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(order);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    public Observable<CustomerAccount> getCustomerInfoObservable(final Integer tenantId, final Integer siteId, final Integer customerId) {
        return Observable
                .create(new Observable.OnSubscribe<CustomerAccount>() {
                    @Override
                    public void call(Subscriber<? super CustomerAccount> subscriber) {
                        try {
                            CustomerAccountResource customerAccountResource = new CustomerAccountResource(new MozuApiContext(tenantId, siteId));
                            CustomerAccount customerAccount = customerAccountResource.getAccount(customerId);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(customerAccount);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }


    public Observable<List<ShippingRate>> getOrderShipments(final Integer tenantId, final Integer siteId, final String orderId) {
        return Observable
                .create(new Observable.OnSubscribe<List<ShippingRate>>() {
                    @Override
                    public void call(Subscriber<? super List<ShippingRate>> subscriber) {
                        try {
                            final ShipmentResource shipmentResource = new ShipmentResource(new MozuApiContext(tenantId, siteId));
                            List<ShippingRate> mShipments = shipmentResource.getAvailableShipmentMethods(orderId);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(mShipments);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<DiscountCollection> getCoupons(final Integer tenantId, final Integer siteId) {
        return Observable
                .create(new Observable.OnSubscribe<DiscountCollection>() {
                    @Override
                    public void call(Subscriber<? super DiscountCollection> subscriber) {
                        try {
                            final DiscountResource discountResource = new DiscountResource(new MozuApiContext(tenantId, siteId));
                            DiscountCollection discountCollection = discountResource.getDiscounts();
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(discountCollection);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Order> getUpdateOrderObservable(final Integer tenantId, final Integer siteId, final Order order, final String orderId) {
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
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Order> getOrderActionObservable(final Integer tenantId, final Integer siteId, final Order order, final OrderAction orderAction) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final OrderResource orderResource = new OrderResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = orderResource.performOrderAction(orderAction, order.getId());
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
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Order> getApplyCouponObservable(final Integer tenantId, final Integer siteId, final String orderId, final String couponCode) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final AppliedDiscountResource appliedDiscountResource = new AppliedDiscountResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = appliedDiscountResource.applyCoupon(orderId, couponCode);
                            updatedOrder.getStatus();
                            subscriber.onNext(updatedOrder);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Order> getRemoveCouponObservable(final Integer tenantId, final Integer siteId, final String orderId, final String couponCode) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            Order updatedOrder;
                            final AppliedDiscountResource appliedDiscountResource = new AppliedDiscountResource(new MozuApiContext(tenantId, siteId));
                            updatedOrder = appliedDiscountResource.removeCoupon(orderId, couponCode);
                            updatedOrder.getStatus();
                            subscriber.onNext(updatedOrder);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<ProductVariationPagedCollection> getProductVariationCodes(final Integer tenantId, final Integer siteId, final String productCode) {
        return Observable
                .create(new Observable.OnSubscribe<ProductVariationPagedCollection>() {
                    @Override
                    public void call(Subscriber<? super ProductVariationPagedCollection> subscriber) {
                        try {
                            ProductVariationPagedCollection productVariationPagedCollection;
                            final ProductVariationResource productVariationResource = new ProductVariationResource(new MozuApiContext(tenantId, siteId));
                            productVariationPagedCollection = productVariationResource.getProductVariations(productCode);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(productVariationPagedCollection);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }


}
