package com.mozu.mozuandroidinstoreassistant.app.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPackageDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FullfillmentPickupItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.PickupFullfillmentTitleDataitem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.ShipmentFullfillmentTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailFullfillmentAdapter extends BaseAdapter {

    public enum RowType{
        SHIPMENT_TITLE_ROW,
        PICKUP_TITLE_ROW,
        HEADER_ROW,
        ITEM_ROW,
        PACKAGE_ROW,
        PICKUP_ITEM_ROW,
        EMPTY_ROW,
        TOP_ROW,
        BOTTOM_ROW
    }

    List<IData> mData = new ArrayList<IData>();

    @Override
    public boolean isEnabled(int position) {
       return true;
    }

    public OrderDetailFullfillmentAdapter(Context context, List<IData> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public RowType getRowType(int position) {
        IData dataItem = getItem(position);
        if (dataItem instanceof FullfillmentHeaderDataItem) {
            return RowType.HEADER_ROW;
        } else if (dataItem instanceof ShipmentFullfillmentTitleDataItem) {
            return RowType.SHIPMENT_TITLE_ROW;
        }else if (dataItem instanceof PickupFullfillmentTitleDataitem) {
            return RowType.PICKUP_TITLE_ROW;
        } else if (dataItem instanceof FullfillmentDataItem) {
            return RowType.ITEM_ROW;
        } else if (dataItem instanceof FullfillmentPackageDataItem) {
            return RowType.PACKAGE_ROW;
        }else if(dataItem instanceof TopRowItem){
            return RowType.TOP_ROW;
        } else if(dataItem instanceof BottomRowItem){
            return RowType.BOTTOM_ROW;
        } else if(dataItem instanceof FullfillmentPickupItem){
            return RowType.PICKUP_ITEM_ROW;
        }else {
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
            if(rowType == RowType.SHIPMENT_TITLE_ROW){
                convertView = inflater.inflate(R.layout.order_fullfilment_title,null);
            }else if( rowType == RowType.PICKUP_TITLE_ROW){
                convertView = inflater.inflate(R.layout.order_pickup_fullfillment_title,null);
            }else if( rowType == RowType.HEADER_ROW){
                convertView = inflater.inflate(R.layout.order_fullfillment_header,null);
            }else if( rowType == RowType.PACKAGE_ROW){
                convertView = inflater.inflate(R.layout.fullfillment_package_item,null);
            }else if(rowType == RowType.ITEM_ROW){
                convertView = inflater.inflate(R.layout.fullfilment_ship_item,null);
            }else if(rowType == RowType.TOP_ROW){
                convertView = inflater.inflate(R.layout.top_row_layout,null);
            }else if(rowType == RowType.BOTTOM_ROW){
                convertView = inflater.inflate(R.layout.bottom_row,null);
            }else if(rowType == RowType.PICKUP_ITEM_ROW){
                convertView = inflater.inflate(R.layout.fulfillment_pickup_item,null);
            }
        }

        IRowLayout rowItem = (IRowLayout)convertView;
        rowItem.bindData(getItem(position));
        return convertView;
    }


    public void setData(List<IData> data){
        mData = data;
    }

}
