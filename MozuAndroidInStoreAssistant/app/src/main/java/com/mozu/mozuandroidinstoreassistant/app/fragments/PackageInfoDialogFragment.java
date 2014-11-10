package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.api.contracts.commerceruntime.fulfillment.PackageItem;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Contact;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPackageItemAdapter;
import com.mozu.mozuandroidinstoreassistant.app.models.FulfillmentItem;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.utils.ProductUtils;
import com.mozu.mozuandroidinstoreassistant.app.utils.ShipperUtils;

import java.util.Date;

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
    @InjectView(R.id.package_info_close) ImageView mPackageInfoClose;

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

        mList.setDivider(null);
        if (mFulfillmentItem != null) {
            setFulfillmentItemViews();
        }
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PackageItem item = (PackageItem) adapterView.getItemAtPosition(position);
                String productCode = ProductUtils.getPackageorPickupProductCode(item.getProductCode());
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, productCode);
                intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());
                startActivity(intent);

            }
        });

        return view;
    }

    private void setFulfillmentItemViews() {

        setPackageItems();

        mPackageName.setText(mFulfillmentItem.getPackageNumber());
        mPackageInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        if (mFulfillmentItem.getOrderPackage() != null) {
            final Package orderPackage = mFulfillmentItem.getOrderPackage();

            if (orderPackage.getShippingMethodName() != null) {
                mShipping.setText(orderPackage.getShippingMethodName());
            }

            if (orderPackage.getPackagingType() != null) {
                mPackagingType.setText(orderPackage.getPackagingType());
            }

            if (orderPackage.getTrackingNumber() != null) {
                mTrackingNumber.setText(orderPackage.getTrackingNumber());
                mTrackingNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String shippingCode = orderPackage.getShippingMethodCode();
                        String Url = "";
                        if (shippingCode.toLowerCase().startsWith(ShipperUtils.Shipper.DHL.toString().toLowerCase())) {
                            Url = ShipperUtils.Shipper.DHL.getTrackingUrl(orderPackage.getTrackingNumber());
                        } else if (shippingCode.toLowerCase().startsWith(ShipperUtils.Shipper.FEDEX.toString().toLowerCase())) {
                            Url = ShipperUtils.Shipper.FEDEX.getTrackingUrl(orderPackage.getTrackingNumber());
                        } else if (shippingCode.toLowerCase().startsWith(ShipperUtils.Shipper.UPS.toString().toLowerCase())) {
                            Url = ShipperUtils.Shipper.UPS.getTrackingUrl(orderPackage.getTrackingNumber());
                        } else if (shippingCode.toLowerCase().startsWith(ShipperUtils.Shipper.USPS.toString().toLowerCase())) {
                            Url = ShipperUtils.Shipper.USPS.getTrackingUrl(orderPackage.getTrackingNumber());
                        } else {
                            Toast.makeText(getActivity(), "Unsupported Shipper", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                        startActivity(browse);

                    }
                });
            }else{
                mTrackingNumber.setText("N/A");
            }

            if (orderPackage.getStatus() != null) {
                mStatus.setText(orderPackage.getStatus());
            }

            if (orderPackage.getAuditInfo().getCreateDate() != null) {
                android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                String date = dateFormat.format("MM/dd/yy  hh:mm a", new Date(orderPackage.getAuditInfo().getCreateDate().getMillis())).toString();
                mShipDate.setText(date);
            }
        }

        if (mFulfillmentItem.getmFulfillmentContact() != null) {
            Contact contact = mFulfillmentItem.getmFulfillmentContact();
            if (contact != null) {
                mAddress.setText(contact.getFirstName() + " " + contact.getLastNameOrSurname() + "\n" + constructAddress(contact.getAddress()));
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
