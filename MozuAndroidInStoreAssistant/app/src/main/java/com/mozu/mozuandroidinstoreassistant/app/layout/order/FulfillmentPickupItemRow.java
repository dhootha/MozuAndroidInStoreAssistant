package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class FulfillmentPickupItemRow extends RelativeLayout implements IRowLayout {
    private MarkPickupAsFulfilledListener mListener;

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
        TextView mItemLocation = (TextView) findViewById(R.id.location_code);
        TextView mStatus = (TextView) findViewById(R.id.status);
        Button mMarkAsFulfilled = (Button) findViewById(R.id.mark_fulfilled);
        Button mCancel = (Button) findViewById(R.id.cancel);


        if (data instanceof FulfillmentPickupItem) {
            final FulfillmentPickupItem fulfillmentPickupItem = (FulfillmentPickupItem) data;
            mPickUpNameText.setText(getContext().getResources().getString(R.string.fulfillment_pickup_number) + String.valueOf(fulfillmentPickupItem.getPickupCount()));

            int totalItemCount = 0;
            for (PickupItem item : fulfillmentPickupItem.getPickup().getItems()) {
                totalItemCount += item.getQuantity();
            }

            mItemCountText.setText(String.valueOf(totalItemCount)+ " " + getContext().getString(R.string.fulfillment_items_label));
            mItemLocation.setText(fulfillmentPickupItem.getPickup().getFulfillmentLocationCode());
            mStatus.setText(fulfillmentPickupItem.getPickup().getStatus());
            mMarkAsFulfilled.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.markPickUpAsFulfilled(fulfillmentPickupItem.getPickup());
                }
            });
            mCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.cancelPickup(fulfillmentPickupItem.getPickup());
                }
            });

        }

    }

    public void setMarkAsFulfilledListener(MarkPickupAsFulfilledListener listener) {
        this.mListener = listener;
    }

    public interface MarkPickupAsFulfilledListener {
        void markPickUpAsFulfilled(Pickup pickup);

        void cancelPickup(Pickup pickup);
    }
}
