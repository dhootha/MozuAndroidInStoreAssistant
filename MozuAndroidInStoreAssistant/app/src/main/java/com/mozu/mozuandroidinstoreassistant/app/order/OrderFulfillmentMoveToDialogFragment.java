package com.mozu.mozuandroidinstoreassistant.app.order;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mozu.api.contracts.commerceruntime.fulfillment.Package;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.order.FulfillmentMoveToDataItem;
import com.mozu.mozuandroidinstoreassistant.app.order.adapters.OrderFulfillmentMoveToItemAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OrderFulfillmentMoveToDialogFragment extends DialogFragment implements OrderFulfillmentMoveToItemAdapter.MoveToListListener {

    @InjectView(R.id.items)
    RecyclerView mRecyclerViewProducts;
    @InjectView(R.id.cancel)
    Button mCancel;
    @InjectView(R.id.move_to)
    Spinner mMoveToOptions;
    private FulfillmentMoveToDataItem mData;
    private List<Package> mPackages;
    private List<String> options;
    private List<Integer> selected = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fulfillment_move_to, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewProducts.setLayoutManager(layoutManager);
        OrderFulfillmentMoveToItemAdapter adapter = new OrderFulfillmentMoveToItemAdapter(mData.getItems(), this);
        mRecyclerViewProducts.setAdapter(adapter);
        mRecyclerViewProducts.setHasFixedSize(true);
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, options);
        dropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMoveToOptions.setAdapter(dropDownAdapter);
    }

    public void setData(FulfillmentMoveToDataItem data, List<Package> packages) {
        this.mData = data;
        this.mPackages = packages;
        options = new ArrayList<>(packages.size());
        for (Package mozuPackage : packages) {
            options.add("Package " + mozuPackage.getId());
        }
        options.add("Create New Package");
        options.add(0, "Move To:");
    }

    @OnClick(R.id.cancel)
    public void dismissDialog() {
        this.dismiss();
    }

    @Override
    public void onItemSelected(Integer position, boolean isSelected) {
        if (isSelected) {
            selected.add(position);
        } else {
            selected.remove(position);
        }
    }
}
