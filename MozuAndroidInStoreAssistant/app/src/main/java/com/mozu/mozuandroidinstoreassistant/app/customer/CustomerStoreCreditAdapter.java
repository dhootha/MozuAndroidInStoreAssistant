package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.credit.Credit;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.text.NumberFormat;
import java.util.List;

public class CustomerStoreCreditAdapter extends BaseAdapter {

    private List<Credit> mData;

    public CustomerStoreCreditAdapter(List<Credit> data){
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Credit getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Credit credit = getItem(position);
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.customer_storecredit_item,null);
        } else {
            view = convertView;
        }
        TextView creditAmount = (TextView)view.findViewById(R.id.customer_credit_amount_value);
        TextView creditBalance = (TextView)view.findViewById(R.id.customer_credit_balance_value);
        TextView creditCode = (TextView)view.findViewById(R.id.customer_credit_code_value);
        TextView creditDate = (TextView)view.findViewById(R.id.customer_credit_date_issued_value);
        TextView creditIssuedBy = (TextView)view.findViewById(R.id.customer_credit_issued_by_value);
        TextView creditExpires = (TextView)view.findViewById(R.id.customer_credit_expires_value);

        creditAmount.setText(NumberFormat.getCurrencyInstance().format(credit.getInitialBalance()));
        creditCode.setText(credit.getCode());
        creditBalance.setText(NumberFormat.getCurrencyInstance().format(credit.getCurrentBalance()));
        creditIssuedBy.setText(credit.getAuditInfo().getCreateBy());
        creditExpires.setText(DateUtils.getFormattedDate(credit.getExpirationDate().getMillis()));
        creditDate.setText(DateUtils.getFormattedDate(credit.getAuditInfo().getCreateDate().getMillis()));

        return view;
    }

    public void setData(List<Credit> data) {
        mData = data;
    }


}
