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

    public CustomerAddressAdapter(List<CustomerContactDataItem> data){
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
            view = inflater.inflate(R.layout.customer_address_grid_item,null);
        } else {
            view = convertView;
        }
        TextView name = (TextView)view.findViewById(R.id.customer_address_name);
        TextView company = (TextView)view.findViewById(R.id.customer_address_company_name);
        TextView address1 = (TextView)view.findViewById(R.id.customer_address_line1);
        TextView email = (TextView)view.findViewById(R.id.customer_email);
        TextView contact_num = (TextView)view.findViewById(R.id.customer_contact_num);


        TextView isPrimary = (TextView)view.findViewById(R.id.customer_primary_address);
        if (customerContactDataItem.isPrimary()) {
            isPrimary.setVisibility(View.VISIBLE);
        } else {
            isPrimary.setVisibility(View.INVISIBLE);
        }


        name.setText(customerContact.getFirstName()+" "+customerContact.getLastNameOrSurname());
        address1.setText(constructAddress(customerContact.getAddress()));
        company.setText(customerContact.getCompanyOrOrganization());
        email.setText(customerContact.getEmail());


        String phone;
        if ((phone = customerContact.getPhoneNumbers().getWork()) != null) {
            contact_num.setText(phone);
        } else if ((phone = customerContact.getPhoneNumbers().getMobile()) != null) {
            contact_num.setText(phone);
        } else if ((phone = customerContact.getPhoneNumbers().getHome()) != null) {
            contact_num.setText(phone);
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
