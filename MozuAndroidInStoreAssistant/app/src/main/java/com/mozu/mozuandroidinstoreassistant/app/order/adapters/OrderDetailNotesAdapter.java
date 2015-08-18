package com.mozu.mozuandroidinstoreassistant.app.order.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mozu.api.contracts.commerceruntime.orders.Order;
import com.mozu.api.contracts.commerceruntime.orders.OrderNote;
import com.mozu.api.contracts.commerceruntime.orders.ShopperNotes;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.Date;

public class OrderDetailNotesAdapter extends BaseAdapter implements ListView.OnItemClickListener {

    private final Context mContext;
    private Order mOrder;
    private boolean mIsInternalNotes;

    public OrderDetailNotesAdapter(Context context, Order order, boolean isInternalNotes) {
        mContext = context;
        mIsInternalNotes = isInternalNotes;
        mOrder = order;
    }

    @Override
    public int getCount() {
        if (mOrder.getNotes() != null && mIsInternalNotes) {
            return mOrder.getNotes().size();
        } else {
            return 1;
        }
    }

    @Override
    public Object getItem(int i) {
        if (mOrder.getNotes() != null && mIsInternalNotes) {
            return mOrder.getNotes().get(i);
        } else {
            return mOrder.getShopperNotes();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mIsInternalNotes) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_item, parent, false);
            }
            if (getItem(position) instanceof OrderNote) {
                OrderNote note = (OrderNote) getItem(position);

                TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
                TextView comment = (TextView) convertView.findViewById(R.id.note_comment);
                TextView employee = (TextView) convertView.findViewById(R.id.note_employee);

                String dateString = note.getAuditInfo() != null && note.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy", new Date(note.getAuditInfo().getCreateDate().getMillis())).toString() : "";
                noteDate.setText(dateString);
                employee.setText(note.getAuditInfo().getCreateBy());

                comment.setText(note.getText());
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_notes_list_item, parent, false);
            }

            if (getItem(position) instanceof ShopperNotes) {
                ShopperNotes note = (ShopperNotes) getItem(position);
//                mOrder.getAuditInfo().getCreateDate();

                TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
                TextView comment = (TextView) convertView.findViewById(R.id.note_comment);

                String dateString = mOrder.getAuditInfo() != null && mOrder.getAuditInfo().getCreateDate() != null ? DateFormat.format("MM/dd/yy", new Date(mOrder.getAuditInfo().getCreateDate().getMillis())).toString() : "";
                noteDate.setText(dateString);

                comment.setText(note.getComments());

            }

        }

        return convertView;
    }

    public void setOrder(Order order) {
        this.mOrder = order;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String note;
        Object item = getItem(position);
        if (getItem(position) instanceof ShopperNotes) {
            note = ((ShopperNotes) item).getComments();
        } else {
            note = ((OrderNote) item).getText();
        }
        showEditNoteDialog(note, position);
    }

    private void showEditNoteDialog(final String note, final int position) {
        final EditText editText = new EditText(mContext);
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setText(note);
        final AlertDialog editNoteDialog = new AlertDialog.Builder(mContext)
                .setView(editText)
                .setNegativeButton("Delete", null)
                .setNeutralButton("Edit", null)
                .setPositiveButton("Done", null)
                .create();
        editNoteDialog.show();
        Button positive = editNoteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button neutral = editNoteDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        Button negative = editNoteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem(editText.getText().toString(), position);
                editNoteDialog.dismiss();
            }
        });
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setEnabled(true);
                editText.setFocusable(true);
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
                editNoteDialog.dismiss();
            }
        });
    }

    private void updateItem(String note, int position) {
        if (mOrder.getNotes() != null && mIsInternalNotes) {
            mOrder.getNotes().get(position).setText(note);
        } else {
            mOrder.getShopperNotes().setComments(note);
        }
        notifyDataSetChanged();
    }

    private void deleteItem(int position) {
        if (mOrder.getNotes() != null && mIsInternalNotes) {
            mOrder.getNotes().remove(position);
        } else {
            mOrder.setShopperNotes(null);
        }
        notifyDataSetChanged();
    }
}
