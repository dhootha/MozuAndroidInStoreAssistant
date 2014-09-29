package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.LoginActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailSectionPagerAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.OrderDetailLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RetrieveCustomerAsyncTask;
import com.viewpagerindicator.TabPageIndicator;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerDetailActivity extends Activity {

    private View mView;
    private ViewPager mCustomerViewPager;
    private TabPageIndicator mTabPageIndicator;
    private CustomerFragmentAdapter mCustomerFragmentAdapter;
    private CustomerAccount mCustomer;
    public static final String CUSTOMER_ID = "customerId";

    @InjectView(R.id.customer_name) TextView mCustomerName;
    @InjectView(R.id.customer_email) TextView mCustomerEmail;
    @InjectView(R.id.customer_newsletter) TextView mCustomerNewsletter;
    @InjectView(R.id.customer_tax_exempt) TextView mCustomerTaxExempt;
    @InjectView(R.id.customer_group) TextView mCustomerGroup;
    @InjectView(R.id.customer_id) TextView mCustomerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mCustomer = (CustomerAccount) getIntent().getSerializableExtra(CUSTOMER_ID);
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        mView = inflater.inflate(R.layout.activity_customer_detail,null);
        setContentView(mView);
        ButterKnife.inject(this);
        mCustomerFragmentAdapter = new CustomerFragmentAdapter(getFragmentManager(),mCustomer);
        mCustomerViewPager = (ViewPager)mView.findViewById(R.id.customer_detail_sections_viewpager);
        mCustomerViewPager.setAdapter(mCustomerFragmentAdapter);
        mTabPageIndicator =  (TabPageIndicator)mView.findViewById(R.id.customer_detail_sections_tab);
        mTabPageIndicator.setViewPager(mCustomerViewPager);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("");
        mCustomerName.setText(mCustomer.getFirstName() + "  " + mCustomer.getLastName());
        mCustomerEmail.setText(mCustomer.getEmailAddress());
        if (mCustomer.getTaxExempt() != null) {
            mCustomerTaxExempt.setText(mCustomer.getTaxExempt() ? "Yes" : "No");
        }else{
            mCustomerTaxExempt.setText("N/A");
        }
        if (mCustomer.getAcceptsMarketing() != null) {
            mCustomerNewsletter.setText(mCustomer.getAcceptsMarketing() ? "Yes" : "No");
        }else{
            mCustomerNewsletter.setText("N/A");
        }
        if (mCustomer.getCompanyOrOrganization() != null) {
            mCustomerGroup.setText(mCustomer.getCompanyOrOrganization());
        } else {
            mCustomerGroup.setText("N/A");
        }
        mCustomerId.setText(String.valueOf(mCustomer.getId()));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            UserAuthenticationStateMachineProducer.getInstance(getApplicationContext()).getCurrentUserAuthState().signOutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*

    @Override
    public void onRefresh(){

        mTabIndicator.setCurrentItem(0);
        Loader orderLoader = getLoaderManager().getLoader(LOADER_ORDER_DETAIL);
        orderLoader.reset();
        orderLoader.startLoading();
        orderLoader.forceLoad();

    }

    */
}
