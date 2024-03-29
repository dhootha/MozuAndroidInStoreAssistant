package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.discounts.AppliedLineItemProductDiscount;
import com.mozu.api.contracts.commerceruntime.discounts.Discount;
import com.mozu.api.contracts.commerceruntime.discounts.ShippingDiscount;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderAttribute;
import com.mozu.api.contracts.commerceruntime.orders.OrderItem;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.product.ProductDetailOverviewDialogFragment;

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
    private View mView;
    private ScrollView mScrollView;

    public OrderDetailOverviewFragment() {
        mNumberFormat = NumberFormat.getCurrencyInstance();

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.order_detail_overview_fragment, null);
        mScrollView = (ScrollView) mView.findViewById(R.id.topscrollview);

        mOrderedItemLayout = (LinearLayout) mView.findViewById(R.id.layout_to_add_ordered_items_to);
        mDetailLayout = (LinearLayout) mView.findViewById(R.id.detail_layout);

        mOrderAttributeItemLayout = (LinearLayout) mView.findViewById(R.id.layout_to_add_order_attributes_to);
        mAttributesHeaderLayout = (LinearLayout) mView.findViewById(R.id.attributes_header_layout);

        mDetailLayout.setVisibility(View.GONE);

        mItemsTotal = (TextView) mView.findViewById(R.id.items_total);
        mDiscounts = (TextView) mView.findViewById(R.id.discounts);
        mCoupons = (TextView) mView.findViewById(R.id.coupons);
        mSubTotal = (TextView) mView.findViewById(R.id.sub_total);
        mShipping = (TextView) mView.findViewById(R.id.shipping_total);
        mShippingDiscounts = (TextView) mView.findViewById(R.id.shipping_discounts_total);
        mTax = (TextView) mView.findViewById(R.id.tax_total);

        mToggleDetailsView = (ImageView) mView.findViewById(R.id.toggle_details_view);
        mToggleDetailsView.setOnClickListener(this);

        if (mOrder != null) {
            setOrderToViews(mView);
        }

        return mView;
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
                label.setText(getResources().getString(R.string.not_available));
            } else {
                label.setText(getPropertyValue(attribute.getFullyQualifiedName()));
            }

            String valueStr = getStringValueFromAttributesValues(attribute.getValues());

            if (TextUtils.isEmpty(valueStr)) {
                value.setText(getResources().getString(R.string.not_available));
            } else {
                value.setText(valueStr);
            }

            mOrderAttributeItemLayout.addView(view);
        }
    }

    private String getPropertyValue(String fullyQualifiedName) {
        String delimiter = getResources().getString(R.string.attribute_delimiter);
        if (!TextUtils.isEmpty(fullyQualifiedName)) {
            return fullyQualifiedName.substring(fullyQualifiedName.indexOf(delimiter)+1, fullyQualifiedName.length()).toUpperCase();
        } else {
            return "";
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
         //   final ImageView discountInfo = (ImageView) view.findViewById(R.id.discount_info_image);
            final LinearLayout discountInfoLayout = (LinearLayout)view.findViewById(R.id.discount_info_image_layout);
            discountInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    determineShowDiscountInfo(discountInfoLayout);
                }
            });

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
            view.setOnClickListener(new ProductClickListener());
            view.setTag(i);
            if (item.getProductDiscounts() != null && item.getProductDiscounts().size() > 0) {
                discountInfoLayout.setTag(i);
                discountInfoLayout.setVisibility(View.VISIBLE);
            } else {
                discountInfoLayout.setVisibility(View.INVISIBLE);
            }
            i++;
            mOrderedItemLayout.addView(view);
        }
    }

    public void setOrder(Order order) {
        mOrder = order;
    }

    private class ProductClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int index;
            try {
                index = Integer.parseInt(String.valueOf(view.getTag()));
            } catch (Exception e) {
                index = -1;
            }
            if (index != -1 && mOrder != null && mOrder.getItems() != null && mOrder.getItems().size() > 0) {
                OrderItem item = mOrder.getItems().get(index);
                FragmentManager manager = getFragmentManager();
                ProductDetailOverviewDialogFragment productOverviewFragment = (ProductDetailOverviewDialogFragment) manager.findFragmentByTag("productDialog");
                UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                if (productOverviewFragment == null) {
                    productOverviewFragment = new ProductDetailOverviewDialogFragment();
                    productOverviewFragment.setProduct(item.getProduct());
                    productOverviewFragment.setTenantId(userState.getTenantId());
                    productOverviewFragment.setSiteId(userState.getSiteId());
                    productOverviewFragment.setSiteDomain(userState.getSiteDomain());
                }
                productOverviewFragment.show(manager, "productDialog");
            }

        }
    }

    @Override
    public void onClick(final View v) {
        if (mToggleDetailsView.getId() == v.getId()) {
            if (mDetailLayout.getVisibility() == View.GONE) {
                mDetailLayout.setVisibility(View.VISIBLE);
                mScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.scrollTo(0,mDetailLayout.getBottom());
                    }
                });
            } else {
                mDetailLayout.setVisibility(View.GONE);
            }
        }
    }

    private void determineShowDiscountInfo(View v) {
        int index;
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
