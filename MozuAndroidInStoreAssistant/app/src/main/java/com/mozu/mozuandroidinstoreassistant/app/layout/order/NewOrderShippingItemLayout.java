package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mozu.api.contracts.commerceruntime.commerce.Adjustment;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShippingItemRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

/**
 * Created by santhosh_mankala on 8/21/15.
 */
public class NewOrderShippingItemLayout extends LinearLayout implements IRowLayout {
    public NewOrderShippingItemLayout(Context context) {
        super(context);
    }

    public NewOrderShippingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewOrderShippingItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        if(data instanceof ShippingItemRow){
            Adjustment adjustment = ((ShippingItemRow)data).mShippingAdjustment;
        }


    }

    public interface UpdateShippingListener(){
        public void updateShipping(Order order);
    }

}
