package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class OrderDetailPackageItemAdapter extends ArrayAdapter<PackageItem> {

    private Integer mTenantId;
    private Integer mSiteId;

    public OrderDetailPackageItemAdapter(Context context, List<PackageItem> packageItems, Integer tenantId, Integer siteId) {
        super(context, R.layout.package_list_item);

        mTenantId = tenantId;
        mSiteId = siteId;

        addAll(packageItems);
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

        PackageItem packageItem = getItem(position);

        viewHolder.code.setText(packageItem.getProductCode());
        viewHolder.productName.loadName(packageItem.getProductCode());
        viewHolder.quantity.setText(String.valueOf(packageItem.getQuantity()));

        return convertView;
    }

}
