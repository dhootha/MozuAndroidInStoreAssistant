package com.mozu.mozuandroidinstoreassistant.app.customer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerAddressesAdapter extends RecyclerView.Adapter<CustomerAddressesAdapter.ViewHolder> {

    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
    private List<CustomerContact> data;
    private AddressEditListener addressEditListener;
    private boolean onBind = false;

    public CustomerAddressesAdapter(List<CustomerContact> data, AddressEditListener addressEditListener) {
        this.data = data;
        this.addressEditListener = addressEditListener;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CustomerContact customerContact = data.get(position);
        Address address = customerContact.getAddress();
        Phone phone = customerContact.getPhoneNumbers();
        holder.name.setText(customerContact.getFirstName() + " " + customerContact.getLastNameOrSurname());
        holder.address1.setText(address.getAddress1());
        if (address.getAddress2() != null && !address.getAddress2().isEmpty()) {
            holder.address2.setText(address.getAddress2());
            holder.address2.setVisibility(View.VISIBLE);
        }
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

        for (ContactType type : customerContact.getTypes()) {
            if (type.getIsPrimary() && type.getName().equalsIgnoreCase(BILLING)) {
                holder.isBilling.setChecked(true);
            }
            if (type.getIsPrimary() && type.getName().equalsIgnoreCase(SHIPPING)) {
                holder.isShipping.setChecked(true);
            }
        }
        onBind = true;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface AddressEditListener {
        void onEditAddressClicked(int position);
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
        @InjectView(R.id.delete)
        Button delete;
        @InjectView(R.id.edit)
        Button edit;
        @InjectView(R.id.default_billing)
        CheckBox isBilling;
        @InjectView(R.id.default_shipping)
        CheckBox isShipping;
        @InjectView(R.id.customer_default_billing_address)
        TextView defaultBilling;
        @InjectView(R.id.customer_default_shipping_address)
        TextView defaultShipping;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            isBilling.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onBind && isChecked) {
                        updatePrimaryAddressSelection(BILLING);
                        notifyDataSetChanged();
                    }
                }
            });

            isShipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onBind && isChecked) {
                        updatePrimaryAddressSelection(SHIPPING);
                        notifyDataSetChanged();
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressEditListener.onEditAddressClicked(getAdapterPosition());
                }
            });
        }

        private void updatePrimaryAddressSelection(String name) {
            int position = ViewHolder.this.getAdapterPosition();
            List<ContactType> types;
            for (int i = 0; i < data.size(); i++) {
                if (i != position) {
                    types = data.get(i).getTypes();
                    for (ContactType type : types) {
                        if (type.getIsPrimary() && type.getName().equals(name)) {
                            type.setIsPrimary(false);
                        }
                    }
                } else {
                    types = data.get(position).getTypes();
                    if (types == null) {
                        types = new ArrayList<>();
                    }
                    ContactType type = new ContactType();
                    type.setIsPrimary(true);
                    type.setName(name);
                }
            }
        }

    }
}
