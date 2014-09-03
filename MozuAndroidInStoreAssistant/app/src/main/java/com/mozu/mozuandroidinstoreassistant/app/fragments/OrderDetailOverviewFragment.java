package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.discounts.AppliedLineItemProductDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.AppliedProductDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.Discount;
import com.mozu.api.contracts.commerceruntime.discounts.ShippingDiscount;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderAttribute;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.List;


public class OrderDetailOverviewFragment extends Fragment implements View.OnClickListener {

    public static final String N_A = "N/A";
    private Order mOrder;

    private LinearLayout mOrderedItemLayout;
    private LinearLayout mDetailLayout;

    private LinearLayout mOrderAttributeItemLayout;
    private LinearLayout mAttributesHeaderLayout;

    private TextView mItemsTotal;
    private TextView mDiscounts;
    private TextView mCoupons;
    private TextView mSubTotal;
    private TextView mShipping;
    private TextView mShippingDiscounts;
    private TextView mTax;

    private ImageView mToggleDetailsView;

    private NumberFormat mNumberFormat;

    public OrderDetailOverviewFragment() {
        mNumberFormat = NumberFormat.getCurrencyInstance();

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_overview_fragment, null);

        mOrderedItemLayout = (LinearLayout) view.findViewById(R.id.layout_to_add_ordered_items_to);
        mDetailLayout = (LinearLayout) view.findViewById(R.id.detail_layout);

        mOrderAttributeItemLayout = (LinearLayout) view.findViewById(R.id.layout_to_add_order_attributes_to);
        mAttributesHeaderLayout = (LinearLayout) view.findViewById(R.id.attributes_header_layout);

        mDetailLayout.setVisibility(View.GONE);

        mItemsTotal = (TextView) view.findViewById(R.id.items_total);
        mDiscounts = (TextView) view.findViewById(R.id.discounts);
        mCoupons = (TextView) view.findViewById(R.id.coupons);
        mSubTotal = (TextView) view.findViewById(R.id.sub_total);
        mShipping = (TextView) view.findViewById(R.id.shipping_total);
        mShippingDiscounts = (TextView) view.findViewById(R.id.shipping_discounts_total);
        mTax = (TextView) view.findViewById(R.id.tax_total);

        mToggleDetailsView = (ImageView) view.findViewById(R.id.toggle_details_view);
        mToggleDetailsView.setOnClickListener(this);

        if (mOrder != null) {
            setOrderToViews(view);
        }

        return view;
    }

    private void setOrderToViews(View view) {

        addOrderedItemLayoutsToView(mOrder.getItems());
        addOrderAttributesLayoutsToView(mOrder.getAttributes());

        TextView total = (TextView) view.findViewById(R.id.order_overview_total);
        total.setText(mNumberFormat.format(mOrder.getTotal()));

        mItemsTotal.setText(mOrder.getTotal() != null ? mNumberFormat.format(mOrder.getTotal()) : "N/A");
        mDiscounts.setText(mOrder.getDiscountTotal() != null ? mNumberFormat.format(mOrder.getDiscountTotal()) : "N/A");
        mCoupons.setText("N/A");
        mSubTotal.setText(mOrder.getSubtotal() != null ? mNumberFormat.format(mOrder.getSubtotal()) : "N/A");
        mShipping.setText(mOrder.getShippingSubTotal() != null ? mNumberFormat.format(mOrder.getShippingSubTotal()) : "N/A");

        if (mOrder.getShippingDiscounts() != null) {
            Double shippingDiscountTotal = 0d;

            for (ShippingDiscount discount : mOrder.getShippingDiscounts()) {
                if (discount.getDiscount() != null) {
                    shippingDiscountTotal += discount.getDiscount().getImpact();
                }
            }

            mShippingDiscounts.setText(mNumberFormat.format(shippingDiscountTotal));

        } else {
            mShippingDiscounts.setText("N/A");
        }

        mTax.setText(mOrder.getTaxTotal() != null ? mNumberFormat.format(mOrder.getTaxTotal()) : "N/A");
    }

    private void addOrderAttributesLayoutsToView(List<OrderAttribute> attributes) {

        if (attributes == null || attributes.size() < 1) {
            mAttributesHeaderLayout.setVisibility(View.GONE);
            mOrderAttributeItemLayout.setVisibility(View.GONE);

            return;
        }

        mAttributesHeaderLayout.setVisibility(View.VISIBLE);
        mOrderAttributeItemLayout.setVisibility(View.VISIBLE);


        for(OrderAttribute attribute: attributes) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_attribute_list_item, mOrderAttributeItemLayout, false);

            TextView label = (TextView) view.findViewById(R.id.attribute_label);
            TextView value = (TextView) view.findViewById(R.id.attribute_value);

            label.setVisibility(View.VISIBLE);
            value.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(attribute.getFullyQualifiedName())) {
                label.setText("N/A");
            } else {
                label.setText(attribute.getFullyQualifiedName());
            }

            String valueStr = getStringValueFromAttributesValues(attribute.getValues());

            if (TextUtils.isEmpty(valueStr)) {
                value.setText("N/A");
            } else {
                value.setText(valueStr);
            }

            mOrderAttributeItemLayout.addView(view);
        }
    }

    private String getStringValueFromAttributesValues(List<Object> values) {
        String valueString = "";

        for (Object obj: values) {
            valueString += obj.toString() + " ";
        }

        return valueString;
    }

    private void addOrderedItemLayoutsToView(List<OrderItem> items) {

        int i = 0;

        for(OrderItem item: items) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.ordered_item_list_item, mOrderedItemLayout, false);

            TextView code = (TextView) view.findViewById(R.id.ordered_item_code);
            TextView productName = (TextView) view.findViewById(R.id.ordered_item_product);
            TextView price = (TextView) view.findViewById(R.id.ordered_item_price);
            TextView quantity = (TextView) view.findViewById(R.id.ordered_item_quantity);
            TextView total = (TextView) view.findViewById(R.id.ordered_item_total);
            ImageView discountInfo = (ImageView) view.findViewById(R.id.discount_info_image);

            Product product = item.getProduct();

            if (product == null) {
                code.setText(N_A);
                productName.setText(N_A);
                price.setText(N_A);
                quantity.setText(N_A);
                total.setText(N_A);

                mOrderedItemLayout.addView(view);

                continue;
            }

            code.setText(product.getProductCode());
            productName.setText(product.getName());
            price.setText(product.getPrice() != null && product.getPrice().getPrice() != null ? mNumberFormat.format(product.getPrice().getPrice()) : N_A);
            quantity.setText(String.valueOf(item.getQuantity()));
            total.setText(mNumberFormat.format(item.getTotal()));

            if (item.getProductDiscounts() != null && item.getProductDiscounts().size() > 0) {
                view.setOnClickListener(this);

                view.setTag(i);

                discountInfo.setVisibility(View.VISIBLE);
            } else {
                discountInfo.setVisibility(View.INVISIBLE);
            }

            i++;

            mOrderedItemLayout.addView(view);

        }

    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    @Override
    public void onClick(View v) {

        if (mToggleDetailsView.getId() == v.getId()) {
            if (mDetailLayout.getVisibility() == View.GONE) {
                mDetailLayout.setVisibility(View.VISIBLE);
            } else {
                mDetailLayout.setVisibility(View.GONE);
            }
        } else {
            determineShowDiscountInfo(v);
        }

    }

    private void determineShowDiscountInfo(View v) {
        int index;

        //try to parse the tag as an index, if it does not parse then we know it is not an index
        try {

            index = Integer.parseInt(String.valueOf(v.getTag()));

        } catch (Exception e) {

           index = -1;
        }

        if (index != -1 && mOrder != null && mOrder.getItems() != null && mOrder.getItems().size() > 0) {
            OrderItem item = mOrder.getItems().get(index);

            LinearLayout discountsView = new LinearLayout(getActivity());
            discountsView.setOrientation(LinearLayout.VERTICAL);
            discountsView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            for (AppliedLineItemProductDiscount productDiscount: item.getProductDiscounts()) {
                Discount discount = productDiscount.getDiscount();

                View eachDiscountView = LayoutInflater.from(getActivity()).inflate(R.layout.ordered_item_discount_list_item, discountsView, false);

                TextView discountName = (TextView) eachDiscountView.findViewById(R.id.discount_name);
                TextView discountAmount = (TextView) eachDiscountView.findViewById(R.id.discount_price);
                TextView discountTotal = (TextView) eachDiscountView.findViewById(R.id.discount_total);

                discountName.setText(discount.getName());
                discountAmount.setText(mNumberFormat.format(productDiscount.getImpactPerUnit() * -1));
                discountTotal.setText(mNumberFormat.format(productDiscount.getImpact() * -1));

                discountsView.addView(eachDiscountView);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setView(discountsView);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
