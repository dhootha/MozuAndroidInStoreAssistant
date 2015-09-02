package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.api.contracts.commerceruntime.fulfillment.PickupItem;
import com.mozu.mozuandroidinstoreassistant.app.ProductDetailActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderDetailPickupItemAdapter;
import com.mozu.mozuandroidinstoreassistant.app.utils.ProductUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PickupInfoDialogFragment extends DialogFragment {

    @InjectView(R.id.pickup_items_list) ListView mList;
    private Pickup mPickup;
    private Integer mTenantId;
    private Integer mSiteId;
    private TextView mStatus;
    private TextView mLocation;
    private ImageView mCloseView;
    private TextView mPickupName;

    public PickupInfoDialogFragment() {
        // Required empty public constructor

        setStyle(STYLE_NO_TITLE, 0);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pickup_dialog_fragment, null);
        mStatus = (TextView) view.findViewById(R.id.pickup_status_value);
        mLocation = (TextView)view.findViewById(R.id.pickup_location_value);
        mPickupName = (TextView) view.findViewById(R.id.pickup_name);
        mCloseView = (ImageView)view.findViewById(R.id.pickup_close);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        ButterKnife.inject(this, view);
        mList.setDivider(null);

        if (mPickup != null) {
            setPickupViews();
        }

        return view;
    }

    private void setPickupViews() {

        setPickupItems();

    }

    private void setPickupItems() {
        if (mPickup.getCode() != null && !mPickup.getCode().isEmpty()) {
            mPickupName.setText(mPickup.getCode());
        }
        if (mPickup.getStatus() != null) {
            mStatus.setText(mPickup.getStatus());
        } else {
            mStatus.setText("N/A");
        }
        if (mPickup.getFulfillmentLocationCode() != null) {
            mLocation.setText(mPickup.getFulfillmentLocationCode());
        } else {
            mLocation.setText("N/A");
        }
        if (mPickup.getItems() == null|| mPickup.getItems().size() < 1) {
            return;
        }

        OrderDetailPickupItemAdapter adapter = new OrderDetailPickupItemAdapter(getActivity(), mPickup.getItems(), mTenantId, mSiteId);

        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PickupItem item = (PickupItem) adapterView.getItemAtPosition(position);
                String productCode = ProductUtils.getPackageorPickupProductCode(item.getProductCode());
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                UserAuthenticationStateMachine userAuthenticationStateMachine = UserAuthenticationStateMachineProducer.getInstance(getActivity());
                intent.putExtra(ProductDetailActivity.PRODUCT_CODE_EXTRA_KEY, productCode);
                intent.putExtra(ProductDetailActivity.CURRENT_TENANT_ID, userAuthenticationStateMachine.getTenantId());
                intent.putExtra(ProductDetailActivity.CURRENT_SITE_ID, userAuthenticationStateMachine.getSiteId());
                startActivity(intent);

            }
        });

    }

    public void setPickup(Pickup pickup) {

        mPickup = pickup;
    }

    public void setTenantAndSiteId(Integer tenantId, Integer siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;
    }
}
