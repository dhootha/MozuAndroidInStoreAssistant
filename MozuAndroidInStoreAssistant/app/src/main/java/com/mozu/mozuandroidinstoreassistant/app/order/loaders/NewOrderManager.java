package com.mozu.mozuandroidinstoreassistant.app.order.loaders;


import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.fulfillment.ShippingRate;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.contracts.location.LocationCollection;
import com.mozu.api.contracts.productadmin.DiscountCollection;
import com.mozu.api.contracts.productadmin.ProductVariationPagedCollection;
import com.mozu.api.contracts.productruntime.ProductSearchResult;
import com.mozu.api.resources.commerce.LocationResource;
import com.mozu.api.resources.commerce.OrderResource;
import com.mozu.api.resources.commerce.catalog.admin.DiscountResource;
import com.mozu.api.resources.commerce.catalog.admin.products.ProductVariationResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductSearchResultResource;
import com.mozu.api.resources.commerce.orders.AppliedDiscountResource;
import com.mozu.api.resources.commerce.orders.OrderItemResource;
import com.mozu.api.resources.commerce.orders.ShipmentResource;
import com.mozu.mozuandroidinstoreassistant.app.data.product.FulfillmentInfo;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderDetailFullfillmentFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

public class NewOrderManager {

    public static int count = 0;

    private static NewOrderManager mNewOrderManager;
    private AsyncSubject<ProductSearchResult> mProductSearchSubject;
    private String mSearch;
    public static final String PRODUCT_SORT_BY = "productname asc";
    private static final int ITEMS_PER_PAGE = 20;

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
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
                                orderItem.setQuantity(quantity);
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<FulfillmentInfo>> getInstoreLocationCodes(final Integer tenantId, final Integer siteId, final String productCode) {
        return Observable
                .create(new Observable.OnSubscribe<List<FulfillmentInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<FulfillmentInfo>> subscriber) {
                        try {
                            LocationResource locationResource = new LocationResource(new MozuApiContext(tenantId, siteId));
                            LocationCollection locationCollection = locationResource.getInStorePickupLocations();
                            List<FulfillmentInfo> fulfillmentInfoList = new ArrayList<>();
                            for (Location location : locationCollection.getItems()) {
                                if (location.getSupportsInventory()) {
                                    fulfillmentInfoList.add(new FulfillmentInfo(OrderDetailFullfillmentFragment.PICKUP, location.getCode()));
                                }
                            }

                            Location directShipLocation = locationResource.getDirectShipLocation();
                            fulfillmentInfoList.add(new FulfillmentInfo(OrderDetailFullfillmentFragment.SHIP, directShipLocation.getCode()));


                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(fulfillmentInfoList);
                                subscriber.onCompleted();
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
