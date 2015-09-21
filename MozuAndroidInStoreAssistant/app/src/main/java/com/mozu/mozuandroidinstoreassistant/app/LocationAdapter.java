package com.mozu.mozuandroidinstoreassistant.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.location.Location;

public class LocationAdapter extends ArrayAdapter<Location> {

    public LocationAdapter(Context context) {
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

        Location location = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.site_name);
        nameTextView.setText(location.getName());

        return convertView;
    }

}
