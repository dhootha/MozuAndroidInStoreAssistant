package com.mozu.mozuandroidinstoreassistant.app.layout.order;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnDataItem;
import com.mozu.mozuandroidinstoreassistant.app.layout.IRowLayout;

public class OrderReturnItemRow extends LinearLayout implements IRowLayout {
    public OrderReturnItemRow(Context context) {
        super(context);
    }

    public OrderReturnItemRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderReturnItemRow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void bindData(IData data) {
        TextView productCode = (TextView) findViewById(R.id.product_code_header_value);
        TextView productTitle = (TextView) findViewById(R.id.product_title_header_value);
        TextView productPrice = (TextView) findViewById(R.id.product_price_header_value);
        TextView productLoss = (TextView) findViewById(R.id.return_loss_header_value);
        TextView productRestockable = (TextView) findViewById(R.id.return_restockable_header_value);
        TextView reason = (TextView) findViewById(R.id.return_reason_header_value);
        TextView quantity = (TextView) findViewById(R.id.return_quantity_header_value);
        TextView quantityReturned = (TextView) findViewById(R.id.return_quantity_returned_header_value);

        if (data instanceof OrderReturnDataItem) {
            OrderReturnDataItem orderReturnDataItem = (OrderReturnDataItem) data;
            productCode.setText(orderReturnDataItem.getProductCode());
            productTitle.setText(orderReturnDataItem.getProductTitle());
            productPrice.setText(orderReturnDataItem.getProductPrice());
            productLoss.setText(orderReturnDataItem.getLoss());
            productRestockable.setText(orderReturnDataItem.getRestockable());
            reason.setText(orderReturnDataItem.getReason());
            quantity.setText(orderReturnDataItem.getQuantity());
            quantityReturned.setText(orderReturnDataItem.getQuantityReturned());
        }

    }
}