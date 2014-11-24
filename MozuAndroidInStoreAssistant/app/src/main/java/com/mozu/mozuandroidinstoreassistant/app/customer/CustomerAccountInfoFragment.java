package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerAttribute;
import com.mozu.api.contracts.customer.CustomerAttributeCollection;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAccountFetcher;
import com.mozu.mozuandroidinstoreassistant.app.data.EmptyRowDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerAccountAttribute;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerOverviewDataItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerAccountInfoFragment extends Fragment {
    private ListView mListView;
    private CustomerAccountInfoAdapter mAdapter;
    private static String CUSTOMER_ACCOUNT = "customerAccount";

    private CustomerAccount mCustomerAccount;
    private rx.Observable<CustomerAttributeCollection> mCustomerObservable;
    private CustomerAccountFetcher mCustomerAccountFetcher;
    private LoadingView mCustomerAttributeLoading;

    public static CustomerAccountInfoFragment getInstance(CustomerAccount customerAccount) {
        CustomerAccountInfoFragment customerAccountInfoFragment = new CustomerAccountInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable(CUSTOMER_ACCOUNT, customerAccount);
        customerAccountInfoFragment.setArguments(b);
        return customerAccountInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mCustomerAccountFetcher = new CustomerAccountFetcher();
        mCustomerObservable = AndroidObservable.bindActivity(getActivity(), mCustomerAccountFetcher.getCustomerAccountAttributes(userState.getTenantId(), userState.getSiteId()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_account_info, container, false);
        mCustomerAttributeLoading = (LoadingView) fragmentView.findViewById(R.id.accountinfo_loading);
        mListView = (ListView) fragmentView.findViewById(R.id.customer_info_list);
        mAdapter = new CustomerAccountInfoAdapter(new ArrayList<IData>());
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private class AttributeFetcher implements rx.Observer<CustomerAttributeCollection> {
        List<CustomerAttribute> mCustomerAttributeList = new ArrayList<CustomerAttribute>();
        @Override
        public void onCompleted() {
            mAdapter.setData(getData(mCustomerAttributeList));
            mAdapter.notifyDataSetChanged();
            mCustomerAttributeLoading.success();
        }

        @Override
        public void onError(Throwable e) {
            mAdapter.setData(getData(mCustomerAttributeList));
            mCustomerAttributeLoading.success();
        }

        @Override
        public void onNext(CustomerAttributeCollection customerAttributeCollection) {
            mCustomerAttributeList = customerAttributeCollection.getItems();
        }
    }
    private void loadData(){
        mCustomerAccountFetcher.setCustomerId(mCustomerAccount.getId());
        mCustomerObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new AttributeFetcher());
    }

    protected ArrayList<IData> getData(List<CustomerAttribute> customerAttributes) {
        ArrayList<IData> resultSet = new ArrayList<IData>();

        CustomerOverviewDataItem dataItem = new CustomerOverviewDataItem(getActivity().getResources().getString(R.string.customer_email), mCustomerAccount.getEmailAddress(),
                getActivity().getResources().getString(R.string.customer_newsletter), (mCustomerAccount.getAcceptsMarketing() != null && mCustomerAccount.getAcceptsMarketing()) ? getString(R.string.yes) : getString(R.string.no));


        resultSet.add(dataItem);
        dataItem = new CustomerOverviewDataItem(getActivity().getResources().getString(R.string.customer_segments), mCustomerAccount.getCompanyOrOrganization(),
                getActivity().getResources().getString(R.string.customer_tax_exmept), (mCustomerAccount.getTaxExempt() != null && mCustomerAccount.getTaxExempt())? getString(R.string.yes) : getString(R.string.no));

        resultSet.add(dataItem);

        List<CustomerAttribute> attributes = customerAttributes;
        if (attributes != null && attributes.size()>0) {
            resultSet.add(new EmptyRowDataItem());
            for (CustomerAttribute attribute : attributes) {
                CustomerAccountAttribute customerAccountAttribute = new CustomerAccountAttribute();
                customerAccountAttribute.setProperty(getPropertyValue(attribute.getFullyQualifiedName()));
                customerAccountAttribute.setValue(getValue(attribute.getValues()));
                resultSet.add(customerAccountAttribute);
            }
            resultSet.add(new EmptyRowDataItem());
        }
        return resultSet;
    }

    private String getPropertyValue(String fullyQualifiedName) {
        String delimiter = getResources().getString(R.string.attribute_delimiter);
        if (!TextUtils.isEmpty(fullyQualifiedName)) {
            return fullyQualifiedName.substring(fullyQualifiedName.indexOf(delimiter)+1, fullyQualifiedName.length()).toUpperCase();
        } else {
            return "";
        }
    }

    private String getValue(List<Object> values) {
        StringBuilder resultValue = new StringBuilder();
        for (Object obj : values) {
            if (resultValue.length() != 0) {
                resultValue.append(",");
            }
            try {
                resultValue.append(String.valueOf(obj));
            } catch (Exception e) {
                return null;
            }

        }
        return resultValue.toString();
    }


}
