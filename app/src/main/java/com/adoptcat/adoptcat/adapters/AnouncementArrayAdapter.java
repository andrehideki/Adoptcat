package com.adoptcat.adoptcat.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.model.Announcement;

import java.util.ArrayList;

public class AnouncementArrayAdapter extends ArrayAdapter<Announcement>  {

    private Context context;
    private ArrayList<Announcement> announcements;

    public AnouncementArrayAdapter(@NonNull Context context, ArrayList<Announcement> announcements) {
        super(context,0, announcements);
        this.context = context;
        this.announcements = announcements;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Announcement announcement = this.announcements.get(position);
        convertView = LayoutInflater.from( this.context ).inflate( R.layout.list_item_announcements, null);
        TextView dataTextView, amountTextView, descriptionTextView, titleTextView,
                vaccinetedTextView, dewomedTextView, spayedTextView;

        titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        dataTextView = (TextView) convertView.findViewById(R.id.dataTextView);
        amountTextView = (TextView) convertView.findViewById(R.id.amountTextView);
        descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
        vaccinetedTextView = (TextView) convertView.findViewById(R.id.vaccinetedTextView);
        dewomedTextView = (TextView) convertView.findViewById(R.id.dewomedTextView);
        spayedTextView = (TextView) convertView.findViewById(R.id.spayedTextView);

        titleTextView.setText( announcement.getTitle() );
        dataTextView.setText( announcement.getDate() );
        amountTextView.setText( context.getString(R.string.registercat_amoutcats) +  announcement.getAmount() );
        descriptionTextView.setText( announcement.getDescription() );
        if( !announcement.isVaccineted() ) vaccinetedTextView.setVisibility(TextView.GONE);
        if( !announcement.isDewomed() ) dewomedTextView.setVisibility(TextView.GONE);
        if( !announcement.isSpayed() ) spayedTextView.setVisibility(TextView.GONE);

        return convertView;
    }




}
