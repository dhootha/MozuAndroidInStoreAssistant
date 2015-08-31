package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentColumnHeader;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentDividerRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentFulfilledDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentMoveToDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentCategoryHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.PickupFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShipmentFulfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentMoveToRow;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentMoveToRow.MoveToListener;
import com.mozu.mozuandroidinstoreassistant.app.layout.order.FulfillmentPickupItemRow;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailFulfillmentAdaptertemp extends BaseAdapter {

    private final MoveToListener mListener;
    List<IData> mData = new ArrayList<IData>();
    private FulfillmentPickupItemRow.MarkPickupAsFulfilledListener mFulfillListener;

    public OrderDetailFulfillmentAdaptertemp(Context context, List<IData> data, MoveToListener listener, FulfillmentPickupItemRow.MarkPickupAsFulfilledListener fulfilledListener) {
        mData = data;
        mListener = listener;
        mFulfillListener = fulfilledListener;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public RowType getRowType(int position) {
        IData dataItem = getItem(position);
        if (dataItem instanceof FullfillmentCategoryHeaderDataItem) {
            return RowType.CATEGORY_ROW;
        } else if (dataItem instanceof ShipmentFulfillmentTitleDataItem) {
            return RowType.SHIPMENT_TITLE_ROW;
        } else if (dataItem instanceof PickupFulfillmentTitleDataItem) {
            return RowType.PICKUP_TITLE_ROW;
        } else if (dataItem instanceof FulfillmentDataItem) {
            return RowType.ITEM_ROW;
        } else if (dataItem instanceof FulfillmentPackageDataItem) {
            return RowType.PACKAGE_ROW;
        } else if (dataItem instanceof TopRowItem) {
            return RowType.TOP_ROW;
        } else if (dataItem instanceof BottomRowItem) {
            return RowType.BOTTOM_ROW;
        } else if (dataItem instanceof FulfillmentPickupItem) {
            return RowType.PICKUP_ITEM_ROW;
        } else if (dataItem instanceof FulfillmentDividerRowItem) {
            return RowType.DIVIDER_ROW;
        } else if (dataItem instanceof FulfillmentColumnHeader) {
            return RowType.COLUMN_ROW;
        } else if (dataItem instanceof FulfillmentMoveToDataItem) {
            return RowType.MOVE_TO;
        } else if (dataItem instanceof FulfillmentFulfilledDataItem) {
            return RowType.FULFILLED_ROW;
        } else {
            return RowType.EMPTY_ROW;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getRowType(position).ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public IData getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            RowType rowType = getRowType(position);
            switch (rowType) {
                case SHIPMENT_TITLE_ROW:
                    convertView = inflater.inflate(R.layout.order_fullfilment_title, null);
                    break;
                case PICKUP_TITLE_ROW:
                    convertView = inflater.inflate(R.layout.order_pickup_fullfillment_title, null);
                    break;
                case CATEGORY_ROW:
                    convertView = inflater.inflate(R.layout.fullfillment_sub_header, null);
                    break;
                case PACKAGE_ROW:
                    convertView = inflater.inflate(R.layout.fullfillment_package_item, null);
                    break;
                case ITEM_ROW:
                    convertView = inflater.inflate(R.layout.fullfilment_ship_item, null);
                    break;
                case TOP_ROW:
                    convertView = inflater.inflate(R.layout.top_row_layout, null);
                    break;
                case BOTTOM_ROW:
                    convertView = inflater.inflate(R.layout.bottom_row, null);
                    break;
                case PICKUP_ITEM_ROW:
                    convertView = inflater.inflate(R.layout.fulfillment_pickup_item, null);
                    ((FulfillmentPickupItemRow) convertView).setMarkAsFulfilledListener(mFulfillListener);
                    break;
                case DIVIDER_ROW:
                    convertView = inflater.inflate(R.layout.order_fullfillment_divider_row, null);
                    break;
                case COLUMN_ROW:
                    convertView = inflater.inflate(R.layout.fullfillment_column_header, null);
                    break;
                case MOVE_TO:
                    convertView = inflater.inflate(R.layout.fulfillment_move_to_row, null);
                    ((FulfillmentMoveToRow) convertView).setResponseListener(mListener);
                    break;
                case FULFILLED_ROW:
                    convertView = inflater.inflate(R.layout.fulfillment_fulfilled_row, null);
                    break;
            }
        }

        IRowLayout rowItem = (IRowLayout) convertView;
        rowItem.bindData(getItem(position));
        return convertView;
    }

    public void setData(List<IData> data) {
        mData = data;
    }


    public enum RowType {
        SHIPMENT_TITLE_ROW,
        PICKUP_TITLE_ROW,
        CATEGORY_ROW,
        ITEM_ROW,
        PACKAGE_ROW,
        PICKUP_ITEM_ROW,
        EMPTY_ROW,
        TOP_ROW,
        BOTTOM_ROW,
        DIVIDER_ROW,
        COLUMN_ROW,
        MOVE_TO,
        FULFILLED_ROW
    }

}
