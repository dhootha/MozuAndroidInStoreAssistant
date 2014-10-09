package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.api.contracts.commerceruntime.returns.ReturnReason;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

import java.text.NumberFormat;

public class OrderReturnDataItem implements IData{

    public ReturnItem mOrderReturnItem;
    public OrderReturnDataItem(ReturnItem orderReturnItem){
        mOrderReturnItem = orderReturnItem;
    }

    public String getProductCode() {
        if (mOrderReturnItem.getProduct() != null || mOrderReturnItem.getProduct().getProductCode() != null) {
            return mOrderReturnItem.getProduct().getProductCode();
        } else {
            return "N/A";
        }
    }

    public String getProductTitle() {
        if (mOrderReturnItem.getProduct() != null || mOrderReturnItem.getProduct().getName() != null) {
            return mOrderReturnItem.getProduct().getName();
        } else {
            return "N/A";
        }
    }

    public String getProductPrice() {
        if (mOrderReturnItem.getProduct() != null || mOrderReturnItem.getProduct().getPrice() != null) {
            return NumberFormat.getCurrencyInstance().format( mOrderReturnItem.getProduct().getPrice().getPrice());
        } else {
            return "N/A";
        }
    }


    public String getLoss() {
        if (mOrderReturnItem.getProductLossAmount() != null) {
            return NumberFormat.getCurrencyInstance().format( mOrderReturnItem.getProductLossAmount());
        } else {
            return "N/A";
        }
    }


    public String getRestockable() {
        if (mOrderReturnItem.getQuantityRestockable() != null) {
            return String.valueOf(mOrderReturnItem.getQuantityRestockable());
        } else {
            return "N/A";
        }
    }

    public String getReason() {
        if (mOrderReturnItem.getReasons() != null && mOrderReturnItem.getReasons().size() > 0) {
            String reason = "";
            for (ReturnReason returnReason : mOrderReturnItem.getReasons()) {
                if (reason.length() > 0) {
                    reason = reason + ",";
                }
                reason = reason + returnReason.getReason();
            }
            return String.valueOf(reason);
        } else {
            return "N/A";
        }
    }

    public String getQuantity() {
        if (mOrderReturnItem.getQuantityShipped() != null ) {
            return String.valueOf(mOrderReturnItem.getQuantityShipped());
        } else {
            return "N/A";
        }
    }

    public String getQuantityReturned() {
        if (mOrderReturnItem.getQuantityReceived() != null ) {
            return String.valueOf(mOrderReturnItem.getQuantityReceived());
        } else {
            return "N/A";
        }
    }
 }
