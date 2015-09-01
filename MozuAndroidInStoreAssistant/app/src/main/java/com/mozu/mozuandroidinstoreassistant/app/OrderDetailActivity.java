package com.mozu.mozuandroidinstoreassistant.app;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.bus.RxBus;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderEditEvent;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailSectionPagerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderDetailLoader;
import com.mozu.mozuandroidinstoreassistant.app.settings.SettingsFragment;
import com.mozu.mozuandroidinstoreassistant.app.tasks.CustomerAsyncListener;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RetrieveCustomerAsyncTask;
import com.viewpagerindicator.TabPageIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OrderDetailActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Order>, CustomerAsyncListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String ORDER_NUMBER_EXTRA_KEY = "ORDER_NUMBER";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final int LOADER_ORDER_DETAIL = 45;
    private final String ORDER_SETTINGS_FRAGMENT = "Order_Settings_Fragment";
    public RxBus mRxBus;
    @InjectView(R.id.order_detail_container)
    SwipeRefreshLayout mOrderSwipeRefresh;
    @InjectView(R.id.edit_mode)
    Button enterEditMode;
    private String mOrderNumber;
    private TextView mOrderStatus;
    private TextView mOrderDate;
    private TextView mCustomerName;
    private TextView mOrderTotal;
    private Order mOrder;
    private int mTenantId;
    private int mSiteId;
    private ViewPager mOrderViewPager;
    private TabPageIndicator mTabIndicator;
    private List<String> mTitles;
    private NumberFormat mNumberFormat;
    private OrderDetailSectionPagerAdapter mAdapter;
    private TextView mOrderFulfillmentStatus;
    private Boolean mIsEditMode = false;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getResources().getBoolean(R.bool.allow_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_order_detail);
        mRxBus = RxBus.getInstance();
        mSubscription = RxBus.getInstance().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getEventSubscriber());

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

        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle(" ");
        }

        enterEditMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwapEditMode();
            }
        });
        mOrderStatus = (TextView) findViewById(R.id.order_status_value);
        mOrderDate = (TextView) findViewById(R.id.order_date_value);
        mCustomerName = (TextView) findViewById(R.id.customer_value);
        mOrderTotal = (TextView) findViewById(R.id.order_total_value);
        mCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrder != null && mOrder.getCustomerAccountId() != null) {
                    Intent intent = new Intent(OrderDetailActivity.this, CustomerDetailActivity.class);
                    intent.putExtra(CustomerDetailActivity.CUSTOMER_ID, mOrder.getCustomerAccountId());
                    startActivity(intent);
                }
            }
        });

        mOrderFulfillmentStatus = (TextView) findViewById(R.id.order_fulfillment_status);

        mTitles = new ArrayList<String>();
        mTitles.add(getString(R.string.overview_tab_name));
        mTitles.add(getString(R.string.fullfillment_tab_name));
        mTitles.add(getString(R.string.payment_tab_name));
        mTitles.add(getString(R.string.returns_tab_name));
        mTitles.add(getString(R.string.notes_tab_name));

        mOrderViewPager = (ViewPager) findViewById(R.id.order_detail_sections_viewpager);
        mTabIndicator = (TabPageIndicator) findViewById(R.id.order_detail_sections);

        if (getLoaderManager().getLoader(LOADER_ORDER_DETAIL) == null) {
            getLoaderManager().initLoader(LOADER_ORDER_DETAIL, null, this).forceLoad();
        } else {
            getLoaderManager().initLoader(LOADER_ORDER_DETAIL, null, this);
        }


        mNumberFormat = NumberFormat.getCurrencyInstance();
        mOrderSwipeRefresh.setOnRefreshListener(this);
        mOrderSwipeRefresh.setEnabled(false);
        mOrderSwipeRefresh.setColorScheme(R.color.first_color_swipe_refresh,
                R.color.second_color_swipe_refresh,
                R.color.third_color_swipe_refresh,
                R.color.fourth_color_swipe_refresh);

    }

    private Action1<Object> getEventSubscriber() {
        return new Action1<Object>() {

            @Override
            public void call(Object o) {
                if (o instanceof OrderEditEvent) {
                    OrderEditEvent event = (OrderEditEvent) o;
                    if (event.shouldShowEdit) {
                        enterEditMode.setVisibility(View.VISIBLE);
                    } else {
                        enterEditMode.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORDER_NUMBER_EXTRA_KEY, mOrderNumber);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);

        super.onSaveInstanceState(outState);
    }

    public void setFulfillmentStatus(String fulfillmentStatus) {
        mOrderFulfillmentStatus.setText(fulfillmentStatus);
        mOrderFulfillmentStatus.setVisibility(View.VISIBLE);
    }

    public void clearFulfillmentStatus() {
        mOrderFulfillmentStatus.setVisibility(View.GONE);
    }

    public void setEditModeVisibility(boolean isVisible) {
        enterEditMode.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.order_detail, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.refresh_order) {
            mOrderSwipeRefresh.setRefreshing(true);
            onRefresh();
            return true;
        } else if (item.getItemId() == R.id.settings) {
            SettingsFragment settingsFragment = SettingsFragment.getInstance();
            settingsFragment.show(getFragmentManager(), ORDER_SETTINGS_FRAGMENT);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
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
        TextView tv = new TextView(this);
        tv.setText("Order #" + mOrder.getOrderNumber());

        tv.setPadding(getResources().getDimensionPixelSize(R.dimen.order_actionbar_margin_left), 0, 0, 0);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.dark_gray_text));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        getActionBar().setCustomView(tv);

        String date = mOrder.getSubmittedDate() != null ? DateFormat.format("MM/dd/yy  hh:mm a", new Date(mOrder.getSubmittedDate().getMillis())).toString() : "";

        mOrderDate.setText(date);

        mOrderStatus.setText(mOrder.getStatus());

        mOrderTotal.setText(mNumberFormat.format(mOrder.getTotal() != null ? mOrder.getTotal() : 0));

        if (mAdapter == null) {
            mAdapter = new OrderDetailSectionPagerAdapter(getFragmentManager(), mOrder, mTitles, mTenantId, mSiteId);
            mOrderViewPager.setAdapter(mAdapter);
            mTabIndicator.setViewPager(mOrderViewPager);
        } else {
            mAdapter.setData(mOrder);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<Order> loader) {

    }


    @Override
    public void customerRetrieved(CustomerAccount customer) {
        if (mCustomerName != null && customer != null) {
            mCustomerName.setText(customer.getFirstName() + " " + customer.getLastName());
        }
    }

    @Override
    public void onError(String errorMessage) {
        mCustomerName.setText(getString(R.string.error_message_for_order_customer_name));
    }

    public void onSwapEditMode() {
        mIsEditMode = !mIsEditMode;
        OrderEditEvent event = new OrderEditEvent();
        event.isEditMode = mIsEditMode;
        event.shouldShowEdit = true;
        mRxBus.send(event);
        mAdapter.setIsEditMode(mIsEditMode);
        if (mIsEditMode) {
            enterEditMode.setText(getString(R.string.exit_edit_mode));
        } else {
            enterEditMode.setText(getString(R.string.edit));
        }
    }
}
