package com.mozu.mozuandroidinstoreassistant.app.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.adapters.TenantAdapter;
import com.mozu.mozuandroidinstoreassistant.app.serialization.TenantListJsonConverter;

import org.joda.time.DateTime;

import java.util.List;

public class TenantFragment extends DialogFragment implements ListView.OnItemClickListener, Dialog.OnKeyListener {

    public static final String TENANTS_AS_JSON = "TENANTS_AS_JSON";
    private TenantSelectionFragmentListener mListener;

    private ListView mListView;
    private List<Scope> mTenants;
    private TenantAdapter mAdapter;

    public TenantFragment() {
        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTenants = new TenantListJsonConverter().getTenantsFromJson(savedInstanceState.getString(TENANTS_AS_JSON, ""));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenant_list, container, false);

        mAdapter = new TenantAdapter(getActivity());
        mAdapter.addAll(mTenants);

        mListView = (ListView) view.findViewById(R.id.tenant_list);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        getDialog().setTitle(getString(R.string.tenant_selection_dialog_title));

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

        mListener = (TenantSelectionFragmentListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TENANTS_AS_JSON, new TenantListJsonConverter().getTenantJsonFromListOfTenants(mTenants));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {

            mListener.tenantWasChosen(mAdapter.getItem(position));
        }
    }

    public void setTenants(List<Scope> tenants) {
       mTenants = tenants;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled()) {
            Toast.makeText(getActivity(), "no for real, the real back was pressed" + String.valueOf(new DateTime().getMillis()), Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
