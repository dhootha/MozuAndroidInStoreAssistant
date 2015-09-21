package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailNotesAdapter extends BaseAdapter {

    private List<OrderNote> mOrderNotes = new ArrayList<>();

    public OrderDetailNotesAdapter(List<OrderNote> notes) {
        if (notes != null)
            mOrderNotes = notes;
    }

    @Override
    public int getCount() {
        return mOrderNotes.size();
    }

    @Override
    public OrderNote getItem(int i) {
        return mOrderNotes.get(i);
    }

    public void setData(List<OrderNote> data) {
        if (data != null) {
            mOrderNotes = data;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
        }
        OrderNote note = getItem(position);
        TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
        TextView comment = (TextView) convertView.findViewById(R.id.note_comment);
        String dateString = note.getAuditInfo() != null && note.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy", new Date(note.getAuditInfo().getCreateDate().getMillis())).toString() : "";
        noteDate.setText(dateString);
        comment.setText(note.getText());
        return convertView;
    }
}
