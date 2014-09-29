package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderFetcher;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerOrderHistoryFragment extends Fragment {

    private static String CUSTOMER_ACCOUNT = "customerAccount";
    private CustomerAccount mCustomerAccount;
    private ListView mListView;
    private LoadingView mOrderLoading;
    private CustomerOrderHistoryAdapter mAdapter;
    private rx.Observable<List<Order>> mOrderObservable;
    OrderFetcher mOrderFetcher;

    public static CustomerOrderHistoryFragment getInstance(CustomerAccount customerAccount){
        CustomerOrderHistoryFragment customerOrderHistoryFragment = new CustomerOrderHistoryFragment();
        Bundle b = new Bundle();
        b.putSerializable(CUSTOMER_ACCOUNT,customerAccount);
        customerOrderHistoryFragment.setArguments(b);
        return customerOrderHistoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        mOrderFetcher = new OrderFetcher();
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mOrderObservable = AndroidObservable.bindFragment(this, mOrderFetcher.getOrdersByCustomerId(userState.getTenantId(), userState.getSiteId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.customer_order_history, container, false);
        mListView = (ListView) fragmentView.findViewById(R.id.customer_order_list);
        mOrderLoading = (LoadingView)fragmentView.findViewById(R.id.orderhistory_loading);
        mAdapter = new CustomerOrderHistoryAdapter(new ArrayList<Order>());
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
        mOrderFetcher.setCustomerId(mCustomerAccount.getId());
        mOrderObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new OrderSubscriber());
    }

    private class OrderSubscriber implements rx.Observer<List<Order>> {
        List<Order> mOrderList = new ArrayList<Order>();
        @Override
        public void onCompleted() {
            if (mOrderList.size() > 0) {
                mOrderLoading.success();
                mAdapter.setData(mOrderList);
                mAdapter.notifyDataSetChanged();
            } else {
                mOrderLoading.setError("No Content Available");
            }
        }

        @Override
        public void onError(Throwable e) {
            mOrderLoading.setError(e.getMessage());
        }

        @Override
        public void onNext(List<Order> orderList) {
           mOrderList = orderList;
        }
    }

}
