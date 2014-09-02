package com.mozu.mozuandroidinstoreassistant.app.models;

import com.mozu.api.contracts.commerceruntime.fulfillment.*;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;

public class FulfillmentItem {

    private boolean isPackaged;
    private boolean isFullfilled;

    private OrderItem nonPackgedItem;
    private Package orderPackage;
    private Shipment shipment;
    
    private String packageNumber;

    public boolean isPackaged() {
        return isPackaged;
    }

    public void setPackaged(boolean isPackaged) {
        this.isPackaged = isPackaged;
    }

    public boolean isFullfilled() {
        return isFullfilled;
    }

    public void setFullfilled(boolean isFullfilled) {
        this.isFullfilled = isFullfilled;
    }

    public OrderItem getNonPackgedItem() {
        return nonPackgedItem;
    }

    public void setNonPackgedItem(OrderItem nonPackgedItem) {
        this.nonPackgedItem = nonPackgedItem;
    }

    public Package getOrderPackage() {
        return orderPackage;
    }

    public void setOrderPackage(Package orderPackage) {
        this.orderPackage = orderPackage;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }
}
