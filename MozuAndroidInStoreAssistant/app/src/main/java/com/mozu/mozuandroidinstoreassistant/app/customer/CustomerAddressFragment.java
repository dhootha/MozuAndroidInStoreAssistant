package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerAccountAttribute;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.PrimaryAccountInfo;
import com.mozu.api.contracts.customer.CustomerAttribute;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressFragment extends Fragment {
    private ListView mListView;
    private CustomerAccountInfoAdapter mAdapter;
    private static String CUSTOMER_ACCOUNT = "customerAccount";

    private CustomerAccount mCustomerAccount;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_account_info, container, false);
        mListView = (ListView) fragmentView.findViewById(R.id.customer_info_list);
        mAdapter = new CustomerAccountInfoAdapter(getData(mCustomerAccount));
        mListView.setAdapter(mAdapter);
        return fragmentView;
    }


    public ArrayList<IData> getData(CustomerAccount customerAccount){
        ArrayList<IData> resultSet = new ArrayList<IData>();

        String customerSince = DateUtils.getFormattedDateTime(customerAccount.getAuditInfo().getCreateDate().getMillis());
        PrimaryAccountInfo accountInfo = new PrimaryAccountInfo();
        accountInfo.setCustomerSince(customerSince);
        if (customerAccount.getCommerceSummary() != null ){
            if(customerAccount.getCommerceSummary().getTotalOrderAmount() != null && customerAccount.getCommerceSummary().getTotalOrderAmount().getAmount() != null) {
                accountInfo.setLiveTimeValue(customerAccount.getCommerceSummary().getTotalOrderAmount().getAmount());
            }
            if (customerAccount.getCommerceSummary().getOrderCount() != null) {
                accountInfo.setTotalOrders(customerAccount.getCommerceSummary().getOrderCount());
            }
            accountInfo.setTotalVisits(customerAccount.getCommerceSummary().getVisitsCount());
        } else{
            accountInfo.setLiveTimeValue(null);
            accountInfo.setTotalOrders(null);
            accountInfo.setTotalVisits(null);
        }

        resultSet.add(accountInfo);

        List<CustomerAttribute> attributes= customerAccount.getAttributes();
        if (attributes != null) {
            for (CustomerAttribute attribute : attributes) {
                CustomerAccountAttribute customerAccountAttribute = new CustomerAccountAttribute();
                customerAccountAttribute.setProperty(attribute.getFullyQualifiedName());
                resultSet.add(customerAccountAttribute);
            }
        }

        return resultSet;

    }



}
