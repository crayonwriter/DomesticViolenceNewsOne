package com.example.android.domesticviolencenewsone;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.domesticviolencenewsone.R;

import org.w3c.dom.Text;

import java.util.List;

public class DVArticlesAdapter extends ArrayAdapter<DVArticles> {
    //Constructor
    public DVArticlesAdapter(Context context, List<DVArticles> dvArticles) {
        super(context, 0, dvArticles);
    }

    //Get the item for the listview by overriding the getView method

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Find the article at the given position in the list of articles
        DVArticles currentDVArticles = getItem(position);

        // Find the TextView with view ID section
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        // Display the section of the current article in that TextView
        sectionView.setText(currentDVArticles.getSection());

        // Find the TextView with view ID url
        TextView urlView = (TextView) listItemView.findViewById(R.id.url);
        urlView.setText(currentDVArticles.getUrl());

        TextView headlineView = listItemView.findViewById(R.id.headline);
        headlineView.setText(currentDVArticles.getHeadline());

        TextView bylineView = listItemView.findViewById(R.id.byline);
        bylineView.setText(currentDVArticles.getByline());

        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(currentDVArticles.getDate());

        TextView bodyView = listItemView.findViewById(R.id.body);
        bodyView.setText(currentDVArticles.getBody());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
