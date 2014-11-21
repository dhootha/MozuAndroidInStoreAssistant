package com.mozu.mozuandroidinstoreassistant.app.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundHeaderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailReturnDialogAdapter extends BaseAdapter {

    public enum RowType {
        RETURN_TITLE_ROW,
        RETURN_HEADER_ROW,
        RETURN_ITEM_ROW,
        REFUND_HEADER_ROW,
        REFUND_ITEM_ROW,
        EMPTY_ROW,
        TOP_ROW,
        BOTTOM_ROW
    }

    List<IData> mData = new ArrayList<IData>();

    @Override
    public boolean isEnabled(int position) {
        RowType rowType = getRowType(position);
        return (rowType == RowType.RETURN_ITEM_ROW || rowType == RowType.REFUND_ITEM_ROW);
    }

    public OrderDetailReturnDialogAdapter(Context context, List<IData> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public RowType getRowType(int position) {
        IData dataItem = getItem(position);
        if (dataItem instanceof OrderReturnTitleDataItem) {
            return RowType.RETURN_TITLE_ROW;
        } else if (dataItem instanceof OrderReturnHeaderDataItem) {
            return RowType.RETURN_HEADER_ROW;
        } else if (dataItem instanceof OrderReturnDataItem) {
            return RowType.RETURN_ITEM_ROW;
        } else if (dataItem instanceof OrderRefundDataItem) {
            return RowType.REFUND_ITEM_ROW;
        } else if (dataItem instanceof OrderRefundHeaderItem) {
            return RowType.REFUND_HEADER_ROW;
        } else if (dataItem instanceof TopRowItem) {
            return RowType.TOP_ROW;
        } else if (dataItem instanceof BottomRowItem) {
            return RowType.BOTTOM_ROW;
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
            if (rowType == RowType.RETURN_TITLE_ROW) {
                convertView = inflater.inflate(R.layout.order_return_title_item, null);
            } else if (rowType == RowType.RETURN_HEADER_ROW) {
                convertView = inflater.inflate(R.layout.order_return_header_item, null);
            } else if (rowType == RowType.RETURN_ITEM_ROW) {
                convertView = inflater.inflate(R.layout.order_return_item_row, null);
            } else if (rowType == RowType.REFUND_HEADER_ROW) {
                convertView = inflater.inflate(R.layout.order_refund_header_item, null);
            } else if (rowType == RowType.REFUND_ITEM_ROW) {
                convertView = inflater.inflate(R.layout.order_refund_item, null);
            } else if (rowType == RowType.BOTTOM_ROW) {
                convertView = inflater.inflate(R.layout.bottom_row, null);
            } else if (rowType == RowType.TOP_ROW) {
                convertView = inflater.inflate(R.layout.top_row_layout, null);
            }
        }

        IRowLayout rowItem = (IRowLayout) convertView;
        rowItem.bindData(getItem(position));
        return convertView;
    }


    public void setData(List<IData> data) {
        mData = data;
    }

}
