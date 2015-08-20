package com.mozu.mozuandroidinstoreassistant.app.bus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by chris_pound on 8/19/15.
 */
public class RxBus {
    private final Subject<Object, Object> _bus = PublishSubject.create();

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return _bus;
    }
}
