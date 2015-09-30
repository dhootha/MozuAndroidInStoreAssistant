package com.mozu.mozuandroidinstoreassistant.app.customer.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.core.Phone;
import com.mozu.api.contracts.customer.ContactType;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CustomerAddressesAdapter extends RecyclerView.Adapter<CustomerAddressesAdapter.ViewHolder> {

    private static String BILLING = "billing";
    private static String SHIPPING = "shipping";
    private final AddressDeleteListener addressDeleteListener;
    private List<CustomerContact> data;
    private AddressEditListener addressEditListener;

    public CustomerAddressesAdapter(List<CustomerContact> data, AddressEditListener addressEditListener, AddressDeleteListener addressDeleteListener) {
        this.data = data;
        this.addressEditListener = addressEditListener;
        this.addressDeleteListener = addressDeleteListener;
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
                holder.defaultBilling.setVisibility(View.VISIBLE);
            }
            if (type.getIsPrimary() && type.getName().equalsIgnoreCase(SHIPPING)) {
                holder.defaultShipping.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface AddressEditListener {
        void onEditAddressClicked(int position);

    }

    public interface AddressDeleteListener {
        void onDeleteAddressClicked(int position);
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
        @InjectView(R.id.customer_default_billing_address)
        TextView defaultBilling;
        @InjectView(R.id.customer_default_shipping_address)
        TextView defaultShipping;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setMessage(R.string.delete_address)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addressDeleteListener.onDeleteAddressClicked(getAdapterPosition());
                                }


                            })
                            .create()
                            .show();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressEditListener.onEditAddressClicked(getAdapterPosition());
                }
            });
        }
    }
}
