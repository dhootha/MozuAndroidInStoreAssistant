package com.mozu.mozuandroidinstoreassistant.app.bus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class RxBus {

    private static RxBus instance;
    private final Subject<Object, Object> _bus = PublishSubject.create();

    private RxBus() {

    }

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return _bus;
    }
}
