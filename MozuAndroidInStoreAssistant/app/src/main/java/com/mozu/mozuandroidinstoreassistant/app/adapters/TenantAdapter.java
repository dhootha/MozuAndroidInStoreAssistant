package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.R;

public class TenantAdapter extends ArrayAdapter<Scope> {

    public TenantAdapter(Context context) {
        super(context, R.layout.tenant_list_item);
    }

    @Override
    public long getItemId(int position) {

         return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tenant_list_item, parent, false);
        }

        Scope tenant = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.tenant_name);
        nameTextView.setText(tenant.getName());

        TextView idTextView = (TextView) convertView.findViewById(R.id.tenant_id);
        idTextView.setText(String.valueOf(tenant.getId()));

        return convertView;
    }

}
