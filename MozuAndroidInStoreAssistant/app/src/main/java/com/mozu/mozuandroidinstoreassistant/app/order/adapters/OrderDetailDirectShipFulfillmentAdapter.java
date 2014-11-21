package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;

import java.util.List;

public class OrderDetailDirectShipFulfillmentAdapter extends ArrayAdapter<FulfillmentItem> {

    public OrderDetailDirectShipFulfillmentAdapter(Context context, List<FulfillmentItem> fulfillmentItems) {
        super(context, R.layout.fulfillment_direct_ship_list_item);

        addAll(fulfillmentItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailDirectShipFulfillmentViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fulfillment_direct_ship_list_item, parent, false);
            viewHolder = new OrderDetailDirectShipFulfillmentViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailDirectShipFulfillmentViewHolder) convertView.getTag();
        }

        FulfillmentItem orderPackage = getItem(position);

        setCellType(viewHolder.cellType, orderPackage);

        setMainLabel(viewHolder.mainLabel, orderPackage);

        setRemainingFields(viewHolder, orderPackage);

        return convertView;
    }

    private void setCellType(TextView view, FulfillmentItem item) {

        //items are not packaged
        if (!item.isPackaged()) {
            view.setText(getContext().getString(R.string.pending_items_direct_ship_label_unpackaged));

            return;
        }

        //items are packaged, but might not be fulfilled
        if (item.isFullfilled()) {
            view.setText(getContext().getString(R.string.pending_items_direct_ship_label_fulfilled));
        } else {
            view.setText(getContext().getString(R.string.pending_items_direct_ship_label_not_fulfilled));
        }

    }

    private void setMainLabel(TextView view, FulfillmentItem item) {

        if (!item.isPackaged()) {
            view.setText(item.getNonPackgedItem().getProduct().getName());

            return;
        }

        view.setText(item.getPackageNumber());
    }

    private void setRemainingFields(OrderDetailDirectShipFulfillmentViewHolder viewHolder, FulfillmentItem item) {

        if (!item.isPackaged()) {
            viewHolder.itemCount.setText("");
            viewHolder.trackingNumber.setText("");

            return;
        }

        if (item.getOrderPackage() != null && item.getOrderPackage().getItems() != null) {
            viewHolder.itemCount.setText(String.valueOf(item.getOrderPackage().getItems().size()) + " " + getContext().getString(R.string.fulfillment_items_label));
        }

        if (item.getOrderPackage() != null) {
            viewHolder.trackingNumber.setText(item.getOrderPackage().getTrackingNumber());
        }

    }
}
