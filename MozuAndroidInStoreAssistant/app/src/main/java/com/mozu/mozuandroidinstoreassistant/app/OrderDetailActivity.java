package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailSectionPagerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.OrderDetailLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.CustomerAsyncListener;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RetrieveCustomerAsyncTask;
import com.mozu.mozuandroidinstoreassistant.app.views.HeightWrappingViewPager;
import com.viewpagerindicator.TabPageIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Order>, CustomerAsyncListener, SwipeRefreshLayout.OnRefreshListener{

    public static final String ORDER_NUMBER_EXTRA_KEY = "ORDER_NUMBER";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final int LOADER_ORDER_DETAIL = 45;

    private String mOrderNumber;

    private TextView mOrderNumberTextView;
    private TextView mOrderStatus;
    private TextView mOrderDate;
    private TextView mOrderName;
    private TextView mOrderTotal;

    private Order mOrder;

    private int mTenantId;

    private int mSiteId;

    private HeightWrappingViewPager mOrderViewPager;

    private TabPageIndicator mTabIndicator;

    private List<String> mTitles;

    private NumberFormat mNumberFormat;

    @InjectView(R.id.order_detail_container) SwipeRefreshLayout mOrderSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ButterKnife.inject(this);

        if (getIntent() != null) {
            mOrderNumber = getIntent().getStringExtra(ORDER_NUMBER_EXTRA_KEY);
            mTenantId = getIntent().getIntExtra(CURRENT_TENANT_ID, -1);
            mSiteId = getIntent().getIntExtra(CURRENT_SITE_ID, -1);
        } else if (savedInstanceState != null) {
            mOrderNumber = savedInstanceState.getString(ORDER_NUMBER_EXTRA_KEY);
            mTenantId = savedInstanceState.getInt(CURRENT_TENANT_ID, -1);
            mSiteId = savedInstanceState.getInt(CURRENT_SITE_ID, -1);
        }

        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");

        mOrderNumberTextView = (TextView) findViewById(R.id.order_number);
        mOrderStatus = (TextView) findViewById(R.id.order_status);
        mOrderDate = (TextView) findViewById(R.id.order_date);
        mOrderName = (TextView) findViewById(R.id.order_name);
        mOrderTotal = (TextView) findViewById(R.id.order_total);

        mTitles = new ArrayList<String>();
        mTitles.add(getString(R.string.overview_tab_name));
        mTitles.add(getString(R.string.fullfillment_tab_name));
        mTitles.add(getString(R.string.payment_tab_name));
        mTitles.add(getString(R.string.returns_tab_name));
        mTitles.add(getString(R.string.notes_tab_name));

        mOrderViewPager = (HeightWrappingViewPager) findViewById(R.id.order_detail_sections_viewpager);
        mTabIndicator = (TabPageIndicator) findViewById(R.id.order_detail_sections);

        OrderDetailSectionPagerAdapter adapter = new OrderDetailSectionPagerAdapter(getFragmentManager(), mOrder, mTitles, mTenantId, mSiteId);
        mOrderViewPager.setAdapter(adapter);

        mTabIndicator.setViewPager(mOrderViewPager);

        getLoaderManager().initLoader(LOADER_ORDER_DETAIL, null, this).forceLoad();

        mNumberFormat = NumberFormat.getCurrencyInstance();
        mOrderSwipeRefresh.setOnRefreshListener(this);
        mOrderSwipeRefresh.setEnabled(true);
        mOrderSwipeRefresh.setColorScheme(R.color.first_color_swipe_refresh,
                R.color.second_color_swipe_refresh,
                R.color.third_color_swipe_refresh,
                R.color.fourth_color_swipe_refresh);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORDER_NUMBER_EXTRA_KEY, mOrderNumber);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }else if (item.getItemId() == R.id.refresh_order) {
            mOrderSwipeRefresh.setRefreshing(true);
            onRefresh();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            UserAuthenticationStateMachineProducer.getInstance(getApplicationContext()).getCurrentUserAuthState().signOutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh(){
        mTabIndicator.setCurrentItem(0);
        Loader orderLoader = getLoaderManager().getLoader(LOADER_ORDER_DETAIL);
        orderLoader.reset();
        orderLoader.startLoading();
        orderLoader.forceLoad();
    }

    @Override
    public Loader<Order> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_ORDER_DETAIL) {
            mOrderSwipeRefresh.setRefreshing(true);
            return new OrderDetailLoader(this, mTenantId, mSiteId, mOrderNumber);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Order> loader, Order data) {
        mOrderSwipeRefresh.setRefreshing(false);
        mOrder = data;
        if (mOrder == null) {
            return;
        }

        new RetrieveCustomerAsyncTask(this, this, mSiteId, mTenantId, mOrder.getCustomerAccountId()).execute();

        mOrderNumberTextView.setText(String.valueOf(mOrder.getOrderNumber()));

        android.text.format.DateFormat dateFormat= new android.text.format.DateFormat();
        String date = mOrder.getSubmittedDate() != null ? dateFormat.format("MM/dd/yy  hh:mm a", new Date(mOrder.getSubmittedDate().getMillis())).toString() : "";

        mOrderDate.setText(date);

        mOrderStatus.setText(mOrder.getStatus());

        mOrderTotal.setText(mNumberFormat.format(mOrder.getTotal() != null ? mOrder.getTotal() : 0));

        OrderDetailSectionPagerAdapter adapter = new OrderDetailSectionPagerAdapter(getFragmentManager(), mOrder, mTitles, mTenantId, mSiteId);
        mOrderViewPager.setAdapter(adapter);
        mTabIndicator.setViewPager(mOrderViewPager);
        mTabIndicator.setCurrentItem(0);

    }

    @Override
    public void onLoaderReset(Loader<Order> loader) {

    }


    @Override
    public void customerRetreived(CustomerAccount customer) {
        mOrderName.setText(customer.getFirstName() + " " + customer.getLastName());
    }

    @Override
    public void onError(String errorMessage) {
        mOrderName.setText(getString(R.string.error_message_for_order_customer_name));
    }
}
