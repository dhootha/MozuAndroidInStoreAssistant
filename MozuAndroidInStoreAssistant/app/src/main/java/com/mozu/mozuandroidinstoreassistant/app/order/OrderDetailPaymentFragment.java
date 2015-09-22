package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailPaymentsAdapter;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class OrderDetailPaymentFragment extends Fragment implements OrderDetailPaymentsAdapter.CapturePaymentListener, CapturePaymentDialogFragment.onCaptureDoneListener {

    private Order mOrder;
    private Integer mTenantId;
    private Integer mSiteId;

    private NumberFormat mNumberFormat;

    public OrderDetailPaymentFragment() {

        // Required empty public constructor
        setRetainInstance(true);

        mNumberFormat = NumberFormat.getCurrencyInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_detail_payment_fragment, null);

        if (mOrder != null) {
            setOrderToViews(view);
        }
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mSiteId = mUserState.getSiteId();
        mTenantId = mUserState.getTenantId();
        return view;
    }

    @Override
    public void capturePayment(Payment payment) {
        CapturePaymentDialogFragment newOrderItemEditFragment = CapturePaymentDialogFragment.getInstance(payment);
        newOrderItemEditFragment.setOnCaptureDoneListener(this);
        newOrderItemEditFragment.show(getFragmentManager(), "");

    }

    @Override
    public void onCaptureDone(Order order) {
        ((OrderDetailActivity) getActivity()).onRefresh();
    }

    class PaymentsSort implements Comparator<Payment> {
        @Override
        public int compare(Payment p1, Payment p2) {
            if (p1.getAuditInfo().getCreateDate().getMillis() > p2.getAuditInfo().getCreateDate().getMillis())
                return -1;
            else if ((p2.getAuditInfo().getCreateDate().getMillis() > p1.getAuditInfo().getCreateDate().getMillis())) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private Double getTotalPayment(List<Payment> payments) {
        Double total = 0.0;
        for (Payment payment : payments) {
            total += payment.getAmountCollected();
        }
        return total;
    }


    private void setOrderToViews(View view) {
        ListView paymentList = (ListView) view.findViewById(R.id.payments_list);
        List<Payment> payments = mOrder.getPayments();
        Collections.sort(payments, new PaymentsSort());
        final OrderDetailPaymentsAdapter adapter = new OrderDetailPaymentsAdapter(getActivity(), payments, this);
        paymentList.setAdapter(adapter);

        TextView orderTotal = (TextView) view.findViewById(R.id.order_total);
        TextView paymentsReceived = (TextView) view.findViewById(R.id.payments_received);
        Button addPaymentButton = (Button) view.findViewById(R.id.add_payment);
        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPaymentDialogFragment newOrderItemEditFragment = AddPaymentDialogFragment.getInstance(mOrder);
                newOrderItemEditFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.ActivityDialogMozu);
                newOrderItemEditFragment.show(getFragmentManager(), "");
            }
        });
        TextView balance = (TextView) view.findViewById(R.id.balance);
        TextView status = (TextView) view.findViewById(R.id.payment_status);
        if (mOrder.getPaymentStatus() != null) {
            status.setText("Payment Status: " + mOrder.getPaymentStatus());
        } else {
            status.setText("Payment Status: N/A");
        }
        orderTotal.setText(mNumberFormat.format(mOrder.getTotal()));
        Double amountCollected = getTotalPayment(payments);
        paymentsReceived.setText(mNumberFormat.format(amountCollected));
        balance.setText(mNumberFormat.format(mOrder.getTotal() - amountCollected));
    }


    public void setOrder(Order order) {
        mOrder = order;
    }

}
