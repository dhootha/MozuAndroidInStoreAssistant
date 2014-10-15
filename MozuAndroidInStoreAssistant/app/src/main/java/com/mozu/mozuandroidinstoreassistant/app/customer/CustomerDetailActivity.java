package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Activity;
import android.content.Intent;
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
import com.mozu.mozuandroidinstoreassistant.app.LoginActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.settings.SettingsFragment;
import com.viewpagerindicator.TabPageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerDetailActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private View mView;
    private CustomerFragmentAdapter mCustomerFragmentAdapter;
    private Integer mUserAccountId;
    public static final String CUSTOMER_ID = "customerId";

    @InjectView(R.id.customer_name) TextView mCustomerName;
    @InjectView(R.id.customer_email) TextView mCustomerEmail;
    @InjectView(R.id.customer_newsletter) TextView mCustomerNewsletter;
    @InjectView(R.id.customer_tax_exempt) TextView mCustomerTaxExempt;
    @InjectView(R.id.customer_group) TextView mCustomerGroup;
    @InjectView(R.id.customer_id) TextView mCustomerId;
    @InjectView(R.id.customer_detail_container) SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.customer_detail_sections_viewpager) ViewPager mCustomerViewPager;
    @InjectView(R.id.customer_detail_sections_tab) TabPageIndicator mTabPageIndicator;

    static final String VIEW_PAGER_POS = "viewPagerPos";
    static final String CUSTOMER_ACCOUNT = "customerAccount";
    private CustomerAccount mCustomerAccount;
    private Integer mViewPagerPos;

    private rx.Observable<CustomerAccount> mCustomerObservable;
    private CustomerAccountFetcher mCustomerAccountFetcher;
    private final String CUSTOMER_SETTINGS_FRAGMENT = "Customer_Settings_Fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(mView);
        ButterKnife.inject(this);


        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");

        mSwipeRefreshLayout.setOnRefreshListener(this);
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

    public void setUpViews(CustomerAccount customerAccount) {
        mCustomerFragmentAdapter = new CustomerFragmentAdapter(getFragmentManager(), customerAccount);
        mCustomerViewPager.setAdapter(mCustomerFragmentAdapter);
        mTabPageIndicator.setViewPager(mCustomerViewPager);
        if (mViewPagerPos != null) {
            mCustomerViewPager.setCurrentItem(mViewPagerPos);
        }

        mCustomerName.setText(customerAccount.getFirstName() + "  " + customerAccount.getLastName());
        mCustomerEmail.setText(customerAccount.getEmailAddress());
        if (customerAccount.getTaxExempt() != null) {
            mCustomerTaxExempt.setText(customerAccount.getTaxExempt() ? "Yes" : "No");
        }else{
            mCustomerTaxExempt.setText("N/A");
        }
        if (customerAccount.getAcceptsMarketing() != null) {
            mCustomerNewsletter.setText(customerAccount.getAcceptsMarketing() ? "Yes" : "No");
        }else{
            mCustomerNewsletter.setText("N/A");
        }
        if (customerAccount.getCompanyOrOrganization() != null) {
            mCustomerGroup.setText(customerAccount.getCompanyOrOrganization());
        } else {
            mCustomerGroup.setText("N/A");
        }

        mCustomerId.setText(String.valueOf(customerAccount.getId()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }else if (item.getItemId() == R.id.action_logout) {
            UserAuthenticationStateMachineProducer.getInstance(getApplicationContext()).getCurrentUserAuthState().signOutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (item.getItemId() == R.id.refresh_category_detail) {
            onRefresh();
        }else if(item.getItemId() == R.id.settings){
            SettingsFragment settingsFragment = SettingsFragment.getInstance();
            settingsFragment.show(getFragmentManager(), CUSTOMER_SETTINGS_FRAGMENT);
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadData(){
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
        mCustomerViewPager.setCurrentItem(0);
        loadData();
    }


}
