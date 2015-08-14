package com.mozu.mozuandroidinstoreassistant.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerLookupFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.NewOrderActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris_pound on 8/5/15.
 */
public class OrderCreationActivity extends BaseActivity implements CustomerLookupFragment.CustomerSelectionListener {

    public static final String ORDER_EXTRA_KEY = "ORDER";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final String ORDER_CUSTOMER_EXTRA_KEY = "order_customer";

    private Order mOrder;

    private int mTenantId;

    private int mSiteId;
    private CustomerAccount mCustomerAccount;

    @InjectView(R.id.submitOrder)
    public Button mOrderSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_create);
        ButterKnife.inject(this);

        if (getIntent() != null) {
            mOrder = (Order) getIntent().getSerializableExtra(ORDER_EXTRA_KEY);
            mTenantId = getIntent().getIntExtra(CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getIntExtra(CURRENT_SITE_ID, -1);
        } else if (savedInstanceState != null) {
            mOrder = (Order) savedInstanceState.getSerializable(ORDER_EXTRA_KEY);
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
        }

        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder, CustomerLookupFragment.getInstance(mTenantId, mSiteId), "create").commit();

        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle("Order #" + mOrder.getOrderNumber());
        }
        mOrderSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderCreationActivity.this, NewOrderActivity.class);
                intent.putExtra(ORDER_CUSTOMER_EXTRA_KEY, mCustomerAccount);
                intent.putExtra(ORDER_EXTRA_KEY, mOrder);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ORDER_EXTRA_KEY, mOrder);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCustomerSelected(CustomerAccount customerAccount) {
        mCustomerAccount = customerAccount;
    }
}
