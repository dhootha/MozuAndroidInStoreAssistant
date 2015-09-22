package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.paymentservice.PublicCard;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderPaymentManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;


public class AddPaymentDialogFragment extends DialogFragment {

    private static final String ORDER = "order";
    private Integer mTenantId;
    private Integer mSiteId;
    private View mView;

    @InjectView(R.id.card_name)
    EditText mCardName;
    @InjectView(R.id.card_amount)
    EditText mCardAmount;
    @InjectView(R.id.card_type)
    Spinner mCardType;
    @InjectView(R.id.card_number)
    EditText mCardNumber;
    @InjectView(R.id.card_expire_month)
    EditText mCardMonthExpiry;
    @InjectView(R.id.card_expire_year)
    EditText mCardExpireYear;
    @InjectView(R.id.card_cvv)
    EditText mCardCVV;
    @InjectView(R.id.add_payment_button)
    Button mAddPayment;
    @InjectView(R.id.cancel_button)
    Button mCancelButton;
    @InjectView(R.id.payment_loading)
    LinearLayout mPaymentLoading;
    private Order mOrder;


    public static AddPaymentDialogFragment getInstance(Order order) {
        AddPaymentDialogFragment addPaymentDialogFragment = new AddPaymentDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(ORDER, order);
        addPaymentDialogFragment.setArguments(b);
        return addPaymentDialogFragment;
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
        mView = inflater.inflate(R.layout.add_payment_detail, null);
        if (getArguments() != null) {
            mOrder = (Order) getArguments().getSerializable(ORDER);
        }
        ButterKnife.inject(this, mView);
        setUpViews();
        return mView;
    }

    private void setUpViews() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        mAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmitPayment();
            }
        });

    }

    private void validateAndSubmitPayment() {
        String cardName;
        Double cardAmount;
        String cardType;
        String cardNumber;
        Integer cardExpireMonth;
        Integer cardExpireYear;
        String cardCVV;
        if (TextUtils.isEmpty(cardName = mCardName.getText().toString())) {
            mCardName.setError("This Field is required");
            mCardName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mCardAmount.getText().toString())) {
            mCardAmount.setError("This Field is required");
            mCardAmount.requestFocus();
            return;
        }
        cardAmount = Double.parseDouble(mCardAmount.getText().toString());

        if (TextUtils.isEmpty(cardNumber = mCardNumber.getText().toString())) {
            mCardNumber.setError("This Field is required");
            mCardNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mCardMonthExpiry.getText().toString())) {
            mCardMonthExpiry.setError("This Field is required");
            mCardMonthExpiry.requestFocus();
            return;
        }
        try {
            cardExpireMonth = Integer.parseInt(mCardMonthExpiry.getText().toString());
        } catch (ClassCastException e) {
            mCardMonthExpiry.setError("Invalid Value");
            mCardMonthExpiry.requestFocus();
            return;
        }
        if (cardExpireMonth > 12 || cardExpireMonth < 0) {
            mCardMonthExpiry.setError("Invalid value");
            mCardMonthExpiry.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mCardExpireYear.getText().toString())) {
            mCardExpireYear.setError("This Field is required");
            mCardExpireYear.requestFocus();
            return;
        }
        cardExpireYear = Integer.parseInt(mCardExpireYear.getText().toString());

        if (TextUtils.isEmpty(cardCVV = mCardCVV.getText().toString())) {
            mCardCVV.setError("This Field is required");
            mCardCVV.requestFocus();
            return;
        }
        cardType = mCardType.getSelectedItem().toString();
        PublicCard card = new PublicCard();
        card.setCardHolderName(cardName);
        card.setCardNumber(cardNumber);
        card.setExpireMonth(cardExpireMonth);
        card.setExpireYear(cardExpireYear);
        card.setCardType(cardType);
        card.setCvv(cardCVV);

        mPaymentLoading.setVisibility(View.VISIBLE);
        AndroidObservable.bindFragment(AddPaymentDialogFragment.this, OrderPaymentManager.getInstance().getPaymentCreateObservable(mTenantId, mSiteId, card, mOrder, cardAmount))
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        getDialog().dismiss();
                        ((OrderDetailActivity) getActivity()).onRefresh();
                        mPaymentLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Failed to add payment.Please try later", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                        mPaymentLoading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Order order) {
                        Toast.makeText(getActivity(), "Payment added successfully", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
