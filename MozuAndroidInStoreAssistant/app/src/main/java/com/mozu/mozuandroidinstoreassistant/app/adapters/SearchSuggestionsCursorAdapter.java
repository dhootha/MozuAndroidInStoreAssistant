package com.mozu.mozuandroidinstoreassistant.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.R;
import com.mozu.mozuandroidinstoreassistant.app.models.RecentSearch;

import java.util.List;

public class SearchSuggestionsCursorAdapter extends CursorAdapter {

    private List<RecentSearch> mItems;

    public SearchSuggestionsCursorAdapter(Context context, Cursor cursor, List<RecentSearch> items) {
        super(context, cursor, false);
        mItems = items;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(mItems.get(cursor.getPosition()).getSearchTerm());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);
        return view;

    }

}
