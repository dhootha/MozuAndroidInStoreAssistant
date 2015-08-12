package com.mozu.mozuandroidinstoreassistant.app.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris_pound on 8/12/15.
 */
public class CustomerAddressesAdapter extends RecyclerView.Adapter<CustomerAddressesAdapter.ViewHolder> {

    private List<CustomerContact> data;

    public CustomerAddressesAdapter(List<CustomerContact> data) {
        this.data = data;
    }

    public void setData(List<CustomerContact> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CustomerContact customerContact = data.get(position);
        Address address = customerContact.getAddress();
        Phone phone = customerContact.getPhoneNumbers();
        //todo string builder
        holder.name.setText(customerContact.getFirstName() + " " + customerContact.getLastNameOrSurname());
        holder.address1.setText(address.getAddress1());
        holder.address2.setText(address.getAddress2());
        holder.citystatezip.setText(address.getCityOrTown() + ",  " + address.getStateOrProvince() + " " + address.getPostalOrZipCode());
        holder.country.setText(address.getCountryCode());
        if (phone.getHome() != null && !phone.getHome().isEmpty()) {
            holder.phoneNumber.setText("Phone: " + phone.getHome());

        } else if (phone.getMobile() != null && !phone.getMobile().isEmpty()) {
            holder.phoneNumber.setText("Phone: " + phone.getMobile());

        } else if (phone.getWork() != null && !phone.getWork().isEmpty()) {
            holder.phoneNumber.setText("Phone: " + phone.getWork());

        }
        holder.email.setText(customerContact.getEmail());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.address_1)
        TextView address1;
        @InjectView(R.id.address_2)
        TextView address2;
        @InjectView(R.id.citystatezip)
        TextView citystatezip;
        @InjectView(R.id.country)
        TextView country;
        @InjectView(R.id.phone_number)
        TextView phoneNumber;
        @InjectView(R.id.email)
        TextView email;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
