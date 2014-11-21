package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.commerceruntime.orders.ShopperNotes;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.Date;
import java.util.List;

public class OrderDetailNotesAdapter extends BaseAdapter {

    public enum NotesType{
        INTERNAL_NOTE,
        CUSTOMER_NOTE
    }
    private Order mOrder;
    private NotesType mNotesType;
    public OrderDetailNotesAdapter(Order order,boolean isInternalNotes) {
        mOrder = order;
        if (isInternalNotes) {
            mNotesType = NotesType.INTERNAL_NOTE;
        } else {
            mNotesType = NotesType.CUSTOMER_NOTE;
        }
    }

    @Override
    public int getCount() {
        if(mNotesType == NotesType.INTERNAL_NOTE){
            return mOrder.getNotes().size();
        }else{
            return 1;
        }
    }

    @Override
    public Object getItem(int i) {
        if(mNotesType == NotesType.INTERNAL_NOTE){
            return mOrder.getNotes().get(i);
        }else{
            return mOrder.getShopperNotes();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(mNotesType == NotesType.INTERNAL_NOTE){
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
            }
            if(getItem(position) instanceof  OrderNote){
                OrderNote note = (OrderNote) getItem(position);

                TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
                TextView comment = (TextView) convertView.findViewById(R.id.note_comment);
                TextView employee = (TextView)convertView.findViewById(R.id.note_employee);

                String dateString = note.getAuditInfo() != null && note.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy", new Date(note.getAuditInfo().getCreateDate().getMillis())).toString() : "";
                noteDate.setText(dateString);
                employee.setText(note.getAuditInfo().getCreateBy());

                comment.setText(note.getText());
            }
        }else{
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_notes_list_item, parent, false);
            }

            if(getItem(position) instanceof ShopperNotes){
                ShopperNotes note = (ShopperNotes) getItem(position);
                mOrder.getAuditInfo().getCreateDate();


                TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
                TextView comment = (TextView) convertView.findViewById(R.id.note_comment);

                String dateString = mOrder.getAuditInfo() != null && mOrder.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy", new Date(mOrder.getAuditInfo().getCreateDate().getMillis())).toString() : "";
                noteDate.setText(dateString);

                comment.setText(note.getComments());

            }

        }

        return convertView;
    }

}
