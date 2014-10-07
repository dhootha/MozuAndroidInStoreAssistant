package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Contact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.utils.DateUtils;

import java.text.NumberFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentInfoFragment extends DialogFragment {

    private static final String PAYMENT = "payment";
    private Payment mPayment;

    @InjectView(R.id.payment_bill_to) TextView mPaymentBillTo;
    @InjectView(R.id.payment_method) TextView mPaymentMethod;
    @InjectView(R.id.payment_status) TextView mPaymentStatus;
    @InjectView(R.id.payment_date) TextView mPaymentDate;
    @InjectView(R.id.payment_amount) TextView mPaymentAmount;

    public static PaymentInfoFragment getInstance(Payment payment){
        PaymentInfoFragment paymentInfoFragment = new PaymentInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable(PAYMENT,payment);
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
        ButterKnife.inject(this,view);
        setUpViews();
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
        mPaymentBillTo.setText(billTo);
        mPaymentStatus.setText(mPayment.getStatus());
        mPaymentMethod.setText(getPaymentMethod(mPayment));
        mPaymentDate.setText(DateUtils.getFormattedDateTime(mPayment.getBillingInfo().getAuditInfo().getCreateDate().getMillis()));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        mPaymentAmount.setText(numberFormat.format(mPayment.getAmountCollected()));
    }

    private String getPaymentMethod(Payment payment){

        BillingInfo billingInfo = payment.getBillingInfo();
        StringBuilder str = new StringBuilder();
        String lineSeparator = System.getProperty ("line.separator");

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

    private String getAddressString(Payment payment){
        String lineSeparator = System.getProperty ("line.separator");
        Contact billingContact =  payment.getBillingInfo().getBillingContact();
        Address address = billingContact.getAddress();
        StringBuilder str = new StringBuilder();
        str.append(billingContact.getFirstName()+" "+billingContact.getLastNameOrSurname());
        str.append(lineSeparator);
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

        str.append(address.getCityOrTown());
        str.append(",");
        str.append(address.getStateOrProvince());
        str.append(" ");
        str.append(address.getPostalOrZipCode());
        str.append(lineSeparator);
        str.append(address.getCountryCode());
        str.append(lineSeparator);

        String phone;
        if ((phone = payment.getBillingInfo().getBillingContact().getPhoneNumbers().getWork()) != null) {
            str.append(phone);
            str.append(lineSeparator);
        } else if ((phone = payment.getBillingInfo().getBillingContact().getPhoneNumbers().getMobile()) != null) {
            str.append(phone);
            str.append(lineSeparator);
        } else if ((phone = payment.getBillingInfo().getBillingContact().getPhoneNumbers().getHome()) != null) {
            str.append(phone);
            str.append(lineSeparator);
        }
        return str.toString();
    }

}
