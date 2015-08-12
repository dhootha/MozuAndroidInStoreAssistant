package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.AddressValidationResponse;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.CustomerCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAddressValidation;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chris_pound on 8/7/15.
 */
public class CustomerCreationFragment extends Fragment implements CustomerAddressVerifier {

    private static final int CUSTOMER_ADDRESS_VALIDATION_LOADER = 510;
    @InjectView(R.id.first_name)
    EditText mFirstName;
    @InjectView(R.id.last_name)
    EditText mLastName;
    @InjectView(R.id.phone_number)
    EditText mPhoneNumber;
    @InjectView(R.id.address_type)
    Spinner mAddressType;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.address_1)
    EditText mAddress1;
    @InjectView(R.id.address_2)
    EditText mAddress2;
    @InjectView(R.id.city)
    EditText mCity;
    @InjectView(R.id.state)
    Spinner mState;
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
    @InjectView(R.id.next)
    Button mSave;
    private int mTenantId;
    private int mSiteId;
    private String mAddressTypeSelected;
    private String mStateSelected;
    private Address mAddress;
    private Observable<AddressValidationResponse> addressValidationResponseObservable;
    private CustomerCreationActivity customerCreationListener;

    public static CustomerCreationFragment getInstance(int tenantId, int siteId) {
        Bundle bundle = new Bundle();
        CustomerCreationFragment fragment = new CustomerCreationFragment();
        bundle.putInt(OrderCreationActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(OrderCreationActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        ButterKnife.inject(this, view);
        mTenantId = getArguments().getInt(OrderCreationActivity.CURRENT_TENANT_ID);
        mSiteId = getArguments().getInt(OrderCreationActivity.CURRENT_SITE_ID);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.address_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAddressType.setAdapter(adapter);
        mAddressType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAddressTypeSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStateSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(stateAdapter);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    CustomerAccount customer = createCustomerFromForm();
                    customerCreationListener.onNextClicked(customer);
                }
            }
        });
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddress = createAddressFromForm();
                verifyAddressIsValid(mAddress);
            }
        });
    }

    private CustomerAccount createCustomerFromForm() {
        CustomerAccount customerAccount = new CustomerAccount();
        CustomerContact customerContact = new CustomerContact();
        if (mAddress == null) {
            mAddress = createAddressFromForm();
        }
        customerContact.setAddress(mAddress);
        List<CustomerContact> contacts = new ArrayList<CustomerContact>();
        customerAccount.setFirstName(mFirstName.getText().toString());
        customerAccount.setLastName(mLastName.getText().toString());
        customerAccount.setEmailAddress(mEmail.getText().toString());
        customerAccount.setContacts(contacts);
        customerAccount.setUserName(mEmail.getText().toString());
        customerContact.setFirstName(customerAccount.getFirstName());
        customerContact.setLastNameOrSurname(customerAccount.getLastName());
        customerContact.setEmail(customerAccount.getEmailAddress());
        Phone phone = new Phone();
        phone.setHome(mPhoneNumber.getText().toString());
        customerContact.setPhoneNumbers(phone);
        contacts.add(customerContact);
        return customerAccount;
    }

    private Address createAddressFromForm() {
        Address address = new Address();
        address.setAddress1(mAddress1.getText().toString());
        address.setAddress2(mAddress2.getText().toString());
        address.setStateOrProvince(mStateSelected);
        address.setCityOrTown(mCity.getText().toString());
        address.setPostalOrZipCode(mZip.getText().toString());
        address.setCountryCode(mCountry.getText().toString());
        address.setAddressType(mAddressTypeSelected);
        return address;

    }

    private boolean validateForm() {
        boolean isValid = true;
        if (mFirstName.getText().toString().isEmpty()) {
            mFirstName.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mLastName.getText().toString().isEmpty()) {
            mLastName.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mPhoneNumber.getText().toString().isEmpty()) {
            mPhoneNumber.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mEmail.getText().toString().isEmpty()) {
            mEmail.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mAddress1.getText().toString().isEmpty()) {
            mAddress1.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mCity.getText().toString().isEmpty()) {
            mCity.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mZip.getText().toString().isEmpty()) {
            mZip.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        if (mCountry.getText().toString().isEmpty()) {
            mCountry.setError(getActivity().getResources().getString(R.string.required));
            isValid = false;
        }
        return isValid;
    }

    public void verifyAddressIsValid(Address address) {
        if (validateForm()) {
            addressValidationResponseObservable = new CustomerAddressValidation(mTenantId, mSiteId).getAddressValidationObservable(address);
            addressValidationResponseObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getAddressValidationSubscriber());
        }

    }

    private Subscriber<AddressValidationResponse> getAddressValidationSubscriber() {
        return new Subscriber<AddressValidationResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                AlertDialog error = ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), e.toString());
                error.show();
            }

            @Override
            public void onNext(AddressValidationResponse response) {
                if (response != null && response.getAddressCandidates() != null && response.getAddressCandidates().size() > 0) {
                    Address address = response.getAddressCandidates().get(0);
                    createSuggestedAddressDialog(address).show();
                } else {
                    createValidatedAddressDialog().show();
                }
            }
        };
    }

    private AlertDialog createValidatedAddressDialog() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.verify_title)
                .setMessage(R.string.valid_address)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private AlertDialog createSuggestedAddressDialog(final Address address) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.verify_title)
                .setMessage(address.getAddress1() + "\n" +
                        address.getCityOrTown() + ", " +
                        address.getStateOrProvince() + " " +
                        address.getPostalOrZipCode() + "\n" +
                        address.getCountryCode())
                .setPositiveButton(R.string.verify_use_this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAddress1.setText(address.getAddress1());
                        mAddress2.setText(address.getAddress2());
                        mCity.setText(address.getCityOrTown());
                        //todo
//                        mState.setText(address.getStateOrProvince());
                        mZip.setText(address.getPostalOrZipCode());
                        mCountry.setText(address.getCountryCode());
                    }
                })
                .setNegativeButton(R.string.verify_keep, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                })
                .create();
    }

    public void setCustomerCreationListener(CustomerCreationActivity customerCreationListener) {
        this.customerCreationListener = customerCreationListener;
    }
}
