package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mozu.api.contracts.tenant.Site;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.SitesAdapter;
import com.mozu.mozuandroidinstoreassistant.app.serialization.SiteListJsonConverter;

import java.util.List;

public class SiteFragment extends DialogFragment implements ListView.OnItemClickListener {

    public static final String SITES_AS_JSON = "SITES_AS_JSON";
    private SiteSelectionFragmentListener mListener;

    private ListView mListView;
    private List<Site> mSites;
    private SitesAdapter mAdapter;

    public SiteFragment() {
        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSites = new SiteListJsonConverter().getSitesFromJson(savedInstanceState.getString(SITES_AS_JSON, ""));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);

        mAdapter = new SitesAdapter(getActivity());
        mAdapter.addAll(mSites);

        mListView = (ListView) view.findViewById(R.id.site_list);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        getDialog().setTitle(getString(R.string.site_selection_dialog_title));

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
        outState.putString(SITES_AS_JSON, new SiteListJsonConverter().getSitesJsonFromListOfSites(mSites));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {

            mListener.siteWasChosen(mAdapter.getItem(position));
        }

    }

    public void setSites(List<Site> sites) {
       mSites = sites;
    }

}
