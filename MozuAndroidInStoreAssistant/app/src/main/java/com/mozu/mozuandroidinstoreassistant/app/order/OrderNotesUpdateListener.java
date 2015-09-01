package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderNote;

import java.util.List;

public interface OrderNotesUpdateListener {

    void onInternalNotesUpdated(String noteId, String note);

    void onInternalNoteDeleted(List<OrderNote> notes, OrderNote note);
}
