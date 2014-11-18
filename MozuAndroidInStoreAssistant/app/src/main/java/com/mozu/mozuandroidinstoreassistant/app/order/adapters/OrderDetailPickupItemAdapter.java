package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.ProductUtils;

import java.util.List;

public class OrderDetailPickupItemAdapter extends ArrayAdapter<PickupItem> {

    private Integer mTenantId;
    private Integer mSiteId;

    public OrderDetailPickupItemAdapter(Context context, List<PickupItem> pickupItems, Integer tenantId, Integer siteId) {
        super(context, R.layout.package_list_item);

        mTenantId = tenantId;
        mSiteId = siteId;

        addAll(pickupItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailPackageItemViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.package_list_item, parent, false);
            viewHolder = new OrderDetailPackageItemViewHolder(convertView, mTenantId, mSiteId);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderDetailPackageItemViewHolder) convertView.getTag();
        }

        PickupItem pickupItem = getItem(position);

        viewHolder.code.setText(pickupItem.getProductCode());
        viewHolder.productName.loadName(ProductUtils.getPackageorPickupProductCode(pickupItem.getProductCode()));
        viewHolder.quantity.setText(String.valueOf(pickupItem.getQuantity()));

        return convertView;
    }

}
