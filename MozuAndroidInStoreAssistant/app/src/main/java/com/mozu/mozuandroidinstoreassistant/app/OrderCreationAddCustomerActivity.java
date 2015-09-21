package com.mozu.mozuandroidinstoreassistant.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.mozu.api.contracts.commerceruntime.fulfillment.FulfillmentInfo;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.core.Contact;
import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerLookupFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.NewOrderActivity;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

public class OrderCreationAddCustomerActivity extends BaseActivity {

    private static final String ORDER_EXTRA_KEY = "ORDER";
    private static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    private static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    private static final String ORDER_CUSTOMER_EXTRA_KEY = "order_customer";
    private static final int CREATE_CUSTOMER = 1;
    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
    private Order mOrder;

    private int mTenantId;

    private int mSiteId;
    private CustomerAccount mCustomerAccount;


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
            getActionBar().setTitle("  " + getString(R.string.create_order));
        }

    }

    public void onSubmitClicked() {
        mOrder = new Order();
        mOrder.setCustomerAccountId(mCustomerAccount.getId());
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setBillingContact(getDefaultContact(mCustomerAccount, BILLING));
        mOrder.setBillingInfo(billingInfo);
        FulfillmentInfo fulfillmentInfo = new FulfillmentInfo();
        fulfillmentInfo.setFulfillmentContact(getDefaultContact(mCustomerAccount, SHIPPING));
        mOrder.setFulfillmentInfo(fulfillmentInfo);
        AndroidObservable.bindActivity(OrderCreationAddCustomerActivity.this, NewOrderManager.getInstance().createOrder(mTenantId, mSiteId, mOrder))
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        Intent intent = new Intent(OrderCreationAddCustomerActivity.this, NewOrderActivity.class);
                        intent.putExtra(ORDER_EXTRA_KEY, mOrder.getId());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Order order) {
                        mOrder = order;
                    }
                });
    }

    public Contact getContact(CustomerContact customerContact) {
        Contact contact = new Contact();
        contact.setAddress(customerContact.getAddress());
        contact.setEmail(customerContact.getEmail());
        contact.setFirstName(customerContact.getFirstName());
        contact.setLastNameOrSurname(customerContact.getLastNameOrSurname());
        contact.setPhoneNumbers(customerContact.getPhoneNumbers());
        return contact;
    }

    private Contact getDefaultContact(CustomerAccount mCustomerAccount, String contactType) {
        Contact defaultContact = null;
        for (CustomerContact contact : mCustomerAccount.getContacts()) {
            if (defaultContact == null) {
                defaultContact = getContact(contact);
            }
            for (ContactType type : contact.getTypes()) {
                if (type.getName().equalsIgnoreCase(contactType) && type.getIsPrimary()) {
                    defaultContact = getContact(contact);
                    return defaultContact;
                }
            }
        }

        return defaultContact;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ORDER_EXTRA_KEY, mOrder);
        outState.putInt(CURRENT_TENANT_ID, mTenantId);
        outState.putInt(CURRENT_SITE_ID, mSiteId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_CUSTOMER && resultCode == RESULT_OK) {
            mCustomerAccount = (CustomerAccount) data.getSerializableExtra(CustomerUpdateActivity.CUSTOMER);
            onSubmitClicked();
        }
    }
}
