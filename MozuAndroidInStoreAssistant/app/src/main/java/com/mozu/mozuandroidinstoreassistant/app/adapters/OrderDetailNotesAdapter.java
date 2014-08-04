package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.Date;
import java.util.List;

public class OrderDetailNotesAdapter extends ArrayAdapter<OrderNote> {

    public OrderDetailNotesAdapter(Context context, List<OrderNote> returns) {
        super(context, R.layout.notes_list_item);

        addAll(returns);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notes_list_item, parent, false);
        }

        OrderNote note = getItem(position);

        TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
        TextView comment = (TextView) convertView.findViewById(R.id.note_comment);

        android.text.format.DateFormat dateFormat= new android.text.format.DateFormat();
        String dateString = note.getAuditInfo() != null && note.getAuditInfo().getCreateDate() != null ? dateFormat.format("MM/dd/yy", new Date(note.getAuditInfo().getCreateDate().getMillis())).toString() : "";
        noteDate.setText(dateString);

        comment.setText(note.getText());

        return convertView;
    }

}
