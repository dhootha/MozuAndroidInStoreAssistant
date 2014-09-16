package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerAccountCollection;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class GlobalSearchCustomerAdapter extends BaseAdapter {

    private CustomerAccountCollection mCustomerAccountCollection;
    public GlobalSearchCustomerAdapter(CustomerAccountCollection customerAccountCollection){
        mCustomerAccountCollection = customerAccountCollection;
    }
    @Override
    public int getCount() {
        if(mCustomerAccountCollection.getItems() != null){
            return mCustomerAccountCollection.getItems().size();
        }
        return 0;
    }

    public void setData(CustomerAccountCollection customerAccountCollection){
        mCustomerAccountCollection = customerAccountCollection;
    }

    @Override
    public CustomerAccount getItem(int i) {
        if (mCustomerAccountCollection.getItems() != null) {
            return mCustomerAccountCollection.getItems().get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GlobalSearchOrderViewHolder viewHolder;
        View v;
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.globalsearch_customer_item, parent,false);
            viewHolder = new GlobalSearchOrderViewHolder();
            viewHolder.customerName = (TextView) convertview.findViewById(R.id.customerCode);
            viewHolder.customerCode = (TextView) convertview.findViewById(R.id.customerName);
            convertview.setTag(viewHolder);
        }
        else{
            viewHolder = (GlobalSearchOrderViewHolder) convertview.getTag();
        }

        CustomerAccount customerAccount = getItem(i);
        if (customerAccount != null) {
            viewHolder.customerName.setText(customerAccount.getLastName()+" "+customerAccount.getFirstName());
            viewHolder.customerCode.setText(String.valueOf(customerAccount.getId()));
        }
        return convertview;
    }

    static class GlobalSearchOrderViewHolder{
        TextView customerName;
        TextView customerCode;
    }


}
