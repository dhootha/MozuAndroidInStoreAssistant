package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentFulfilledDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FulfillmentItemFulfilledRow extends RelativeLayout implements IRowLayout {

    @InjectView(R.id.fulfillment_name)
    TextView mFulfillmentName;
    @InjectView(R.id.fulfillment_date)
    TextView mFulfillmentDate;
    @InjectView(R.id.location_code)
    TextView mLocation;
    @InjectView(R.id.item_count)
    TextView mItemCount;

    public FulfillmentItemFulfilledRow(Context context) {
        super(context);
    }

    public FulfillmentItemFulfilledRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FulfillmentItemFulfilledRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        ButterKnife.inject(this);
        if (data instanceof FulfillmentFulfilledDataItem) {
            FulfillmentFulfilledDataItem fulfillmentPickupItem = (FulfillmentFulfilledDataItem) data;
            mFulfillmentName.setText(getContext().getResources().getString(R.string.fulfillment_pickup_number) + String.valueOf(fulfillmentPickupItem.getPickupCount()));

            int totalItemCount = 0;
            for (PickupItem item : fulfillmentPickupItem.getPickup().getItems()) {
                totalItemCount += item.getQuantity();
            }
            DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yy  hh:mm a");
            String pickUpDate = formatter.print(fulfillmentPickupItem.getPickup().getFulfillmentDate().getMillis());
            mFulfillmentDate.setText(pickUpDate);
            mItemCount.setText(String.valueOf(totalItemCount) + " " + getContext().getString(R.string.fulfillment_items_label));
            mLocation.setText(fulfillmentPickupItem.getPickup().getFulfillmentLocationCode());

        }
    }
}
