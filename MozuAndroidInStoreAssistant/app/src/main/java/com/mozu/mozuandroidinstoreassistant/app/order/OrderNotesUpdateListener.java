package com.mozu.mozuandroidinstoreassistant.app.order;

import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.commerceruntime.orders.ShopperNotes;

import java.util.List;

/**
 * Created by chris_pound on 8/19/15.
 */
public interface OrderNotesUpdateListener {
    void onShopperNotesUpdated(ShopperNotes notes);

    void onInternalNotesUpdated(List<OrderNote> notes);
}
