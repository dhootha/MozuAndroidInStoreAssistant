package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.tenant.Site;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class SitesAdapter extends ArrayAdapter<Site> {

    public SitesAdapter(Context context) {
        super(context, R.layout.site_list_item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.site_list_item, parent, false);
        }

        Site site = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.site_name);
        nameTextView.setText(site.getName());

        return convertView;
    }

}
