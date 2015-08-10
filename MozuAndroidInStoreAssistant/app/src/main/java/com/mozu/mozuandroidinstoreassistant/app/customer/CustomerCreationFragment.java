package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.AddressValidationResponse;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAddressValidationLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris_pound on 8/7/15.
 */
public class CustomerCreationFragment extends Fragment implements LoaderManager.LoaderCallbacks<AddressValidationResponse>, CustomerAddressVerifier {

    private static final int CUSTOMER_ADDRESS_VALIDATION_LOADER = 510;
    @InjectView(R.id.first_name)
    EditText mFirstName;
    @InjectView(R.id.last_name)
    EditText mLastName;
    @InjectView(R.id.phone_number)
    EditText mPhoneNumbeer;
    @InjectView(R.id.phone_type)
    EditText mPhoneType;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.address_1)
    EditText mAddress1;
    @InjectView(R.id.address_2)
    EditText mAddress2;
    @InjectView(R.id.city)
    EditText mCity;
    @InjectView(R.id.state)
    EditText mState;
    @InjectView(R.id.country)
    EditText mCountry;
    @InjectView(R.id.zipcode)
    EditText mZip;
    @InjectView(R.id.default_billing)
    CheckBox mDefaultBilling;
    @InjectView(R.id.default_shipping)
    CheckBox mDefaultShipping;
    @InjectView(R.id.verify)
    Button mVerify;
    private int mTenantId;
    private int mSiteId;
    private CustomerAddressValidationLoader mCustomerAddressValidationLoader;

    public static CustomerCreationFragment getInstance(int tenantId, int siteId) {
        Bundle bundle = new Bundle();
        CustomerCreationFragment fragment = new CustomerCreationFragment();
        bundle.putInt(OrderCreationActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(OrderCreationActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        ButterKnife.inject(this, view);
        getLoaderManager().initLoader(CUSTOMER_ADDRESS_VALIDATION_LOADER, null, this);
        mTenantId = getArguments().getInt(OrderCreationActivity.CURRENT_TENANT_ID);
        mSiteId = getArguments().getInt(OrderCreationActivity.CURRENT_SITE_ID);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address addressToVerify = new Address();
                addressToVerify.setAddress1(mAddress1.getText().toString());
                addressToVerify.setAddress2(mAddress2.getText().toString());
                addressToVerify.setStateOrProvince(mState.getText().toString());
                addressToVerify.setCityOrTown(mCity.getText().toString());
                addressToVerify.setPostalOrZipCode(mZip.getText().toString());
                addressToVerify.setCountryCode(mCountry.getText().toString());
                verifyAddressIsValid(addressToVerify);
            }
        });

    }

    @Override
    public Loader<AddressValidationResponse> onCreateLoader(int id, Bundle args) {
        mCustomerAddressValidationLoader = new CustomerAddressValidationLoader(getActivity().getApplicationContext(), mTenantId, mSiteId);
        return mCustomerAddressValidationLoader;
    }

    @Override
    public void onLoadFinished(Loader<AddressValidationResponse> loader, AddressValidationResponse data) {
        if (data.getAddressCandidates().size() > 0 && data != null) {
            Address address = data.getAddressCandidates().get(0);
            final AlertDialog alert = new AlertDialog.Builder(getActivity().getApplicationContext())
                    .setTitle("Validated Address")
                    .setMessage(address.getAddress1() + "\n" + address.getCityOrTown() + ", " + address.getStateOrProvince() + " " + address.getPostalOrZipCode() + "\n" + address.getCountryCode())
                    .setPositiveButton("Use This", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNegativeButton("Keep Original", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null)
                                dialog.dismiss();

                        }
                    })
                    .create();
            alert.show();
        } else {
            final AlertDialog alert = new AlertDialog.Builder(getActivity().getApplicationContext())
                    .setTitle("Validated Address")
                    .setMessage("Address is Valid.")
                    .create();
            alert.show();

        }
    }

    @Override
    public void onLoaderReset(Loader<AddressValidationResponse> loader) {

    }

    @Override
    public void verifyAddressIsValid(Address address) {
        getCustomerAddressValidationLoader().setAddressToVerify(address);
        getCustomerAddressValidationLoader().forceLoad();
    }

    private CustomerAddressValidationLoader getCustomerAddressValidationLoader() {
        if (mCustomerAddressValidationLoader == null) {
            Loader loader = getLoaderManager().getLoader(CUSTOMER_ADDRESS_VALIDATION_LOADER);
            mCustomerAddressValidationLoader = (CustomerAddressValidationLoader) loader;
        }
        return mCustomerAddressValidationLoader;
    }
}
