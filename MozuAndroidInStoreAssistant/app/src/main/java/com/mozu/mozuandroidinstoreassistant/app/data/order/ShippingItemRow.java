package com.mozu.mozuandroidinstoreassistant.app.data.order;

import com.mozu.api.contracts.commerceruntime.commerce.Adjustment;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;

/**
 * Created by santhosh_mankala on 8/18/15.
 */
public class ShippingItemRow implements IData {


    public Adjustment mShippingAdjustment;
    public String mCurrentShippingMethodCode;
    public ShippingItemRow(Adjustment shippingAdjustment,String currentShippingMethodCode) {
        mShippingAdjustment = shippingAdjustment;
        mCurrentShippingMethodCode = currentShippingMethodCode;
    }
}
