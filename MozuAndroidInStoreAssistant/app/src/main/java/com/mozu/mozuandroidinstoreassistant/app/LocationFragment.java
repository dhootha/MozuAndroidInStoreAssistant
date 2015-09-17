package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mozu.api.contracts.location.FulfillmentType;
import com.mozu.api.contracts.location.Location;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SiteSelectionFragmentListener;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends DialogFragment implements ListView.OnItemClickListener, DialogInterface.OnKeyListener {

    public static final String LOCATIONS_AS_JSON = "LOCATIONS_AS_JSON";
    private List<Location> mLocations;
    private SiteSelectionFragmentListener mListener;

    private ListView mListView;
    private LocationAdapter mAdapter;

    public void setLocations(List<Location> locations) {
        //filter
        List<Location> filteredLocations = new ArrayList<>();
        for (Location location : locations) {
            for (FulfillmentType fulfillmentType : location.getFulfillmentTypes()) {
                if (fulfillmentType.getCode().equalsIgnoreCase("SP") || fulfillmentType.getName().equalsIgnoreCase("in store pickup")) {
                    filteredLocations.add(location);
                }
            }
        }
        mLocations = filteredLocations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLocations = new LocationListJsonConverter().getLocationsFromJson(savedInstanceState.getString(LOCATIONS_AS_JSON, ""));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);

        mAdapter = new LocationAdapter(getActivity());
        mAdapter.addAll(mLocations);

        mListView = (ListView) view.findViewById(R.id.site_list);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        getDialog().setTitle(getString(R.string.location_selection_title));

        getDialog().setOnKeyListener(this);

        //this is monster hacky, but not sure how else to change the style of this one bar
        int divierId = getDialog().getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = getDialog().findViewById(divierId);
        divider.setBackgroundColor(getActivity().getResources().getColor(R.color.mozu_color));

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (SiteSelectionFragmentListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATIONS_AS_JSON, new LocationListJsonConverter().getJsonFromListOfLocations(mLocations));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {

            mListener.locationWasChosen(mAdapter.getItem(position));
        }

    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            //if tenant not chosen then exit out

            mListener.siteNotChosenButExited();
            return true;
        }

        return false;
    }
}
