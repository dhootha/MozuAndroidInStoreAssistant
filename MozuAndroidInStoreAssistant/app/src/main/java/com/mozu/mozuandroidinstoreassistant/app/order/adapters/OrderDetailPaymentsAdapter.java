package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.payments.BillingInfo;
import com.mozu.api.contracts.commerceruntime.payments.Payment;
import com.mozu.api.contracts.core.Contact;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.text.NumberFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailPaymentsAdapter extends ArrayAdapter<Payment> {


    private CapturePaymentListener mCapturePaymentListener;
    private NumberFormat mNumberFormat;

    public OrderDetailPaymentsAdapter(Context context, List<Payment> payments, CapturePaymentListener capturePaymentListener) {
        super(context, R.layout.payment_list_item);
        mCapturePaymentListener = capturePaymentListener;
        mNumberFormat = NumberFormat.getCurrencyInstance();
        addAll(payments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.payment_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Payment payment = getItem(position);
        viewHolder.paymentStatus.setText(payment.getStatus());
        viewHolder.paymentNameAddress.setText(getAddressString(payment));
        viewHolder.paymentMethod.setText(getPaymentMethod(payment));
        viewHolder.amountAuthorized.setText(mNumberFormat.format(payment.getAmountRequested()));
        viewHolder.amountCollected.setText(mNumberFormat.format(payment.getAmountCollected()));
        if (payment.getAvailableActions().contains("CapturePayment")) {
            viewHolder.captureAmount.setVisibility(View.VISIBLE);
            viewHolder.captureAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCapturePaymentListener.capturePayment(payment);
                }
            });
            ;
        } else {
            viewHolder.captureAmount.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder {
        @InjectView(R.id.payment_status)
        TextView paymentStatus;
        @InjectView(R.id.name_address)
        TextView paymentNameAddress;
        @InjectView(R.id.payment_method)
        TextView paymentMethod;
        @InjectView(R.id.amount_authorized)
        TextView amountAuthorized;
        @InjectView(R.id.amount_collected)
        TextView amountCollected;
        @InjectView(R.id.capture_amount)
        TextView captureAmount;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    public interface CapturePaymentListener {
        public void capturePayment(Payment payment);
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
        if (billingContact == null)
            return "N/A";
        com.mozu.api.contracts.core.Address address = billingContact.getAddress();
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

            if (!TextUtils.isEmpty(address.getCityOrTown())) {
                str.append(address.getCityOrTown().trim());
                str.append(",");
            }
            if (TextUtils.isEmpty(address.getStateOrProvince())) {
                str.append(address.getStateOrProvince());
                str.append(" ");
            }
            if (TextUtils.isEmpty(address.getPostalOrZipCode())) {
                str.append(address.getPostalOrZipCode());
                str.append(lineSeparator);
            }
            if (TextUtils.isEmpty(address.getCountryCode())) {
                str.append(address.getCountryCode());
                str.append(lineSeparator);
            }
        } else {
            str.append("N/A");
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
