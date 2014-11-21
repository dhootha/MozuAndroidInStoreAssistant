package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailReturnsAdapter;
import com.mozu.mozuandroidinstoreassistant.app.models.ReturnItemForAdapterWrapper;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderReturnDetailDialogFragment;
import com.mozu.mozuandroidinstoreassistant.app.order.OrderReturnFetcher;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import java.util.ArrayList;
import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class OrderDetailReturnsFragment extends Fragment  {
    public static final String REPLACE = "Replace";
    public static final String REFUND = "Refund";
    public static final String UNKNOWN = "Unknown";
    private LinearLayout mListOfReturnsLayout;

    private Order mOrder;

    private TextView mTotal;
    private TextView mEmptyReturnsMessage;
    private ListView mItemList;
    private OrderReturnFetcher mOrderReturnFetcher;
    private rx.Observable<List<Return>> mOrderReturnObservable;
    private LoadingView mReturnLoading;
    private View mView;
    private OrderDetailReturnsAdapter mReturnAdapter;

    private static String RETURN_DETAIL_DIALOG_TAG = "returnDetailDialog";

    public OrderDetailReturnsFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderReturnFetcher = new OrderReturnFetcher();
        UserAuthenticationStateMachine userState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mOrderReturnObservable = AndroidObservable.bindFragment(this, mOrderReturnFetcher.getOrderReturns(userState.getTenantId(), userState.getSiteId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.order_detail_returns_fragment, null);
        mReturnLoading = (LoadingView) mView.findViewById(R.id.order_return_loading);
        mItemList = (ListView) mView.findViewById(R.id.returns_list);
        mReturnAdapter = new OrderDetailReturnsAdapter(getActivity(), new ArrayList<Return>());
        mItemList.setAdapter(mReturnAdapter);
        mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Return returnItem = mReturnAdapter.getItem(position);
                OrderReturnDetailDialogFragment dialogFragment = OrderReturnDetailDialogFragment.getInstance(returnItem);
                FragmentManager manager = getFragmentManager();
                dialogFragment.show(manager, RETURN_DETAIL_DIALOG_TAG);
            }
        });

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData(){
        mOrderReturnFetcher.setOrderNumber(mOrder.getId());
        mOrderReturnObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new OrderReturnSubscriber());
    }

    private class OrderReturnSubscriber implements rx.Observer<List<Return>> {
        List<Return> mReturnList = new ArrayList<Return>();
        @Override
        public void onCompleted() {
            if (mReturnList.size() > 0) {
                mReturnAdapter.setData(mReturnList);
                setOrderToViews(mReturnList);
                mReturnAdapter.notifyDataSetChanged();
                mReturnLoading.success();
            } else {
                mReturnLoading.setError("No returns data Available");
            }
        }

        @Override
        public void onError(Throwable e) {
            mReturnLoading.setError(e.getMessage());
        }

        @Override
        public void onNext(List<Return> returnList) {
            mReturnList = returnList;
        }
    }


    private void setOrderToViews(List<Return> returnList) {
        if (returnList == null) {
            mTotal.setText(getString(R.string.not_available));
            return;
        }

        mListOfReturnsLayout = (LinearLayout) mView.findViewById(R.id.list_of_returns_layout);
        mTotal = (TextView) mView.findViewById(R.id.total_returned);
        mEmptyReturnsMessage = (TextView) mView.findViewById(R.id.empty_returns_message);

        List<ReturnItemForAdapterWrapper> items = new ArrayList<ReturnItemForAdapterWrapper>();

        for (Return item: returnList) {
            String returnType = "";
            if (item.getCustomerInteractionType().equalsIgnoreCase(REPLACE)) {
                returnType = REPLACE;
            } else if (item.getCustomerInteractionType().equalsIgnoreCase(REFUND)) {
                returnType = REFUND;
            } else if (item.getCustomerInteractionType().equalsIgnoreCase(UNKNOWN)) {
                returnType = UNKNOWN;
            }

            for (ReturnItem ri: item.getItems()) {
                ReturnItemForAdapterWrapper wrapper = new ReturnItemForAdapterWrapper(ri, item.getAuditInfo().getCreateDate(), returnType);
                items.add(wrapper);
            }
        }
        if (items.size() < 1) {
            mListOfReturnsLayout.setVisibility(View.INVISIBLE);
            mEmptyReturnsMessage.setVisibility(View.VISIBLE);
        } else {
            mListOfReturnsLayout.setVisibility(View.VISIBLE);
            mEmptyReturnsMessage.setVisibility(View.GONE);
        }
        mTotal.setText(String.valueOf(items.size()));
    }


    public void setOrder(Order order) {
        mOrder = order;
    }

}
