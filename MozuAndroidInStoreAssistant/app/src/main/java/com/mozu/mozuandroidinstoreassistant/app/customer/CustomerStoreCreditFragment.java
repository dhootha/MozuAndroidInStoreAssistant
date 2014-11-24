package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.credit.Credit;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.StoreCreditFetcher;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerStoreCreditFragment extends Fragment {

    private static String CUSTOMER_ACCOUNT = "customerAccount";
    private CustomerAccount mCustomerAccount;
    private ListView mListView;
    private LoadingView mOrderLoading;
    private CustomerStoreCreditAdapter mAdapter;
    private rx.Observable<List<Credit>> mCreditObservable;
    private StoreCreditFetcher mCreditFetcher;

    public static CustomerStoreCreditFragment getInstance(CustomerAccount customerAccount){
        CustomerStoreCreditFragment customerStoreCreditFragment = new CustomerStoreCreditFragment();
        Bundle b = new Bundle();
        b.putSerializable(CUSTOMER_ACCOUNT,customerAccount);
        customerStoreCreditFragment.setArguments(b);
        return customerStoreCreditFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        mCreditFetcher = new StoreCreditFetcher();
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mCreditObservable = AndroidObservable.bindFragment(this, mCreditFetcher.getCreditsByCustomerId(userState.getTenantId(), userState.getSiteId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_storecredit_layout, container, false);
        mListView = (ListView) fragmentView.findViewById(R.id.customer_credit_list);
        mOrderLoading = (LoadingView)fragmentView.findViewById(R.id.customer_credit_loading);
        mAdapter = new CustomerStoreCreditAdapter(new ArrayList<Credit>());
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData(){
        mCreditFetcher.setCustomerId(mCustomerAccount.getId());
        mCreditObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CreditSubscriber());
    }

    private class CreditSubscriber implements rx.Observer<List<Credit>> {
        List<Credit> mCreditList = new ArrayList<Credit>();
        @Override
        public void onCompleted() {
            if (mCreditList.size() > 0) {
                mOrderLoading.success();
                mAdapter.setData(mCreditList);
                mAdapter.notifyDataSetChanged();
            } else {
                mOrderLoading.setError("No store credits to display");
            }
        }

        @Override
        public void onError(Throwable e) {
            mOrderLoading.setError(e.getMessage());
        }

        @Override
        public void onNext(List<Credit> creditList) {
            mCreditList = creditList;

        }
    }

}
