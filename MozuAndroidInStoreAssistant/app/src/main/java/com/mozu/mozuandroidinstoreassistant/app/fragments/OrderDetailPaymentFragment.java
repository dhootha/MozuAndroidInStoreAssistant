package com.mozu.mozuandroidinstoreassistant.app.fragments;

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
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPaymentsAdapter;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class OrderDetailPaymentFragment extends Fragment {

    private Order mOrder;

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

        return view;
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

    private Double getTotalPayment(List<Payment> payments){
        Double total = 0.0;
        for(Payment payment:payments){
            total += payment.getAmountCollected();
        }
        return total;
    }


    private void setOrderToViews(View view) {
        ListView paymentList = (ListView) view.findViewById(R.id.payments_list);
        List<Payment> payments = mOrder.getPayments();
        Collections.sort(payments,new PaymentsSort());
        final OrderDetailPaymentsAdapter adapter = new OrderDetailPaymentsAdapter(getActivity(), payments);
        paymentList.setAdapter(adapter);
        TextView emptyView = (TextView) view.findViewById(R.id.empty_payments_message);
        paymentList.setEmptyView(emptyView);

        if (mOrder.getPayments() == null || mOrder.getPayments().size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        TextView orderTotal = (TextView) view.findViewById(R.id.order_total);
        TextView paymentsReceived = (TextView) view.findViewById(R.id.payments_received);
        TextView balance = (TextView) view.findViewById(R.id.balance);
        TextView status = (TextView) view.findViewById(R.id.status);
        if (mOrder.getPaymentStatus() != null) {
            status.setText(mOrder.getPaymentStatus());
        } else {
            status.setText("N/A");
        }
        orderTotal.setText(mNumberFormat.format(mOrder.getTotal()));
        Double amountCollected = getTotalPayment(payments);
        paymentsReceived.setText(mNumberFormat.format(amountCollected));
        balance.setText(mNumberFormat.format(mOrder.getTotal() - amountCollected));
        paymentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FragmentManager manager = getFragmentManager();
                Payment payment = adapter.getItem(position);
                PaymentInfoFragment frag = PaymentInfoFragment.getInstance(payment);
                frag.show(manager,"payment_info");
            }
        });
    }



    public void setOrder(Order order) {
        mOrder = order;
    }

}
