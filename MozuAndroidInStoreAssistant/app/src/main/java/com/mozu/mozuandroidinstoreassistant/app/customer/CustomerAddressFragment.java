package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerContactDataItem;
import com.mozu.mozuandroidinstoreassistant.app.views.ExpandedGridView;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressFragment extends Fragment {
    private ExpandedGridView mBillingGridView;
    private CustomerAddressAdapter mBillingAdapter;
    private static String CUSTOMER_ACCOUNT = "customerAccount";
    private ExpandedGridView mShippingGridView;
    private CustomerAddressAdapter mShippingAdapter;
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
        mBillingContacts = new ArrayList<CustomerContactDataItem>();
        mShippingContacts = new ArrayList<CustomerContactDataItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_address_layout, container, false);
        updateContacts(mCustomerAccount);
        mAddressLoading = (LoadingView) fragmentView.findViewById(R.id.customer_address_loading);
        TextView billingHeader = (TextView) fragmentView.findViewById(R.id.billing_header);
        TextView shippingHeader = (TextView) fragmentView.findViewById(R.id.shipping_header);
        mBillingGridView = (ExpandedGridView) fragmentView.findViewById(R.id.customer_address_grid);
        mShippingGridView = (ExpandedGridView) fragmentView.findViewById(R.id.customer_shipping_address_grid);


        if (mBillingContacts.size() > 0 || mShippingContacts.size() >0) {
            mAddressLoading.success();

            if (mBillingContacts.size() > 0) {

                mBillingAdapter = new CustomerAddressAdapter(mBillingContacts);
                mBillingGridView.setAdapter(mBillingAdapter);
            } else {
                mBillingGridView.setVisibility(View.GONE);
                billingHeader.setVisibility(View.GONE);
            }

            if (mShippingContacts.size() > 0) {

                mShippingAdapter = new CustomerAddressAdapter(mShippingContacts);
                mShippingGridView.setAdapter(mShippingAdapter);
            } else {
                mShippingGridView.setVisibility(View.GONE);
                shippingHeader.setVisibility(View.GONE);
            }
        } else {
            mAddressLoading.setError("No Content Available");
        }

        return fragmentView;
    }


    public void updateContacts(CustomerAccount customerAccount){
        List<CustomerContact> customerContacts = customerAccount.getContacts();
        if(customerContacts == null)
            return;

        for(CustomerContact contact:customerContacts){
            for(ContactType type: contact.getTypes()){
                if(type.getName().equalsIgnoreCase("billing")){
                    CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                    customerContactDataItem.setCustomerContact(contact);
                    if (type.getIsPrimary()){
                        customerContactDataItem.setPrimary(true);
                    }else{
                        customerContactDataItem.setPrimary(false);
                    }
                    mBillingContacts.add(customerContactDataItem);
                }else if(type.getName().equalsIgnoreCase("shipping")){
                    CustomerContactDataItem customerContactDataItem = new CustomerContactDataItem();
                    customerContactDataItem.setCustomerContact(contact);
                    if (type.getIsPrimary()){
                        customerContactDataItem.setPrimary(true);
                    }else{
                        customerContactDataItem.setPrimary(false);
                    }
                    mShippingContacts.add(customerContactDataItem);
                }
            }
        }
    }

}