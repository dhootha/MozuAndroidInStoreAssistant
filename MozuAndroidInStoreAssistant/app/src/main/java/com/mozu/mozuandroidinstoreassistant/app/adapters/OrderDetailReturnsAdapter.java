package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.ReturnItemForAdapterWrapper;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailReturnsAdapter extends ArrayAdapter<ReturnItemForAdapterWrapper> {


    private NumberFormat mNumberFormat;

    public OrderDetailReturnsAdapter(Context context, List<ReturnItemForAdapterWrapper> returns) {
        super(context, R.layout.returns_list_item);

        mNumberFormat = NumberFormat.getInstance();

        addAll(returns);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.returns_list_item, parent, false);
        }

        ReturnItemForAdapterWrapper wrapper = getItem(position);

        ReturnItem returnItem = wrapper.getReturnItem();
        DateTime date = wrapper.getDate();
        String returnTypeString = wrapper.getReturnType();

        TextView returnDate = (TextView) convertView.findViewById(R.id.return_date);
        TextView returnCode = (TextView) convertView.findViewById(R.id.return_code);
        TextView returnProduct = (TextView) convertView.findViewById(R.id.return_product);
        TextView returnPrice = (TextView) convertView.findViewById(R.id.return_price);
        TextView returnType = (TextView) convertView.findViewById(R.id.return_type);
        TextView returnQuantity = (TextView) convertView.findViewById(R.id.return_quantity);

        android.text.format.DateFormat dateFormat= new android.text.format.DateFormat();
        String dateString = date != null ? dateFormat.format("MM/dd/yy  hh:mm a", new Date(date.getMillis())).toString() : "";
        returnDate.setText(dateString);

        if (returnItem.getProduct() != null) {
            returnCode.setText(returnItem.getProduct().getProductCode());
            returnProduct.setText(returnItem.getProduct().getName());

            if (returnItem.getProduct().getPrice() != null && returnItem.getProduct().getPrice().getPrice() != null) {
                returnPrice.setText(mNumberFormat.format(returnItem.getProduct().getPrice().getPrice()));
            } else {
                returnPrice.setText("N/A");
            }

        } else {
            returnCode.setText("N/A");
            returnProduct.setText("N/A");
            returnPrice.setText("N/A");
        }

        returnType.setText(returnTypeString);

        returnQuantity.setText(String.valueOf(returnItem.getQuantityReceived()));

        return convertView;
    }

}
