package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FulfillmentPickupItemRow extends LinearLayout implements IRowLayout {
    public FulfillmentPickupItemRow(Context context) {
        super(context);
    }

    public FulfillmentPickupItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FulfillmentPickupItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView mPickUpNameText = (TextView) findViewById(R.id.pickup_name);
        TextView mItemCountText = (TextView) findViewById(R.id.item_count);

        if (data instanceof FullfillmentPickupItem) {
            FullfillmentPickupItem fullfillmentDataPickUpItem = (FullfillmentPickupItem) data;
            mPickUpNameText.setText(getContext().getResources().getString(R.string.fulfillment_pickup_number)+String.valueOf(fullfillmentDataPickUpItem.getmPickupCount()));

            int totalItemCount = 0;
            for (PickupItem item : fullfillmentDataPickUpItem.getPickup().getItems()) {
                totalItemCount += item.getQuantity();
            }

            mItemCountText.setText(String.valueOf(totalItemCount)+ " " + getContext().getString(R.string.fulfillment_items_label));
        }

    }
}
