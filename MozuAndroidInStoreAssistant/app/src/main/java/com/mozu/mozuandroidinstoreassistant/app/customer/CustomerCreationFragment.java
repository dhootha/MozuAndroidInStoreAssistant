package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.ContextThemeWrapper;
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
import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.CustomerUpdateActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAddressValidationObservable;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.utils.InputValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.schedulers.Schedulers;

public class CustomerCreationFragment extends Fragment implements CustomerAddressVerifier {

    private static final String CUSTOMER = "customer";
    private static final String IS_EDIT = "edit";
    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
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
    Button mNext;
    @InjectView(R.id.cancel)
    Button mCancel;
    private int mTenantId;
    private int mSiteId;
    private CustomerAccount mCustomerAccount;
    private String mAddressTypeSelected;
    private String mStateSelected;
    private Observable<AddressValidationResponse> addressValidationResponseObservable;
    private int mEditing;
    private List<String> states;
    private List<String> addressTypes;

    public static CustomerCreationFragment getInstance(int tenantId, int siteId, CustomerAccount customerAccount, int editing) {
        Bundle bundle = new Bundle();
        CustomerCreationFragment fragment = new CustomerCreationFragment();
        bundle.putInt(CustomerUpdateActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(CustomerUpdateActivity.CURRENT_SITE_ID, siteId);
        bundle.putInt(IS_EDIT, editing);
        bundle.putSerializable(CUSTOMER, customerAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CustomerCreationFragment getInstance(int tenantId, int siteId, CustomerAccount customerAccount) {
        Bundle bundle = new Bundle();
        CustomerCreationFragment fragment = new CustomerCreationFragment();
        bundle.putInt(CustomerUpdateActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(CustomerUpdateActivity.CURRENT_SITE_ID, siteId);
        bundle.putSerializable(CUSTOMER, customerAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CustomerCreationFragment getInstance(int tenantId, int siteId) {
        Bundle bundle = new Bundle();
        CustomerCreationFragment fragment = new CustomerCreationFragment();
        bundle.putInt(CustomerUpdateActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(CustomerUpdateActivity.CURRENT_SITE_ID, siteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mTenantId = getArguments().getInt(CustomerUpdateActivity.CURRENT_TENANT_ID, -1);
        mSiteId = getArguments().getInt(CustomerUpdateActivity.CURRENT_SITE_ID, -1);
        mEditing = getArguments().getInt(IS_EDIT, -1);
        states = Arrays.asList(getResources().getStringArray(R.array.states));
        addressTypes = Arrays.asList(getResources().getStringArray(R.array.address_type));
        Object possibleCustomer = getArguments().getSerializable(CUSTOMER);
        if (possibleCustomer != null && possibleCustomer instanceof CustomerAccount) {
            mCustomerAccount = (CustomerAccount) possibleCustomer;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_customer, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.address_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(stateAdapter);
        mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStateSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    updateDefaultAddress();
                    createOrUpdateCustomerAccount();
                    ((CustomerCreationListener) getActivity()).onNextClicked(mCustomerAccount);
                }
            }
        });
        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address address = createAddressFromForm();
                verifyAddressIsValid(address);
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomerAccount != null && mCustomerAccount.getContacts() != null && mCustomerAccount.getContacts().size() > 0) {
                    //go back to add addresses
                    ((CustomerCreationListener) getActivity()).onNextClicked(mCustomerAccount);
                } else {
                    getActivity().finish();
                }
            }
        });
        if (mEditing > -1) {
            CustomerContact customerEditing = mCustomerAccount.getContacts().get(mEditing);
            mDefaultBilling.setChecked(false);
            mDefaultShipping.setChecked(false);
            mFirstName.setText(customerEditing.getFirstName());
            mLastName.setText(customerEditing.getLastNameOrSurname());
            mEmail.setText(customerEditing.getEmail());
            mPhoneNumber.setText(customerEditing.getPhoneNumbers().getMobile());
            mAddress1.setText(customerEditing.getAddress().getAddress1());
            mAddress2.setText(customerEditing.getAddress().getAddress2());
            mCity.setText(customerEditing.getAddress().getCityOrTown());
            mZip.setText(customerEditing.getAddress().getPostalOrZipCode());
            setSelectedState(customerEditing.getAddress().getStateOrProvince());
            setSelectedAddressType(customerEditing.getAddress().getAddressType());
            mCountry.setText(customerEditing.getAddress().getCountryCode());
            if (customerEditing.getTypes() != null && customerEditing.getTypes().size() > 0) {
                for (ContactType type : customerEditing.getTypes()) {
                    if (type.getIsPrimary() && type.getName().equalsIgnoreCase(BILLING)) {
                        mDefaultBilling.setChecked(true);
                    } else if (type.getIsPrimary() && type.getName().equalsIgnoreCase(SHIPPING)) {
                        mDefaultShipping.setChecked(true);
                    }
                }
            }
        } else if (mCustomerAccount != null) {
            //adding new account
            mFirstName.setText(mCustomerAccount.getFirstName());
            mLastName.setText(mCustomerAccount.getLastName());
            mEmail.setText(mCustomerAccount.getEmailAddress());
            if (mCustomerAccount.getContacts() != null && mCustomerAccount.getContacts().size() > 0 && mCustomerAccount.getContacts().get(0) != null) {
                mPhoneNumber.setText(mCustomerAccount.getContacts().get(0).getPhoneNumbers().getMobile());
            }
            if (mCustomerAccount.getContacts() != null && mCustomerAccount.getContacts().size() > 0) {
                mDefaultBilling.setChecked(false);
                mDefaultShipping.setChecked(false);
            }
        }
    }

    private void updateDefaultAddress() {
        if (mCustomerAccount != null && mCustomerAccount.getContacts() != null &&
                mCustomerAccount.getContacts().size() > 0) {
            if (mDefaultBilling.isChecked()) {
                for (CustomerContact contact : mCustomerAccount.getContacts()) {
                    for (ContactType type : contact.getTypes()) {
                        if (type.getName().equalsIgnoreCase(BILLING)) {
                            type.setIsPrimary(false);
                        }
                    }
                }
            }
            if (mDefaultShipping.isChecked()) {
                for (CustomerContact contact : mCustomerAccount.getContacts()) {
                    for (ContactType type : contact.getTypes()) {
                        if (type.getName().equalsIgnoreCase(SHIPPING)) {
                            type.setIsPrimary(false);
                        }
                    }
                }
            }
        }
    }

    private void createOrUpdateCustomerAccount() {
        CustomerContact customerContact = createCustomerContactFromForm();
        if (mEditing > -1) {
            customerContact.setId(mCustomerAccount.getContacts().get(mEditing).getId());
            mCustomerAccount.getContacts().set(mEditing, customerContact);
        } else {
            mCustomerAccount = mCustomerAccount == null ? createCustomerAccountFromForm() : mCustomerAccount;
            List<CustomerContact> contacts = mCustomerAccount.getContacts() == null ? new ArrayList<CustomerContact>() : mCustomerAccount.getContacts();
            contacts.add(customerContact);
            mCustomerAccount.setContacts(contacts);
        }
    }

    private CustomerContact createCustomerContactFromForm() {
        CustomerContact customerContact = new CustomerContact();
        customerContact.setFirstName(mFirstName.getText().toString());
        customerContact.setLastNameOrSurname(mLastName.getText().toString());
        customerContact.setAddress(createAddressFromForm());
        customerContact.setEmail(mEmail.getText().toString());
        Phone phone = new Phone();
        phone.setMobile(mPhoneNumber.getText().toString());
        customerContact.setPhoneNumbers(phone);
        List<ContactType> contactTypes = new ArrayList<>();
        if (mDefaultShipping.isChecked()) {
            ContactType contactType = new ContactType();
            contactType.setName(SHIPPING);
            contactType.setIsPrimary(true);
            contactTypes.add(contactType);
        }
        if (mDefaultBilling.isChecked()) {
            ContactType contactType = new ContactType();
            contactType.setName(BILLING);
            contactType.setIsPrimary(true);
            contactTypes.add(contactType);
        }
        customerContact.setTypes(contactTypes);
        return customerContact;
    }

    private CustomerAccount createCustomerAccountFromForm() {
        mCustomerAccount = new CustomerAccount();
        mCustomerAccount.setFirstName(mFirstName.getText().toString());
        mCustomerAccount.setLastName(mLastName.getText().toString());
        mCustomerAccount.setEmailAddress(mEmail.getText().toString());
        mCustomerAccount.setUserName(mEmail.getText().toString());
        return mCustomerAccount;
    }

    private Address createAddressFromForm() {
        if (mStateSelected == null) {
            mStateSelected = states.get(0);
        }
        if (mAddressTypeSelected == null) {
            mAddressTypeSelected = addressTypes.get(0);
        }
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
        if (!InputValidation.isPhoneNumberValid(mPhoneNumber.getText().toString())) {
            mPhoneNumber.setError(getActivity().getResources().getString(R.string.invalid_phone));
            isValid = false;
        }
        if (!InputValidation.isEmailValid(mEmail.getText().toString())) {
            mEmail.setError(getResources().getString(R.string.error_invalid_email));
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
            addressValidationResponseObservable = new CustomerAddressValidationObservable(mTenantId, mSiteId).getAddressValidationObservable(address);
            AndroidObservable.bindFragment(CustomerCreationFragment.this, addressValidationResponseObservable
                    .subscribeOn(Schedulers.io()))
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
                String message = getResources().getString(R.string.standard_error);
                if (e.getMessage() != null && e.getMessage().contains("Address Not Found")) {
                    message = getResources().getString(R.string.address_not_found);
                }
                AlertDialog error = ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), message);
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

    private void setSelectedState(String state) {
        int position = states.indexOf(state);
        mStateSelected = state;
        mState.setSelection(position);
    }

    private void setSelectedAddressType(String type) {
        if (mAddressType != null) {
            int position = addressTypes.indexOf(type);
            mAddressTypeSelected = type;
            mAddressType.setSelection(position);
        }
    }

    private AlertDialog createValidatedAddressDialog() {
        return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.DialogMozu))
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
        return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.DialogMozu))
                .setTitle(R.string.verify_title)
                .setMessage(address.getAddress1() + "\n" +
                        address.getAddress2() + "\n" +
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
                        setSelectedState(address.getStateOrProvince());
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
}
