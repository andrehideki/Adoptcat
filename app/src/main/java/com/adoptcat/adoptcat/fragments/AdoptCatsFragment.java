package com.adoptcat.adoptcat.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

public class AdoptCatsFragment extends Fragment implements View.OnClickListener {

    private ImageView catPhotoImageView;
    private TextView titleTextView, descriptionAnnouncementTextView, amountTextView;
    private CheckBox spayedcheckedTextView, vaccinatedcheckedTextView, dewomedCheckedTextView;
    private ImageButton callImageButton;

    private Announcement announcement;

    private StorageReference storageReference;

    final long THREE_MB = 1024 * 1024 * 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adopt_cats, null, false);

        titleTextView = view.findViewById(R.id.titleTextView);
        descriptionAnnouncementTextView = view.findViewById(R.id.descriptionAnnouncementTextView);
        amountTextView = view.findViewById(R.id.amountTextView);
        catPhotoImageView = view.findViewById(R.id.catPhotoImageView);
        callImageButton = view.findViewById(R.id.callImageButton);
        spayedcheckedTextView = view.findViewById(R.id.spayedcheckedTextView);
        vaccinatedcheckedTextView = view.findViewById(R.id.vaccinatedcheckedTextView);
        dewomedCheckedTextView = view.findViewById(R.id.dewomedCheckedTextView);

        callImageButton.setOnClickListener( this );

        Bundle args = getArguments();
        this.announcement = (Announcement) args.get("announcement");

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageReference = Connection.getStorageReference().child(announcement.getId());

        titleTextView.setText(announcement.getTitle());
        descriptionAnnouncementTextView.setText( announcement.getTitle() );
        amountTextView.setText( announcement.getAmount() + "");
        spayedcheckedTextView.setChecked( announcement.isSpayed() );
        vaccinatedcheckedTextView.setChecked( announcement.isVaccineted() );
        dewomedCheckedTextView.setChecked( announcement.isDewomed() );

        if( announcement.isHasPhoto() ) {
            storageReference.getBytes(THREE_MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] result = task.getResult();
                    if (result.length > 0) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(result, 0, result.length, options);
                        options.inSampleSize = calculateSize(options, catPhotoImageView.getWidth(), catPhotoImageView.getHeight());

                        options.inJustDecodeBounds = false;
                        catPhotoImageView.setImageBitmap(BitmapFactory.decodeByteArray(result, 0, result.length, options));
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse( "tel:" +announcement.getPhone() ));
        startActivity(intent);
    }

    private int calculateSize(BitmapFactory.Options options, int imageViewWidth, int imageViewHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int f = 1;
        if( height > imageViewHeight || width > imageViewHeight ) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while( halfHeight / f > imageViewHeight && halfWidth / f > imageViewWidth ) {
                f *= 2;
            }
        }
        return f;
    }

}
