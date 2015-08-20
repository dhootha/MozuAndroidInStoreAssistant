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
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerAddressOrderVerification;
import com.mozu.mozuandroidinstoreassistant.app.customer.CustomerLookupFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.NewOrderActivity;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;

import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chris_pound on 8/5/15.
 */
public class CustomerLookUpActivity extends BaseActivity implements CustomerLookupFragment.CustomerSelectionListener, CustomerAddressOrderVerification.VerifyCreateOrderListener {

    public static final String ORDER_EXTRA_KEY = "ORDER";
    public static final String CURRENT_TENANT_ID = "curTenantIdWhenActLoaded";
    public static final String CURRENT_SITE_ID = "curSiteIdWhenActLoaded";
    public static final String ORDER_CUSTOMER_EXTRA_KEY = "order_customer";
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
            getActionBar().setTitle("Create Order");
        }

    }

    @Override
    public void onCancelClicked() {
        finish();
    }

    @Override
    public void onSubmitClicked() {
        //todo create order
//        mOrder.setCustomerAccountId(mCustomerAccount.getId());
//        BillingInfo billingInfo = new BillingInfo();
//        billingInfo.setBillingContact(getDefaultContact(mCustomerAccount, BILLING));
//        mOrder.setBillingInfo(billingInfo);
//        FulfillmentInfo fulfillmentInfo = new FulfillmentInfo();
//        fulfillmentInfo.setFulfillmentContact(getDefaultContact(mCustomerAccount, SHIPPING));
//        mOrder.setFulfillmentInfo(fulfillmentInfo);
//        AndroidObservable.bindActivity(CustomerLookUpActivity.this, NewOrderManager.getInstance().getOrderCustomerUpdate(mTenantId, mSiteId, mOrder, mOrder.getId()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Order>() {
//                    @Override
//                    public void onCompleted() {
//                        Intent intent = new Intent(CustomerLookUpActivity.this, NewOrderActivity.class);
//                        intent.putExtra(ORDER_CUSTOMER_EXTRA_KEY, mCustomerAccount);
//                        intent.putExtra(ORDER_EXTRA_KEY, mOrder);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Order order) {
//
//                    }
//                });
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
        CustomerAddressOrderVerification fragment = CustomerAddressOrderVerification.getInstance(mCustomerAccount);
        fragment.setListener(this);
        fragment.show(getFragmentManager(), "verify");
    }
}
