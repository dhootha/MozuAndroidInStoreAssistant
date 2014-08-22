package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailReturnsAdapter;
import com.mozu.mozuandroidinstoreassistant.app.loaders.ReturnLoader;
import com.mozu.mozuandroidinstoreassistant.app.models.ReturnItemForAdapterWrapper;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailReturnsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Return>> {

    private static final int LOADER_ORDER_DETAIL = 141;
    public static final String REPLACE = "Replace";
    public static final String REFUND = "Refund";
    public static final String UNKNOWN = "Unknown";

    private LinearLayout mListOfReturnsLayout;

    private Order mOrder;
    private List<Return> mReturns;

    private TextView mReplacedTotal;
    private TextView mRefundedTotal;
    private TextView mTotal;
    private TextView mEmptyReturnsMessage;

    private ListView mItemList;

    private int mTenantId;
    private int mSiteId;

    public OrderDetailReturnsFragment() {
        // Required empty public constructor
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_returns_fragment, null);

        if (mOrder != null) {
            getLoaderManager().initLoader(LOADER_ORDER_DETAIL, savedInstanceState, this).forceLoad();
        }

        return view;
    }

    private void setOrderToViews(View view) {
        if (view == null || mReturns == null) {
            return;
        }

        mListOfReturnsLayout = (LinearLayout) view.findViewById(R.id.list_of_returns_layout);
        mReplacedTotal = (TextView) view.findViewById(R.id.replaced_total);
        mRefundedTotal = (TextView) view.findViewById(R.id.refunded_total);
        mTotal = (TextView) view.findViewById(R.id.total_returned);
        mEmptyReturnsMessage = (TextView) view.findViewById(R.id.empty_returns_message);

        List<ReturnItemForAdapterWrapper> items = new ArrayList<ReturnItemForAdapterWrapper>();
        int replacedCount = 0;
        int refundedCount = 0;

        for (Return item: mReturns) {
            String returnType = new String();

            if (item.getCustomerInteractionType().equalsIgnoreCase(REPLACE)) {
                replacedCount += item.getReturnNumber();
                returnType = REPLACE;
            } else if (item.getCustomerInteractionType().equalsIgnoreCase(REFUND)) {
                refundedCount += item.getReturnNumber();
                returnType = REFUND;
            } else if (item.getCustomerInteractionType().equalsIgnoreCase(UNKNOWN)) {
                returnType = UNKNOWN;
            }

            for (ReturnItem ri: item.getItems()) {
                ReturnItemForAdapterWrapper wrapper = new ReturnItemForAdapterWrapper(ri, item.getAuditInfo().getCreateDate(), returnType);
                items.add(wrapper);
            }
        }

        mItemList = (ListView) view.findViewById(R.id.returns_list);
        mItemList.setAdapter(new OrderDetailReturnsAdapter(getActivity(), items));

        if (replacedCount + refundedCount < 1) {
            mListOfReturnsLayout.setVisibility(View.INVISIBLE);
            mEmptyReturnsMessage.setVisibility(View.VISIBLE);
        } else {
            mListOfReturnsLayout.setVisibility(View.VISIBLE);
            mEmptyReturnsMessage.setVisibility(View.GONE);
        }

        mReplacedTotal.setText(String.valueOf(replacedCount));
        mRefundedTotal.setText(String.valueOf(refundedCount));
        mTotal.setText(String.valueOf(replacedCount + refundedCount));
    }


    public void setOrder(Order order) {
        mOrder = order;
    }


    @Override
    public Loader<List<Return>> onCreateLoader(int id, Bundle args) {
        return new ReturnLoader(getActivity(), mTenantId, mSiteId, mOrder.getId());
    }

    @Override
    public void onLoadFinished(Loader<List<Return>> loader, List<Return> data) {
        mReturns = data;

        setOrderToViews(getView());
    }

    @Override
    public void onLoaderReset(Loader<List<Return>> loader) {

    }

    public void setTenantId(int tenantId) {
        mTenantId = tenantId;
    }

    public void setSiteId(int siteId) {
        mSiteId = siteId;
    }
}
