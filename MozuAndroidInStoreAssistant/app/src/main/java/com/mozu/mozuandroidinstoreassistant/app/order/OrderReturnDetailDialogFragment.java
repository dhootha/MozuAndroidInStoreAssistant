package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.returns.Return;
import com.mozu.api.contracts.commerceruntime.returns.ReturnItem;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.IData;
import com.mozu.mozuandroidinstoreassistant.app.data.order.BottomRowItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderRefundHeaderItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnHeaderDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.OrderReturnTitleDataItem;
import com.mozu.mozuandroidinstoreassistant.app.data.order.TopRowItem;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderReturnDetailDialogFragment extends DialogFragment {

    private static final String RETURN = "return";
    private Return mReturn;
    private View mView;
    private ListView mReturnListView;
    private OrderDetailReturnDialogAdapter mOrderDetailReturnDialogAdapter;
    private ImageView closeImageView;


    public static OrderReturnDetailDialogFragment getInstance(Return orderReturn) {
        OrderReturnDetailDialogFragment orderReturnDetailFragment = new OrderReturnDetailDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(RETURN, orderReturn);
        orderReturnDetailFragment.setArguments(b);
        return orderReturnDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mReturn = (Return) b.getSerializable(RETURN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.order_return_info, null);
        closeImageView = (ImageView)mView.findViewById(R.id.return_close);
        mReturnListView = (ListView) mView.findViewById(R.id.returns_list);
        mOrderDetailReturnDialogAdapter = new OrderDetailReturnDialogAdapter(getActivity(), setUpData());
        mReturnListView.setAdapter(mOrderDetailReturnDialogAdapter);
        mReturnListView.setDivider(null);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpData();
    }

    private List<IData> setUpData() {
        List<IData> resultList = new ArrayList<IData>();
        if (mReturn != null && mReturn.getItems().size() > 0) {
            OrderReturnTitleDataItem orderReturnTitleDataItem = new OrderReturnTitleDataItem("Returns");
            resultList.add(orderReturnTitleDataItem);
            resultList.add(new TopRowItem());
            resultList.add(new OrderReturnHeaderDataItem());
            for (ReturnItem returnItem : mReturn.getItems()) {
                OrderReturnDataItem orderReturnDataItem = new OrderReturnDataItem(returnItem);
                resultList.add(orderReturnDataItem);
            }
            resultList.add(new BottomRowItem());
        }
        if (mReturn.getRmaDeadline() != null) {
            resultList.add(new OrderReturnTitleDataItem("Return Due on: "+DateUtils.getFormattedDate(mReturn.getRmaDeadline().getMillis())));
        }else{
            resultList.add(new OrderReturnTitleDataItem("Return Due on: N/A"));
        }

        if (mReturn.getPayments() != null && mReturn.getPayments().size() > 0) {
            OrderReturnTitleDataItem orderReturnTitleDataItem = new OrderReturnTitleDataItem("Refunds");
            resultList.add(orderReturnTitleDataItem);
            resultList.add(new TopRowItem());
            resultList.add(new OrderRefundHeaderItem());
            for (Payment payment : mReturn.getPayments()) {
                OrderRefundDataItem orderRefundDataItem = new OrderRefundDataItem(payment);
                resultList.add(orderRefundDataItem);
            }
            resultList.add(new BottomRowItem());
        }
        return resultList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
