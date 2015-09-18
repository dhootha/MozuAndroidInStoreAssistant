package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozu.api.ApiException;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderAction;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.MozuApplication;
import com.mozu.mozuandroidinstoreassistant.app.BaseActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.dialog.ErrorMessageAlertDialog;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.NewOrderFragmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;
import com.viewpagerindicator.TabPageIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

public class NewOrderActivity extends BaseActivity {

    public static final String ORDER_EXTRA_KEY = "ORDER";

    @InjectView(R.id.order_status)
    public TextView mOrderStatus;
    @InjectView(R.id.order_date)
    public TextView mOrderDate;
    @InjectView(R.id.order_name)
    public TextView mOrderName;
    @InjectView(R.id.order_email)
    public TextView mOrderEmail;
    @InjectView(R.id.order_tabs)
    public TabPageIndicator mOrderTabs;
    @InjectView(R.id.order_loading)
    public LinearLayout mOrderLoading;

    @InjectView(R.id.submit_order)
    public Button mSubmitOrder;

    @InjectView(R.id.cancel_order)
    public Button mCancelOrder;

    @InjectView(R.id.order_viewpager)
    public ViewPager mOrderViewPager;

    private String mOrderId;
    private CustomerAccount mCustomerAccount;
    private NewOrderFragmentAdapter mOrderFragmentAdapter;
    private Integer mTenantId;
    private Integer mSiteId;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserAuthenticationStateMachine userStateMachine = UserAuthenticationStateMachineProducer.getInstance(this);
        mTenantId = userStateMachine.getTenantId();
        mSiteId = userStateMachine.getSiteId();
        setContentView(R.layout.neworder_activity);
        ButterKnife.inject(this);
        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle(" ");
        }

        boolean reloadData = true;
        if (savedInstanceState != null) {
            reloadData = false;
            mOrderId = savedInstanceState.getString(ORDER_EXTRA_KEY);
        } else if (getIntent() != null) {
            mOrderId = getIntent().getStringExtra(ORDER_EXTRA_KEY);
        }
        loadOrderData(reloadData);
        if (((MozuApplication) getApplication()).getLocations().size() < 0) {
            loadLocationInformation(mTenantId, mSiteId);
        }
    }

    private void loadLocationInformation(Integer mTenantId, Integer mSiteId) {
        NewOrderManager.getInstance().getLocationsData(mTenantId, mSiteId, true).subscribe(new Subscriber<ArrayMap<String, String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("Locations Fetch", "Couldn't fetch Locations Information");
            }

            @Override
            public void onNext(ArrayMap<String, String> locationMap) {
                ((MozuApplication) getApplication()).setLocations(locationMap);
            }
        });
    }

    private void loadOrderData(boolean hardReset) {
        mOrderLoading.setVisibility(View.VISIBLE);
        AndroidObservable.bindActivity(this, NewOrderManager.getInstance().getOrderData(mTenantId, mSiteId, mOrderId, hardReset))
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        mOrderLoading.setVisibility(View.GONE);
                        loadCustomerData(mOrder);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mOrderLoading.setVisibility(View.GONE);
                        ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(NewOrderActivity.this, getString(R.string.order_load_failure));
                    }

                    @Override
                    public void onNext(Order order) {
                        mOrder = order;
                        setUpViews();

                    }
                });
    }

    private void loadCustomerData(Order order) {
        if (order.getCustomerAccountId() != null) {
            AndroidObservable.bindActivity(this, NewOrderManager.getInstance().getCustomerData(mTenantId, mSiteId, order.getCustomerAccountId())).subscribe(new Subscriber<CustomerAccount>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mOrderName.setText(getString(R.string.not_available));
                    mOrderEmail.setText(getString(R.string.not_available));
                }

                @Override
                public void onNext(CustomerAccount customerAccount) {
                    mCustomerAccount = customerAccount;
                    mOrderName.setText(mCustomerAccount.getLastName() + " " + mCustomerAccount.getFirstName());
                    mOrderEmail.setText(mCustomerAccount.getEmailAddress());
                }
            });
        } else {
            mOrderName.setText(getString(R.string.not_available));
            mOrderEmail.setText(getString(R.string.not_available));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            invalidateSubjects();
        }
    }

    private void invalidateSubjects() {
        NewOrderManager.getInstance().invalidateProductSearch();
        NewOrderManager.getInstance().invalidateOrderData();
        NewOrderManager.getInstance().invalidateCustomerInfo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ORDER_EXTRA_KEY, mOrder.getId());
        super.onSaveInstanceState(outState);
    }


    private void setUpViews() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                TextView tv = new TextView(NewOrderActivity.this);
                tv.setText("Order #" + mOrder.getOrderNumber());
                tv.setPadding(getResources().getDimensionPixelSize(R.dimen.order_actionbar_margin_left), 0, 0, 0);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(getResources().getColor(R.color.dark_gray_text));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                getActionBar().setCustomView(tv);
            }
        });

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        mOrderStatus.setText(mOrder.getStatus());
        mOrderDate.setText(dateFormat.format(cal.getTime()));

        if (mOrderFragmentAdapter == null) {
            mOrderFragmentAdapter = new NewOrderFragmentAdapter(getFragmentManager(), mOrder);
            mOrderViewPager.setAdapter(mOrderFragmentAdapter);
            mOrderTabs.setViewPager(mOrderViewPager);
            mOrderViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                @Override
                public void onPageSelected(int position) {
                    if(position == 0 ){

                    }
                }
            });
            mOrderFragmentAdapter.notifyDataSetChanged();
        } else {
            mOrderFragmentAdapter.notifyDataSetChanged();
        }
        mSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOrCancelOrder(mOrder, true);
            }
        });
        mCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NewOrderActivity.this)
                        .setMessage(getString(R.string.cancel_confirm_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitOrCancelOrder(mOrder, false);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void submitOrCancelOrder(Order order, final boolean isSubmit) {
        OrderAction orderAction = new OrderAction();
        if (isSubmit) {
            orderAction.setActionName(OrderStrings.SUBMIT_ACTION);
        } else {
            orderAction.setActionName(OrderStrings.ABANDON_ACTION);
        }
        mOrderLoading.setVisibility(View.VISIBLE);
        NewOrderManager.getInstance().getOrderActionObservable(mTenantId, mSiteId, order, orderAction).subscribe(new Subscriber<Order>() {
            @Override
            public void onCompleted() {
                mOrderLoading.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                String message = "";
                mOrderLoading.setVisibility(View.GONE);
                if (e instanceof ApiException) {
                    message = ((ApiException) e).getApiError().getMessage();
                }
                ErrorMessageAlertDialog.getStandardErrorMessageAlertDialog(NewOrderActivity.this, "Couldn't Submit Order:\n" + message).show();

            }

            @Override
            public void onNext(Order order) {
                if (isSubmit) {
                    Intent intent = new Intent(NewOrderActivity.this, OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.ORDER_NUMBER_EXTRA_KEY, order.getId());
                    intent.putExtra(OrderDetailActivity.CURRENT_TENANT_ID, mTenantId);
                    intent.putExtra(OrderDetailActivity.CURRENT_SITE_ID, mSiteId);
                    startActivity(intent);
                }
            }
        });


    }

    public void updateOrder(Order order) {

    }

}
