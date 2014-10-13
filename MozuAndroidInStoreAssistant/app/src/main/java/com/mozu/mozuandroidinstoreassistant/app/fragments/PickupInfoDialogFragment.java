package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.fulfillment.Pickup;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.OrderDetailPickupItemAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PickupInfoDialogFragment extends DialogFragment {

    private Pickup mPickup;

    @InjectView(R.id.pickup_items_list) ListView mList;

    private Integer mTenantId;
    private Integer mSiteId;
    private TextView mStatus;
    private TextView mLocation;
    private ImageView mCloseView;

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
        mCloseView = (ImageView)view.findViewById(R.id.pickup_close);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        ButterKnife.inject(this, view);

        if (mPickup != null) {
            setPickupViews();
        }

        return view;
    }

    private void setPickupViews() {

        setPickupItems();

    }

    private void setPickupItems() {
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

    }

    public void setPickup(Pickup pickup) {

        mPickup = pickup;
    }

    public void setTenantAndSiteId(Integer tenantId, Integer siteId) {
        mTenantId = tenantId;
        mSiteId = siteId;
    }
}
