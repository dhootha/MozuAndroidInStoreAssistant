package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.ReturnItemForAdapterWrapper;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailReturnsAdapter extends ArrayAdapter<Return> {


    private NumberFormat mNumberFormat;

    public OrderDetailReturnsAdapter(Context context, List<Return> returns) {
        super(context, R.layout.returns_list_item);
        mNumberFormat = NumberFormat.getInstance();
        addAll(returns);
    }

    public void setData(List<Return> returns){
        clear();
        addAll(returns);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.returns_list_item, parent, false);
        }

        Return returnInfo = getItem(position);
        TextView returnId = (TextView) convertView.findViewById(R.id.return_id_value);
        TextView returnStatus = (TextView) convertView.findViewById(R.id.return_status_value);
        TextView returnDate = (TextView) convertView.findViewById(R.id.return_date_value);
        TextView returnAmount = (TextView) convertView.findViewById(R.id.return_amount_value);
        TextView returnType = (TextView) convertView.findViewById(R.id.return_type_value);
        TextView returnQuantity = (TextView) convertView.findViewById(R.id.return_quantity_value);

        returnId.setText(String.valueOf(returnInfo.getReturnNumber()));
        returnStatus.setText(returnInfo.getStatus());
        returnDate.setText(DateUtils.getFormattedDate(returnInfo.getAuditInfo().getCreateDate().getMillis()));
        returnAmount.setText(String.valueOf(returnInfo.getRefundAmount()));
        returnType.setText(returnInfo.getReturnType());
        returnQuantity.setText(String.valueOf(returnInfo.getItems().size()));

        return convertView;
    }

}
