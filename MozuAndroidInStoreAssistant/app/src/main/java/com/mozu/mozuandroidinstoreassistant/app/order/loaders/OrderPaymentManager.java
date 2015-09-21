package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.commerceruntime.payments.PaymentAction;
import com.mozu.api.contracts.commerceruntime.payments.PaymentCard;
import com.mozu.api.contracts.paymentservice.PublicCard;
import com.mozu.api.contracts.paymentservice.response.SyncResponse;
import com.mozu.api.resources.commerce.orders.PaymentResource;
import com.mozu.api.resources.commerce.payments.PublicCardResource;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class OrderPaymentManager {

    private static OrderPaymentManager mNewPaymentManager;

    private OrderPaymentManager() {
    }

    public static OrderPaymentManager getInstance() {
        if (mNewPaymentManager == null) {
            mNewPaymentManager = new OrderPaymentManager();
        }
        return mNewPaymentManager;
    }

    public Observable<Order> getPaymentCreateObservable(final Integer tenantId, final Integer siteId, final PublicCard publicCard, final Order order, final Double amount) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            PublicCardResource publicCardResource = new PublicCardResource(new MozuApiContext(tenantId, siteId));
                            SyncResponse response = publicCardResource.create(publicCard);

                            PaymentAction action = new PaymentAction();
                            action.setActionName("CreatePayment");
                            action.setAmount(amount);
                            action.setCurrencyCode("USD");

                            PaymentCard paymentCard = new PaymentCard();
                            paymentCard.setCardNumberPartOrMask(response.getNumberPart());
                            paymentCard.setPaymentServiceCardId(response.getId());

                            paymentCard.setExpireMonth(Short.parseShort(publicCard.getExpireMonth().toString()));
                            paymentCard.setExpireYear(Short.parseShort(publicCard.getExpireYear().toString()));
                            paymentCard.setPaymentOrCardType(publicCard.getCardType());

                            BillingInfo billingInfo = order.getBillingInfo();
                            billingInfo.setCard(paymentCard);
                            billingInfo.setPaymentType("CreditCard");
                            action.setNewBillingInfo(billingInfo);
                            PaymentResource paymentResource = new PaymentResource(new MozuApiContext(tenantId, siteId));
                            Order updatedOrder = paymentResource.createPaymentAction(action, order.getId());
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


    public Observable<Order> capturePaymentObservable(final Integer tenantId, final Integer siteId, final String orderId, final PaymentAction paymentAction, final String paymentId) {
        return Observable
                .create(new Observable.OnSubscribe<Order>() {
                    @Override
                    public void call(Subscriber<? super Order> subscriber) {
                        try {
                            PaymentResource paymentResource = new PaymentResource(new MozuApiContext(tenantId, siteId));
                            Order updatedOrder = paymentResource.performPaymentAction(paymentAction, orderId, paymentId);
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
}
