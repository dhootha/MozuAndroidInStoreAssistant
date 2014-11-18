package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerAccountAttribute;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerOverviewDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

import java.util.ArrayList;

public class CustomerAccountInfoAdapter extends BaseAdapter {

    private enum RowType {
        HEADER_ROW,
        ATTRIBUTE_ROW,
        EMPTY_ROW

    }

    private ArrayList<IData> mData;

    public CustomerAccountInfoAdapter(ArrayList<IData> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setData(ArrayList<IData> data) {
        if (data != null) {
            mData.clear();
            mData = data;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(getItem(position)).ordinal();
    }


    @Override
    public IData getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    public RowType getViewType(IData dataItem) {
        if (dataItem instanceof CustomerAccountAttribute) {
            return RowType.ATTRIBUTE_ROW;
        } else if (dataItem instanceof CustomerOverviewDataItem) {
            return RowType.HEADER_ROW;
        } else {
            return RowType.EMPTY_ROW;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IData dataItem = getItem(position);
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            if (getViewType(dataItem) == RowType.ATTRIBUTE_ROW) {
                view = inflater.inflate(R.layout.customer_attribute_layout, null);
            } else if (getViewType(dataItem) == RowType.HEADER_ROW) {
                view = inflater.inflate(R.layout.customer_accountinfo_header, null);
            } else {
                view = inflater.inflate(R.layout.empty_row_layout, null);
            }
        } else {
            view = convertView;
        }
        ((IRowLayout) view).bindData(dataItem);
        return view;
    }
}
