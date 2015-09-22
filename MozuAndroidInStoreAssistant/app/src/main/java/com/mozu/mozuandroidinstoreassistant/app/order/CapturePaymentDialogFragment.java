package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.payments.PaymentAction;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderPaymentManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

public class CapturePaymentDialogFragment extends DialogFragment {

    private static final String PAYMENT = "payment";
    private Integer mTenantId;
    private Integer mSiteId;
    private View mView;

    @InjectView(R.id.capture_amount)
    EditText captureAmount;
    @InjectView(R.id.capture_button)
    Button mCaptureButton;
    @InjectView(R.id.cancel_button)
    Button mCancelButton;
    @InjectView(R.id.payment_loading)
    LinearLayout mPaymentLoading;
    private Payment mPayment;
    private onCaptureDoneListener onCaptureDoneListener;

    public static CapturePaymentDialogFragment getInstance(Payment payment) {
        CapturePaymentDialogFragment capturePaymentDialogFragment = new CapturePaymentDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(PAYMENT, payment);
        capturePaymentDialogFragment.setArguments(b);
        return capturePaymentDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mTenantId = userAuthenticationStateMachine.getTenantId();
        mSiteId = userAuthenticationStateMachine.getSiteId();
        if (getArguments() != null) {
            mPayment = (Payment) getArguments().getSerializable(PAYMENT);
        }
        mView = inflater.inflate(R.layout.payment_capture, null);
        ButterKnife.inject(this, mView);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PaymentAction action = new PaymentAction();
                action.setActionName("CapturePayment");
                action.setAmount(Double.parseDouble(captureAmount.getText().toString()));
                action.setCurrencyCode("USD");
                action.setExternalTransactionId(mPayment.getExternalTransactionId());
                mPaymentLoading.setVisibility(View.VISIBLE);
                AndroidObservable.bindFragment(CapturePaymentDialogFragment.this, OrderPaymentManager.getInstance().capturePaymentObservable(mTenantId, mSiteId, mPayment.getOrderId(), action, mPayment.getId()))
                        .subscribe(new Subscriber<Order>() {
                            @Override
                            public void onCompleted() {
                                mPaymentLoading.setVisibility(View.VISIBLE);
                                getDialog().dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                mPaymentLoading.setVisibility(View.VISIBLE);
                                getDialog().dismiss();
                            }

                            @Override
                            public void onNext(Order order) {
                                onCaptureDoneListener.onCaptureDone(order);
                            }
                        });

            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return mView;
    }


    public void setOnCaptureDoneListener(OrderDetailPaymentFragment onCaptureDoneListener) {
        this.onCaptureDoneListener = onCaptureDoneListener;
    }

    public interface onCaptureDoneListener {
        public void onCaptureDone(Order order);
    }


}
