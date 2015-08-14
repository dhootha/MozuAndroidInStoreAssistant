package com.mozu.mozuandroidinstoreassistant.app.order;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.BaseActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.NewOrderFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewOrderActivity extends BaseActivity {

    private Order mOrder;
    private CustomerAccount mCustomerAccount;

    @InjectView(R.id.order_id)
    public TextView mOrderId;

    @InjectView(R.id.order_status)
    public TextView mOrderStatus;

    @InjectView(R.id.order_date)
    public TextView mOrderDate;

    @InjectView(R.id.order_name)
    public TextView mOrderName;

    @InjectView(R.id.order_email)
    public TextView mOrderEmail;

    @InjectView(R.id.order_tabs)
    public TabPageIndicator mOrderTabs;

    @InjectView(R.id.order_viewpager)
    public ViewPager mOrderViewPager;
    private NewOrderFragmentAdapter mOrderFragmentAdapter;

    private Integer mViewPagerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neworder_activity);
        ButterKnife.inject(this);
        if (getIntent() != null) {
            mOrder = (Order) getIntent().getSerializableExtra(OrderCreationActivity.ORDER_EXTRA_KEY);
            mCustomerAccount = (CustomerAccount) getIntent().getSerializableExtra(OrderCreationActivity.ORDER_CUSTOMER_EXTRA_KEY);
        }
        setUpViews();
    }

    public void setUpViews() {
        mOrderId.setText(String.valueOf(mOrder.getOrderNumber()));
        mOrderStatus.setText(mOrder.getStatus());
        mOrderDate.setText(String.valueOf(mOrder.getAcceptedDate()));
        mOrderName.setText(mCustomerAccount.getLastName() + " " + mCustomerAccount.getFirstName());
        mOrderEmail.setText(mCustomerAccount.getEmailAddress());
        if (mOrderFragmentAdapter == null) {
            mOrderFragmentAdapter = new NewOrderFragmentAdapter(getFragmentManager(), null, null);
            mOrderViewPager.setAdapter(mOrderFragmentAdapter);
            mOrderTabs.setViewPager(mOrderViewPager);
        } else {
            mOrderFragmentAdapter.setData(null);
            mOrderFragmentAdapter.notifyDataSetChanged();
        }
        if (mViewPagerPos != null) {
            mOrderViewPager.setCurrentItem(mViewPagerPos);
        }
    }

}
