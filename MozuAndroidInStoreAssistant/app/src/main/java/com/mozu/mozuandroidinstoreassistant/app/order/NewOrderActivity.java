package com.mozu.mozuandroidinstoreassistant.app.order;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.BaseActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationAddCustomerActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.NewOrderFragmentAdapter;
import com.viewpagerindicator.TabPageIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewOrderActivity extends BaseActivity {

    public static final String IS_EDITABLE = "editable";
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
    private Order mOrder;
    private CustomerAccount mCustomerAccount;
    private NewOrderFragmentAdapter mOrderFragmentAdapter;

    private Integer mViewPagerPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neworder_activity);
        ButterKnife.inject(this);
        if (getIntent() != null) {
            mOrder = (Order) getIntent().getSerializableExtra(OrderCreationAddCustomerActivity.ORDER_EXTRA_KEY);
            mCustomerAccount = (CustomerAccount) getIntent().getSerializableExtra(OrderCreationAddCustomerActivity.ORDER_CUSTOMER_EXTRA_KEY);
        }
        setUpViews();
    }

    private void setUpViews() {
        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle(" ");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    TextView tv = new TextView(NewOrderActivity.this);
                    tv.setText("Order #" + mOrder.getOrderNumber());
                    tv.setPadding(getResources().getDimensionPixelSize(R.dimen.order_actionbar_margin_left), 0, 0, 0);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(getResources().getColor(R.color.dark_gray_text));
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    getActionBar().setCustomView(tv);
                }
            });

        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        mOrderStatus.setText(mOrder.getStatus());
        mOrderDate.setText(dateFormat.format(cal.getTime()));
        mOrderName.setText(mCustomerAccount.getLastName() + " " + mCustomerAccount.getFirstName());
        mOrderEmail.setText(mCustomerAccount.getEmailAddress());
        if (mOrderFragmentAdapter == null) {
            mOrderFragmentAdapter = new NewOrderFragmentAdapter(getFragmentManager(), mOrder);
            mOrderViewPager.setAdapter(mOrderFragmentAdapter);
            mOrderTabs.setViewPager(mOrderViewPager);
        } else {
            mOrderFragmentAdapter.notifyDataSetChanged();
        }
        if (mViewPagerPos != null) {
            mOrderViewPager.setCurrentItem(mViewPagerPos);
        }
    }

    public void updateOrder(Order order) {
        this.mOrder = order;
        //todo update fragments
    }

}
