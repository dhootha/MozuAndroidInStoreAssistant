package com.mozu.mozuandroidinstoreassistant.app.customer;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.CustomerContact;
import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.data.customer.CustomerContactDataItem;

import java.util.List;

public class CustomerAddressAdapter extends BaseAdapter {


    private List<CustomerContactDataItem> mData;

    public CustomerAddressAdapter(List<CustomerContactDataItem> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CustomerContactDataItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerContactDataItem customerContactDataItem = getItem(position);
        CustomerContact customerContact = customerContactDataItem.getCustomerContact();
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            view = inflater.inflate(R.layout.customer_address_grid_item, null);
        } else {
            view = convertView;
        }
        TextView name = (TextView) view.findViewById(R.id.customer_address_name);
        TextView company = (TextView) view.findViewById(R.id.customer_address_company_name);
        TextView address1 = (TextView) view.findViewById(R.id.customer_address_line1);
        TextView email = (TextView) view.findViewById(R.id.customer_email);
        TextView home_num = (TextView) view.findViewById(R.id.customer_home_num);
        View home_num_row = view.findViewById(R.id.customer_home_num_row);

        TextView mobile_num = (TextView) view.findViewById(R.id.customer_mobile_num);
        View mobile_num_row = view.findViewById(R.id.customer_mobile_num_row);

        TextView work_num = (TextView) view.findViewById(R.id.customer_work_num);
        View work_num_row = view.findViewById(R.id.customer_work_num_row);


        TextView isPrimary = (TextView) view.findViewById(R.id.customer_primary_address);
        if (customerContactDataItem.getTypeMessage() != null) {
            isPrimary.setVisibility(View.VISIBLE);
            isPrimary.setText(customerContactDataItem.getTypeMessage());
        } else {
            isPrimary.setVisibility(View.GONE);
        }

        name.setText(customerContact.getFirstName() + " " + customerContact.getLastNameOrSurname());
        address1.setText(constructAddress(customerContact.getAddress()));
        company.setText(customerContact.getCompanyOrOrganization());
        email.setText(customerContact.getEmail());


        String phone;
        if ((phone = customerContact.getPhoneNumbers().getHome()) != null && !phone.isEmpty()) {
            home_num.setText(phone);
            home_num_row.setVisibility(View.VISIBLE);
        }
        if ((phone = customerContact.getPhoneNumbers().getMobile()) != null && !phone.isEmpty()) {
            mobile_num.setText(phone);
            mobile_num_row.setVisibility(View.VISIBLE);
        }
        if ((phone = customerContact.getPhoneNumbers().getWork()) != null && !phone.isEmpty()) {
            work_num.setText(phone);
            work_num_row.setVisibility(View.VISIBLE);
        }
        return view;
    }


    private String constructAddress(Address address) {
        if (address == null) {
            return "";
        }

        String addressString = "";

        if (!TextUtils.isEmpty(address.getAddress1())) {
            addressString += address.getAddress1();
        }

        if (!TextUtils.isEmpty(address.getAddress2())) {
            addressString += "\n" + address.getAddress2();
        }

        if (!TextUtils.isEmpty(address.getAddress3())) {
            addressString += "\n" + address.getAddress3();
        }

        if (!TextUtils.isEmpty(address.getAddress4())) {
            addressString += "\n" + address.getAddress4();
        }

        if (!TextUtils.isEmpty(address.getCityOrTown())) {
            addressString += "\n" + address.getCityOrTown();
        }

        if (!TextUtils.isEmpty(address.getStateOrProvince())) {
            addressString += ", " + address.getStateOrProvince();
        }

        if (!TextUtils.isEmpty(address.getPostalOrZipCode())) {
            addressString += " " + address.getPostalOrZipCode();
        }

        return addressString;
    }


    public void setData(List<CustomerContactDataItem> data) {
        mData = data;
    }


}
