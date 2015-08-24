package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerContactDataItem;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressFragment extends Fragment {
    private static String CUSTOMER_ACCOUNT = "customerAccount";
    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
    private final String SHIPPING_DEFAULT_TYPE = "Default Shipping";
    private final String BILLING_DEFAULT_TYPE = "Default Billing";
    private GridView mBillingGridView;
    private CustomerAddressAdapter mBillingAdapter;
    private List<CustomerContactDataItem> mBillingContacts;
    private List<CustomerContactDataItem> mShippingContacts;
    private CustomerAccount mCustomerAccount;
    private LoadingView mAddressLoading ;

    public static CustomerAddressFragment getInstance(CustomerAccount customerAccount){
        CustomerAddressFragment customerAddressFragment = new CustomerAddressFragment();
        Bundle b = new Bundle();
        b.putSerializable(CUSTOMER_ACCOUNT,customerAccount);
        customerAddressFragment.setArguments(b);
        return customerAddressFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        mBillingContacts = new ArrayList<>();
        mShippingContacts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_address_layout, container, false);
        updateContacts(mCustomerAccount);
        mAddressLoading = (LoadingView) fragmentView.findViewById(R.id.customer_address_loading);
        TextView billingHeader = (TextView) fragmentView.findViewById(R.id.billing_header);
        mBillingGridView = (GridView) fragmentView.findViewById(R.id.customer_address_grid);

        if (mBillingContacts.size() > 0 || mShippingContacts.size() >0) {
            mAddressLoading.success();

            if (mBillingContacts.size() > 0) {
                mBillingAdapter = new CustomerAddressAdapter(mBillingContacts);
                mBillingGridView.setAdapter(mBillingAdapter);
                billingHeader.setVisibility(View.GONE);
            } else {
                mBillingGridView.setVisibility(View.GONE);
                billingHeader.setVisibility(View.GONE);
            }

        } else {
            mAddressLoading.setError(getActivity().getResources().getString(R.string.empty_address));
        }

        return fragmentView;
    }


    protected void updateContacts(CustomerAccount customerAccount){
        List<CustomerContact> customerContacts = customerAccount.getContacts();
        if(customerContacts == null)
            return;

        for(CustomerContact contact:customerContacts){
            if(contact.getTypes().size() > 0 ) {
                for (ContactType type : contact.getTypes()) {
                    if (type.getName().equalsIgnoreCase(BILLING)) {
                        CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                        customerContactDataItem.setCustomerContact(contact);
                        if(type.getIsPrimary()) {
                            customerContactDataItem.setTypeMessage(BILLING_DEFAULT_TYPE);
                            mBillingContacts.add(0,customerContactDataItem);
                        }else{
                            mBillingContacts.add(customerContactDataItem);
                        }
                    } else if (type.getName().equalsIgnoreCase(SHIPPING)) {
                        CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                        customerContactDataItem.setCustomerContact(contact);
                        if(type.getIsPrimary()) {
                            customerContactDataItem.setTypeMessage(SHIPPING_DEFAULT_TYPE);
                            mBillingContacts.add(0,customerContactDataItem);
                        }else{
                            mBillingContacts.add(customerContactDataItem);
                        }
                    }
                }
            }else{
                CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                customerContactDataItem.setCustomerContact(contact);
                customerContactDataItem.setTypeMessage(null);
                mBillingContacts.add(customerContactDataItem);
            }
        }
    }

}
