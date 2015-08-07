package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerLookupFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailSectionPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import java.text.NumberFormat;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by chris_pound on 8/5/15.
 */
public class OrderCreationActivity extends BaseActivity {

    public static final String ORDER_NUMBER_EXTRA_KEY = "ORDER_NUMBER";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";

    private  int mOrderNumber;

    private int mTenantId;

    private int mSiteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getResources().getBoolean(R.bool.allow_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_order_create);

        ButterKnife.inject(this);

        if (getIntent() != null) {
            mOrderNumber = getIntent().getIntExtra(ORDER_NUMBER_EXTRA_KEY, -1);
            mTenantId = getIntent().getIntExtra(CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getIntExtra(CURRENT_SITE_ID, -1);
        } else if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getInt(ORDER_NUMBER_EXTRA_KEY, -1);
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
        }

        getFragmentManager().beginTransaction().replace(R.id.content_fragment_holder,CustomerLookupFragment.getInstance(mTenantId, mSiteId), "create").commit();

        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle("Order #"+ mOrderNumber);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ORDER_NUMBER_EXTRA_KEY, mOrderNumber);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);
        super.onSaveInstanceState(outState);
    }

}
