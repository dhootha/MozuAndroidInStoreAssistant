package com.mozu.mozuandroidinstoreassistant.app.order.loaders;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.resources.commerce.orders.OrderNoteResource;

import rx.Observable;
import rx.Subscriber;

public class OrderNoteObserverable {

    public static Observable<OrderNote> getOrderNoteObserverable(final Integer tenantId, final Integer siteId, final String orderId, final OrderNote note, final OrderCallType type) {
        return Observable.create(new Observable.OnSubscribe<OrderNote>() {
            @Override
            public void call(Subscriber<? super OrderNote> subscriber) {
                OrderNoteResource resource = new OrderNoteResource(new MozuApiContext(tenantId, siteId));
                try {
                    switch (type) {
                        case CREATION:
                            subscriber.onNext(resource.createOrderNote(note, orderId));
                            subscriber.onCompleted();
                            break;
                        case UPDATE:
                            subscriber.onNext(resource.updateOrderNote(note, orderId, note.getId()));
                            subscriber.onCompleted();
                            break;
                        case DELETION:
                            resource.deleteOrderNote(orderId, note.getId());
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                            break;
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    subscriber.onError(e);
                }
            }
        });
    }

    public enum OrderCallType {
        CREATION,
        UPDATE,
        DELETION
    }
}
