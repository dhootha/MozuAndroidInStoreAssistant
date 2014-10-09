package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.*;
import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.products.BundledProduct;
import com.mozu.api.contracts.commerceruntime.products.Product;
import com.mozu.api.contracts.commerceruntime.products.ProductOption;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Contact;
import com.mozu.api.contracts.productruntime.ProductOptionValue;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPackageItemAdapter;
import com.mozu.mozuandroidinstoreassistant.app.htmlutils.HTMLTagHandler;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.views.NoUnderlineClickableSpan;
import com.mozu.mozuandroidinstoreassistant.app.views.ProductOptionsLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PackageInfoDialogFragment extends DialogFragment {

    private FulfillmentItem mFulfillmentItem;

    @InjectView(R.id.package_name) TextView mPackageName;
    @InjectView(R.id.address) TextView mAddress;
    @InjectView(R.id.shipping) TextView mShipping;
    @InjectView(R.id.packaging_type) TextView mPackagingType;
    @InjectView(R.id.tracking_number) TextView mTrackingNumber;
    @InjectView(R.id.status) TextView mStatus;
    @InjectView(R.id.ship_date) TextView mShipDate;
    @InjectView(R.id.package_items_list) ListView mList;

    private Integer mTenantId;
    private Integer mSiteId;

    public PackageInfoDialogFragment() {
        // Required empty public constructor

        setStyle(STYLE_NO_TITLE, 0);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.package_dialog_fragment, null);

        ButterKnife.inject(this, view);

        if (mFulfillmentItem != null) {
            setFulfillmentItemViews();
        }

        return view;
    }

    private void setFulfillmentItemViews() {

        setPackageItems();

        mPackageName.setText(mFulfillmentItem.getPackageNumber());

        if (mFulfillmentItem.getOrderPackage() != null) {
            Package orderPackage = mFulfillmentItem.getOrderPackage();

            if (orderPackage.getShippingMethodName() != null) {

                mShipping.setText(orderPackage.getShippingMethodName());
            }

            if (orderPackage.getPackagingType() != null) {

                mPackagingType.setText(orderPackage.getPackagingType());
            }

            if (orderPackage.getTrackingNumber() != null) {

                mTrackingNumber.setText(orderPackage.getTrackingNumber());
            }

            if (orderPackage.getStatus() != null) {

                mStatus.setText(orderPackage.getStatus());
            }

            if (orderPackage.getFulfillmentDate() != null) {
                android.text.format.DateFormat dateFormat= new android.text.format.DateFormat();
                String date = dateFormat.format("MM/dd/yy  hh:mm a", new Date(orderPackage.getFulfillmentDate().getMillis())).toString();

                mShipDate.setText(date);
            }
        }

        if (mFulfillmentItem.getShipment() != null) {
            Shipment shipment = mFulfillmentItem.getShipment();

            if (shipment.getDestinationAddress() != null) {
                Contact contact = shipment.getDestinationAddress();

                mAddress.setText(contact.getFirstName() + " " + contact.getLastNameOrSurname() + "\n" + constructAddress(contact.getAddress()));
            }

            if (shipment.getTrackingNumber() != null) {

                mTrackingNumber.setText(shipment.getTrackingNumber());
            }
        }

    }

    private void setPackageItems() {

        if (mFulfillmentItem.getOrderPackage() == null || mFulfillmentItem.getOrderPackage().getItems() == null || mFulfillmentItem.getOrderPackage().getItems().size() < 1) {
            return;
        }

        OrderDetailPackageItemAdapter adapter = new OrderDetailPackageItemAdapter(getActivity(), mFulfillmentItem.getOrderPackage().getItems(), mTenantId, mSiteId);

        mList.setAdapter(adapter);

    }

    private String constructAddress(Address address) {
        if (address == null) {
            return "";
        }

        String addressString = "";
        if (!TextUtils.isEmpty(address.getAddress1())) {
            addressString += address.getAddress1();
        }

        if (!TextUtils.isEmpty(address.getAddress2())) {
            addressString += "\n" + address.getAddress2();
        }

        if (!TextUtils.isEmpty(address.getAddress3())) {
            addressString += "\n" + address.getAddress3();
        }

        if (!TextUtils.isEmpty(address.getAddress4())) {
            addressString += "\n" + address.getAddress4();
        }

        if (!TextUtils.isEmpty(address.getCityOrTown())) {
            addressString += "\n" + address.getCityOrTown();
        }

        if (!TextUtils.isEmpty(address.getStateOrProvince())) {
            addressString += ", " + address.getStateOrProvince();
        }

        if (!TextUtils.isEmpty(address.getPostalOrZipCode())) {
            addressString += " " + address.getPostalOrZipCode();
        }

        return addressString;
    }

    public void setFulfillmentItem(FulfillmentItem fulfillmentItem) {

        mFulfillmentItem = fulfillmentItem;
    }

    public void setTenantAndSiteId(Integer tenantId, Integer siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;
    }
}
