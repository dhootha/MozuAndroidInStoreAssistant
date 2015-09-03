package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderAction;
import com.mozu.api.contracts.customer.CustomerAccount;
import com.mozu.mozuandroidinstoreassistant.app.BaseActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderCreationAddCustomerActivity;
import com.mozu.mozuandroidinstoreassistant.app.OrderDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.NewOrderFragmentAdapter;
import com.mozu.mozuandroidinstoreassistant.app.order.loaders.NewOrderManager;
import com.mozu.mozuandroidinstoreassistant.app.views.LoadingView;
import com.viewpagerindicator.TabPageIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;

public class NewOrderActivity extends BaseActivity {

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
    public LoadingView mOrderLoading;


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
        boolean reloadData = true;
        if (savedInstanceState != null) {
            reloadData = false;
            mOrderId = savedInstanceState.getString(OrderCreationAddCustomerActivity.ORDER_EXTRA_KEY);
        } else if (getIntent() != null) {
            mOrderId = getIntent().getStringExtra(OrderCreationAddCustomerActivity.ORDER_EXTRA_KEY);
        }
        loadOrderData(reloadData);
    }

    private void loadOrderData(boolean hardReset) {
        mOrderLoading.setLoading();
        AndroidObservable.bindActivity(this, NewOrderManager.getInstance().getOrderData(mTenantId, mSiteId, mOrderId, hardReset))
                .subscribe(new Subscriber<Order>() {
                    @Override
                    public void onCompleted() {
                        mOrderLoading.success();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mOrderLoading.setError("Couldn't Load Data order data");
                    }

                    @Override
                    public void onNext(Order order) {
                        mOrder = order;
                        setUpViews();
                        loadCustomerData(order);
                    }
                });
    }

    private void loadCustomerData(Order order) {
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
        outState.putString(OrderCreationAddCustomerActivity.ORDER_EXTRA_KEY, mOrder.getId());
        super.onSaveInstanceState(outState);
    }


    private void setUpViews() {
        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setTitle(" ");
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

        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        mOrderStatus.setText(mOrder.getStatus());
        mOrderDate.setText(dateFormat.format(cal.getTime()));

        if (mOrderFragmentAdapter == null) {
            mOrderFragmentAdapter = new NewOrderFragmentAdapter(getFragmentManager(), mOrder);
            mOrderViewPager.setAdapter(mOrderFragmentAdapter);
            mOrderTabs.setViewPager(mOrderViewPager);
            mOrderFragmentAdapter.notifyDataSetChanged();
        } else {
            mOrderFragmentAdapter.notifyDataSetChanged();
        }
        mSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NewOrderActivity.this)
                        .setMessage(getString(R.string.submit_confirm_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitOrCancelOrder(mOrder, true);
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

    public void submitOrCancelOrder(Order order, final boolean isSubmit) {
        OrderAction orderAction = new OrderAction();
        if (isSubmit) {
            orderAction.setActionName("SubmitOrder");
        } else {
            orderAction.setActionName("AbandonOrder");
        }
        NewOrderManager.getInstance().getOrderActionObservable(mTenantId, mSiteId, order, orderAction).subscribe(new Subscriber<Order>() {
            @Override
            public void onCompleted() {
                finish();

            }

            @Override
            public void onError(Throwable e) {

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
