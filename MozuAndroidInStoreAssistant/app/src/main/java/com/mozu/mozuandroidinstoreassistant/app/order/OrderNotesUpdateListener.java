package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderNote;

import java.util.List;

/**
 * Created by chris_pound on 8/19/15.
 */
public interface OrderNotesUpdateListener {

    void onInternalNotesUpdated(List<OrderNote> notes, OrderNote note);

    void onInternalNoteDeleted(List<OrderNote> notes, OrderNote note);
}
