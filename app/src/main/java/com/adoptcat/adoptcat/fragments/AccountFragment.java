package com.adoptcat.adoptcat.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    private TextView userNameTextView, cityUserTextView, userPhoneTextView, userEmailTextView;
    private ImageView userPhotoImageView;

    private User user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Bitmap userPhoto;

    final long ONE_MEGABYTE = 1024 * 1024;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, null);

        userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        cityUserTextView = (TextView) view.findViewById(R.id.cityUserTextView);
        userPhoneTextView = (TextView) view.findViewById(R.id.userPhoneTextView);
        userEmailTextView = (TextView) view.findViewById(R.id.userEmailTextView);
        userPhotoImageView = (ImageView) view.findViewById(R.id.userPhotoImageView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = User.getInstance();

        databaseReference = Connection.getDatabaseUsersReference().child( user.getUUID() );
        storageReference = Connection.getStorageReference().child( user.getUUID() ).child("UserPhoto");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue( User.class );
                User.cloneState( u );
                updateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Picasso.with( getContext() ).load("gs://adoptcat-ee784.appspot.com/" + user.getUUID() + "/UserPhoto");
/*
        storageReference.getBytes( ONE_MEGABYTE ).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                userPhoto = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
                updateImageView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage( getString(R.string.account_failed_donwload_uphoto ) );
            }
        });
        */

    }

    private void updateViews() {
        userNameTextView.setText( user.getName() );
        cityUserTextView.setText( user.getCity() );
        userEmailTextView.setText( user.getEmail() );
        userPhoneTextView.setText( user.getPhone() );
    }

    private void updateImageView() {
        userPhotoImageView.setImageBitmap( userPhoto );
    }

    private void showMessage( String msg ) {
        Toast.makeText( getActivity(), msg, Toast.LENGTH_LONG ).show();
    }

}
