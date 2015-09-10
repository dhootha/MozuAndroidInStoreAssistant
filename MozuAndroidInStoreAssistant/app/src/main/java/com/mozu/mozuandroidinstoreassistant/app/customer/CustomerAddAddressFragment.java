package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.CustomerCreationActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.customer.adapters.CustomerAddressesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.AddCustomerContactObserverable;
import com.mozu.mozuandroidinstoreassistant.app.customer.loaders.CustomerAccountCreationObserver;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerAddAddressFragment extends Fragment {


    private static final String CUSTOMER_ACCOUNT = "customer";
    @InjectView(R.id.addresses)
    RecyclerView mAddressesRecyclerView;
    @InjectView(R.id.addAddress)
    Button mAddAddress;
    @InjectView(R.id.cancel)
    Button mCancel;
    @InjectView(R.id.save)
    Button mSave;
    @InjectView(R.id.create_customer_loading)
    LoadingView loadingView;
    private int mTenantId;
    private int mSiteId;
    private CustomerAccount mCustomerAccount;
    private CustomerAddressesAdapter mRecyclerViewAddressAdapter;
    private int countdown;
    private boolean mIsCusomterCreated;


    public static CustomerAddAddressFragment getInstance(Integer tenantId, Integer siteId, CustomerAccount account, boolean isCustomerCreated) {
        Bundle bundle = new Bundle();
        CustomerAddAddressFragment fragment = new CustomerAddAddressFragment();
        bundle.putInt(CustomerCreationActivity.CURRENT_TENANT_ID, tenantId);
        bundle.putInt(CustomerCreationActivity.CURRENT_SITE_ID, siteId);
        bundle.putBoolean(CustomerCreationActivity.CUSTOMER_CREATED, isCustomerCreated);
        bundle.putSerializable(CUSTOMER_ACCOUNT, account);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_add_address, container, false);
        ButterKnife.inject(this, view);
        mTenantId = getArguments().getInt(CustomerCreationActivity.CURRENT_TENANT_ID);
        mSiteId = getArguments().getInt(CustomerCreationActivity.CURRENT_SITE_ID);
        mCustomerAccount = (CustomerAccount) getArguments().getSerializable(CUSTOMER_ACCOUNT);
        mIsCusomterCreated = getArguments().getBoolean(CustomerCreationActivity.CUSTOMER_CREATED);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingView.success();
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerAddAddressFragment.this.getActivity().finish();
            }
        });
        mAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddAddressClicked();
            }
        });
        mAddressesRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewAddressAdapter = new CustomerAddressesAdapter(mCustomerAccount.getContacts(), (CustomerAddressesAdapter.AddressEditListener) getActivity());
        mAddressesRecyclerView.setLayoutManager(layoutManager);
        mAddressesRecyclerView.setAdapter(mRecyclerViewAddressAdapter);
    }


    public void onAddAddressClicked() {
        ((CustomerCreationListener) getActivity()).addNewAddress(mCustomerAccount);
    }

    private void onSaveClicked() {
        loadingView.setLoading();
        //todo skip this if our customer was already created.
        if (mIsCusomterCreated) {
            updateCustomerAddresses(mCustomerAccount.getId());
        } else {
            CustomerAccountCreationObserver.getCustomerAccountCreationObserverable(mTenantId, mSiteId, mCustomerAccount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getCreateCustomerAccountSubscriber());
        }

    }

    public Subscriber<CustomerAccount> getCreateCustomerAccountSubscriber() {
        return new Subscriber<CustomerAccount>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                AlertDialog error = ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), getString(R.string.standard_error));
                loadingView.success();
                error.show();
            }

            @Override
            public void onNext(CustomerAccount customerAccount) {
                updateCustomerAddresses(customerAccount.getId());
            }
        };
    }

    private void updateCustomerAddresses(Integer customerId) {
        //customer saved goto orders.
        Log.d("Customer created", "created customer");
        countdown = mCustomerAccount.getContacts().size();
        for (int i = countdown; i > 0; i--) {
            AndroidObservable.bindFragment(CustomerAddAddressFragment.this, AddCustomerContactObserverable
                    .getCustomerContactCreationObserverable(mTenantId, mSiteId, customerId, mCustomerAccount.getContacts().get(i - 1)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getAddCustomerContactSubscriber());
        }
    }

    public Subscriber<CustomerContact> getAddCustomerContactSubscriber() {
        return new Subscriber<CustomerContact>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                AlertDialog error = ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(getActivity(), getString(R.string.standard_error));
                loadingView.success();
                error.show();
            }

            @Override
            public void onNext(CustomerContact customerContact) {
                countdown--;
                if (countdown == 0) {
                    loadingView.success();
                    Intent intent = new Intent();
                    intent.putExtra(CustomerCreationActivity.CUSTOMER, mCustomerAccount);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            }
        };
    }
}