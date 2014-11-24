package com.mozu.mozuandroidinstoreassistant.app;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.credit.Credit;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAccountFetcher;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerFragmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.StoreCreditFetcher;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.settings.SettingsFragment;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;
import com.viewpagerindicator.TabPageIndicator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private View mView;
    private CustomerFragmentAdapter mCustomerFragmentAdapter;
    private Integer mUserAccountId;
    public static final String CUSTOMER_ID = "customerId";

    @InjectView(R.id.customer_since_value) TextView mCustomerSince;
    @InjectView(R.id.customer_lifetime_value) TextView mCustomerLifeTimeValue;
    @InjectView(R.id.customer_total_visits_value) TextView mCustomerTotalVisitsValue;
    @InjectView(R.id.customer_fulfilled_orders_value) TextView mCustomerFulfilledValue;
    @InjectView(R.id.customer_store_credits_value) TextView mCustomerStoreCredits;
    @InjectView(R.id.customer_name) TextView mCustomerName;
    @InjectView(R.id.customer_id) TextView mCustomerId;
    @InjectView(R.id.customer_detail_container) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.customer_detail_sections_viewpager) ViewPager mCustomerViewPager;
    @InjectView(R.id.customer_detail_sections_tab) TabPageIndicator mTabPageIndicator;

    static final String VIEW_PAGER_POS = "viewPagerPos";
    static final String CUSTOMER_ACCOUNT = "customerAccount";
    private CustomerAccount mCustomerAccount;
    private Integer mViewPagerPos;

    private rx.Observable<CustomerAccount> mCustomerObservable;
    private rx.Observable<List<Credit>> mCreditObservable;

    private CustomerAccountFetcher mCustomerAccountFetcher;
    private final String CUSTOMER_SETTINGS_FRAGMENT = "Customer_Settings_Fragment";
    StoreCreditFetcher mCreditFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getResources().getBoolean(R.bool.allow_portrait)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (getIntent() != null) {
            mUserAccountId =  getIntent().getIntExtra(CUSTOMER_ID,-1);
        }

        if (savedInstanceState != null) {
            mCustomerAccount = (CustomerAccount) savedInstanceState.getSerializable(CUSTOMER_ACCOUNT);
            mViewPagerPos = savedInstanceState.getInt(VIEW_PAGER_POS);
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        mView = inflater.inflate(R.layout.activity_customer_detail,null);
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(this);
        mCustomerAccountFetcher = new CustomerAccountFetcher();
        mCustomerObservable = AndroidObservable.bindActivity(this, mCustomerAccountFetcher.getCustomerAccount(userState.getTenantId(), userState.getSiteId()));
        mCreditFetcher = new StoreCreditFetcher();
        mCreditObservable = AndroidObservable.bindActivity(this, mCreditFetcher.getCreditsByCustomerId(userState.getTenantId(), userState.getSiteId()));
        setContentView(mView);
        ButterKnife.inject(this);

        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle("");
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorScheme(R.color.first_color_swipe_refresh,
                R.color.second_color_swipe_refresh,
                R.color.third_color_swipe_refresh,
                R.color.fourth_color_swipe_refresh);
        if (mCustomerAccount == null) {
            loadData();
        } else {
            setUpViews(mCustomerAccount);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(VIEW_PAGER_POS, mCustomerViewPager.getCurrentItem());
        savedInstanceState.putSerializable(CUSTOMER_ACCOUNT, mCustomerAccount);
        super.onSaveInstanceState(savedInstanceState);
    }
    private void loadCustomerStoreCredits(CustomerAccount customerAccount){
        mCreditFetcher.setCustomerId(customerAccount.getId());
        mCreditObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CreditSubscriber());
    }
    private class CreditSubscriber implements rx.Observer<List<Credit>> {
        List<Credit> mCreditList = new ArrayList<Credit>();
        @Override
        public void onCompleted() {
            if (mCreditList.size() > 0) {
                Double TotalCredit = 0.0;
                for (Credit credit : mCreditList) {
                    TotalCredit += credit.getCurrentBalance();
                }
                mCustomerStoreCredits.setText(NumberFormat.getCurrencyInstance().format(TotalCredit));
            } else {
                mCustomerStoreCredits.setText(getResources().getString(R.string.not_available));
            }
        }

        @Override
        public void onError(Throwable e) {
            mCustomerStoreCredits.setText(getResources().getString(R.string.not_available));
        }

        @Override
        public void onNext(List<Credit> creditList) {
            mCreditList = creditList;

        }
    }

    protected void setUpViews(CustomerAccount customerAccount) {
        if (mCustomerFragmentAdapter == null) {
            mCustomerFragmentAdapter = new CustomerFragmentAdapter(getFragmentManager(), customerAccount);
            mCustomerViewPager.setAdapter(mCustomerFragmentAdapter);
            mTabPageIndicator.setViewPager(mCustomerViewPager);
        } else {
            mCustomerFragmentAdapter.setData(customerAccount);
            mCustomerFragmentAdapter.notifyDataSetChanged();
        }
        if (mViewPagerPos != null) {
            mCustomerViewPager.setCurrentItem(mViewPagerPos);
        }

        mCustomerId.setText(String.valueOf(customerAccount.getId()));
        mCustomerName.setText(customerAccount.getFirstName() + "  " + customerAccount.getLastName());
        if (customerAccount.getAuditInfo() != null && customerAccount.getAuditInfo().getCreateDate() != null) {
            String customerSince = DateUtils.getFormattedDate(customerAccount.getAuditInfo().getCreateDate().getMillis());
            mCustomerSince.setText(customerSince);
        } else {
            mCustomerSince.setText(getResources().getString(R.string.not_available));
        }
        if (customerAccount.getCommerceSummary().getTotalOrderAmount() != null && customerAccount.getCommerceSummary().getTotalOrderAmount().getAmount() != null) {
            mCustomerLifeTimeValue.setText(NumberFormat.getCurrencyInstance().format(customerAccount.getCommerceSummary().getTotalOrderAmount().getAmount()));
        } else {
            mCustomerLifeTimeValue.setText(getResources().getString(R.string.not_available));
        }

        if (customerAccount.getCommerceSummary() != null && customerAccount.getCommerceSummary().getVisitsCount() != null) {
            mCustomerTotalVisitsValue.setText(String.valueOf(customerAccount.getCommerceSummary().getVisitsCount()));
        } else {
            mCustomerTotalVisitsValue.setText(getResources().getString(R.string.not_available));
        }

        if (customerAccount.getCommerceSummary() != null && customerAccount.getCommerceSummary().getOrderCount() != null) {
            mCustomerFulfilledValue.setText(String.valueOf(customerAccount.getCommerceSummary().getOrderCount()));
        } else {
            mCustomerFulfilledValue.setText(getResources().getString(R.string.not_available));
        }

        loadCustomerStoreCredits(customerAccount);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }else if (item.getItemId() == R.id.refresh_category_detail) {
            onRefresh();
        }else if(item.getItemId() == R.id.settings){
            SettingsFragment settingsFragment = SettingsFragment.getInstance();
            settingsFragment.show(getFragmentManager(), CUSTOMER_SETTINGS_FRAGMENT);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void loadData(){
        mCustomerAccountFetcher.setCustomerId(mUserAccountId);
        mSwipeRefreshLayout.setRefreshing(true);
        mCustomerObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<CustomerAccount>() {
            @Override
            public void onCompleted() {
                setUpViews(mCustomerAccount);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(CustomerDetailActivity.this,"Failed to load data",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(CustomerAccount customerAccount) {
                mCustomerAccount = customerAccount;

            }
        });
    }


    @Override
    public void onRefresh() {
        loadData();
    }


}
