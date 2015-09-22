package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.commerceruntime.payments.PaymentAction;
import com.mozu.api.contracts.commerceruntime.payments.PaymentInteraction;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Contact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.OrderPaymentManager;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.text.NumberFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

public class PaymentInfoFragment extends DialogFragment {

    private static final String PAYMENT = "payment";
    private Payment mPayment;

    @InjectView(R.id.payment_bill_to)
    TextView mPaymentBillTo;
    @InjectView(R.id.capture_amount)
    TextView mCaptureButton;
    @InjectView(R.id.payment_method)
    TextView mPaymentMethod;
    @InjectView(R.id.payment_status)
    TextView mPaymentStatus;
    @InjectView(R.id.payment_date)
    TextView mPaymentDate;
    @InjectView(R.id.payment_amount)
    TextView mPaymentAmount;
    @InjectView(R.id.top_payment_type)
    TextView mTopPaymentType;
    @InjectView(R.id.top_payment_date)
    TextView mTopPaymentDate;
    @InjectView(R.id.top_payment_amount)
    TextView mTopPaymentAmount;
    @InjectView(R.id.trans_id_value)
    TextView mTransId;
    @InjectView(R.id.auth_id_value)
    TextView mAuthId;
    @InjectView(R.id.payment_close)
    ImageView mPaymentClose;
    private Integer mSiteId;
    private Integer mTenantId;


    public static PaymentInfoFragment getInstance(Payment payment) {
        PaymentInfoFragment paymentInfoFragment = new PaymentInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable(PAYMENT, payment);
        paymentInfoFragment.setArguments(b);
        return paymentInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mPayment = (Payment) b.getSerializable(PAYMENT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_info, null);
        ButterKnife.inject(this, view);
        setUpViews();
        UserAuthenticationStateMachine mUserState = UserAuthenticationStateMachineProducer.getInstance(getActivity());
        mSiteId = mUserState.getSiteId();
        mTenantId = mUserState.getTenantId();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void setUpViews() {
        String billTo = getAddressString(mPayment);
        mPaymentClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        mPaymentBillTo.setText(billTo);
        mPaymentStatus.setText(mPayment.getStatus());
        mPaymentMethod.setText(getPaymentMethod(mPayment));
        mPaymentDate.setText(DateUtils.getFormattedDateTime(mPayment.getBillingInfo().getAuditInfo().getCreateDate().getMillis()));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        if (mPayment.getStatus().equalsIgnoreCase("authorized")) {
            mPaymentAmount.setText(numberFormat.format(mPayment.getAmountRequested()));
            mCaptureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PaymentAction action = new PaymentAction();
                    action.setActionName("CapturePayment");
                    action.setAmount(1.00);
                    action.setCurrencyCode("USD");
                    action.setExternalTransactionId(mPayment.getExternalTransactionId());

                    AndroidObservable.bindFragment(PaymentInfoFragment.this, OrderPaymentManager.getInstance().capturePaymentObservable(mTenantId, mSiteId, mPayment.getOrderId(), action, mPayment.getId())).subscribe(new Subscriber<Order>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Order order) {

                        }
                    });
                }
            });
        } else {
            mPaymentAmount.setText(numberFormat.format(mPayment.getAmountCollected()));
        }
        mTopPaymentAmount.setText(numberFormat.format(mPayment.getAmountCollected()));
        mTopPaymentDate.setText(DateUtils.getFormattedDate(mPayment.getBillingInfo().getAuditInfo().getCreateDate().getMillis()));
        if (mPayment.getBillingInfo().getPaymentType().equalsIgnoreCase("CreditCard")) {
            mTopPaymentType.setText(mPayment.getBillingInfo().getCard().getCardNumberPartOrMask());
        } else {
            mTopPaymentType.setText(getPaymentMethod(mPayment));
        }

        if (mPayment.getPaymentServiceTransactionId() != null) {
            for (PaymentInteraction interaction : mPayment.getInteractions()) {
                if (mPayment.getId().equalsIgnoreCase(interaction.getPaymentId())) {
                    if (interaction.getGatewayTransactionId() != null && !interaction.getGatewayTransactionId().equals("0")) {
                        mTransId.setText(interaction.getGatewayTransactionId());
                    } else {
                        mTransId.setText("N/A");
                    }
                    break;
                }
            }
        }
        mAuthId.setText(mPayment.getId());
        mPayment.getInteractions();
    }

    private String getPaymentMethod(Payment payment) {

        BillingInfo billingInfo = payment.getBillingInfo();
        StringBuilder str = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        if (billingInfo.getPaymentType().equalsIgnoreCase("CreditCard")) {
            str.append(billingInfo.getCard().getPaymentOrCardType());
            str.append(lineSeparator);
            if (!TextUtils.isEmpty(billingInfo.getCard().getCardNumberPartOrMask())) {
                str.append(billingInfo.getCard().getCardNumberPartOrMask());
                str.append(lineSeparator);
            }
            str.append("Expiration: ");
            str.append(billingInfo.getCard().getExpireMonth());
            str.append("/");
            str.append(billingInfo.getCard().getExpireYear());
        } else {
            str.append(payment.getBillingInfo().getPaymentType());
        }

        return str.toString();
    }

    private String getAddressString(Payment payment) {
        String lineSeparator = System.getProperty("line.separator");
        Contact billingContact = payment.getBillingInfo().getBillingContact();
        Address address = billingContact.getAddress();
        StringBuilder str = new StringBuilder();
        str.append(billingContact.getFirstName()).append(" ").append(billingContact.getLastNameOrSurname());
        str.append(lineSeparator);
        if (address != null) {
            str.append(address.getAddress1());
            str.append(lineSeparator);
            if (!TextUtils.isEmpty(address.getAddress2())) {
                str.append(address.getAddress2());
                str.append(lineSeparator);
            }
            if (!TextUtils.isEmpty(address.getAddress3())) {
                str.append(address.getAddress3());
                str.append(lineSeparator);
            }
            if (!TextUtils.isEmpty(address.getAddress4())) {
                str.append(address.getAddress4());
                str.append(lineSeparator);
            }

            str.append(address.getCityOrTown().trim());
            str.append(",");
            str.append(address.getStateOrProvince());
            str.append(" ");
            str.append(address.getPostalOrZipCode());
            str.append(lineSeparator);
            str.append(address.getCountryCode());
            str.append(lineSeparator);
        } else {
            str.append(getString(R.string.not_available));
        }

        String phone;
        if (billingContact != null && billingContact.getPhoneNumbers() != null) {
            if ((phone = billingContact.getPhoneNumbers().getWork()) != null) {
                str.append(phone);
                str.append(lineSeparator);
            } else if ((phone = billingContact.getPhoneNumbers().getMobile()) != null) {
                str.append(phone);
                str.append(lineSeparator);
            } else if ((phone = billingContact.getPhoneNumbers().getHome()) != null) {
                str.append(phone);
                str.append(lineSeparator);
            }
        }
        return str.toString();
    }

}
