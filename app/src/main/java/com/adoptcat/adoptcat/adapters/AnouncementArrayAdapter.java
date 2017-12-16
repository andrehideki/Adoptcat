package com.adoptcat.adoptcat.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.model.Announcement;

import org.w3c.dom.Text;

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
        TextView dataTextView, amountTextView, descriptionTextView, titleTextView;
        CheckedTextView vaccinetedCheckedTextView, dewomedCheckedTextView, spayedCheckedTextView;

        titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        dataTextView = (TextView) convertView.findViewById(R.id.dataTextView);
        amountTextView = (TextView) convertView.findViewById(R.id.amountTextView);
        descriptionTextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
        vaccinetedCheckedTextView = (CheckedTextView) convertView.findViewById(R.id.vaccinetedCheckedTextView);
        dewomedCheckedTextView = (CheckedTextView) convertView.findViewById(R.id.dewomedCheckedTextView);
        spayedCheckedTextView = (CheckedTextView) convertView.findViewById(R.id.spayedCheckedTextView);

        titleTextView.setText( announcement.getTitle() );
        dataTextView.setText( announcement.getDate() );
        amountTextView.setText( context.getString(R.string.registercat_amoutcats) +  announcement.getAmount() );
        descriptionTextView.setText( announcement.getDescription() );
        vaccinetedCheckedTextView.setChecked( announcement.isVaccineted() );
        dewomedCheckedTextView.setChecked( announcement.isDewomed() );
        spayedCheckedTextView.setChecked( announcement.isSpayed() );

        return convertView;
    }


}
