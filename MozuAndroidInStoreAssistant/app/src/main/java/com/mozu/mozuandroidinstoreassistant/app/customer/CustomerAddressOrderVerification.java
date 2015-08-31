package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;

import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerContactDataItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris_pound on 8/20/15.
 */
public class CustomerAddressOrderVerification extends DialogFragment {
    @InjectView(R.id.submitOrder)
    public Button mOrderSubmit;
    @InjectView(R.id.cancel)
    public Button mCancel;
    private CustomerAccount mCustomerAccount;
    private VerifyCreateOrderListener mListener;
    private GridView mBillingGridView;
    private List<CustomerContactDataItem> mBillingContacts = new ArrayList<>();
    private List<CustomerContactDataItem> mShippingContacts = new ArrayList<>();
    private CustomerAddressAdapter mBillingAdapter;
    private final String SHIPPING_DEFAULT_TYPE = "Default Shipping";
    private final String BILLING_DEFAULT_TYPE = "Default Billing";
    private static final String BILLING = "billing";
    private static final String SHIPPING = "shipping";


    public static CustomerAddressOrderVerification getInstance(CustomerAccount customerAccount) {
        CustomerAddressOrderVerification fragment = new CustomerAddressOrderVerification();
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customerAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(VerifyCreateOrderListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_verify_order_creation, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable("customer");
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelClicked();
            }
        });
        mOrderSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSubmitClicked();
            }
        });
        updateContacts(mCustomerAccount);
        mBillingGridView = (GridView) view.findViewById(R.id.customer_address_grid);

        if (mBillingContacts.size() > 0 || mShippingContacts.size() > 0) {

            if (mBillingContacts.size() > 0) {
                mBillingAdapter = new CustomerAddressAdapter(mBillingContacts);
                mBillingGridView.setAdapter(mBillingAdapter);
            } else {
                mBillingGridView.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public interface VerifyCreateOrderListener {
        void onCancelClicked();

        void onSubmitClicked();
    }

    protected void updateContacts(CustomerAccount customerAccount) {
        List<CustomerContact> customerContacts = customerAccount.getContacts();
        if (customerContacts == null)
            return;

        for (CustomerContact contact : customerContacts) {
            if (contact.getTypes().size() > 0) {
                for (ContactType type : contact.getTypes()) {
                    if (type.getName().equalsIgnoreCase(BILLING)) {
                        CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                        customerContactDataItem.setCustomerContact(contact);
                        if (type.getIsPrimary()) {
                            customerContactDataItem.setTypeMessage(BILLING_DEFAULT_TYPE);
                            mBillingContacts.add(0, customerContactDataItem);
                        } else {
                            mBillingContacts.add(customerContactDataItem);
                        }
                    } else if (type.getName().equalsIgnoreCase(SHIPPING)) {
                        CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                        customerContactDataItem.setCustomerContact(contact);
                        if (type.getIsPrimary()) {
                            customerContactDataItem.setTypeMessage(SHIPPING_DEFAULT_TYPE);
                            mBillingContacts.add(0, customerContactDataItem);
                        } else {
                            mBillingContacts.add(customerContactDataItem);
                        }
                    }
                }
            } else {
                CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                customerContactDataItem.setCustomerContact(contact);
                customerContactDataItem.setTypeMessage(null);
                mBillingContacts.add(customerContactDataItem);
            }
        }
    }

}
