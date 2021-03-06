package com.adoptcat.adoptcat.fragments;

import android.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.dialog.ChangeInfoDialogFragment;
import com.adoptcat.adoptcat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


import com.adoptcat.adoptcat.R;

public class AccountFragment extends Fragment implements View.OnClickListener, ValueEventListener {

    private TextView userNameTextView, cityUserTextView, userPhoneTextView, userEmailTextView;
    private ImageView userPhotoImageView;
    private ImageButton editInformationsButton;

    private User user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Bitmap userPhoto;

    final long THREE_MB = 1024 * 1024 * 4;
    public final static String DIALOG_TAG = "PhotoDialog";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, null);

        userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
        cityUserTextView = (TextView) view.findViewById(R.id.cityUserTextView);
        userPhoneTextView = (TextView) view.findViewById(R.id.userPhoneTextView);
        userEmailTextView = (TextView) view.findViewById(R.id.userEmailTextView);
        userPhotoImageView = (ImageView) view.findViewById(R.id.userPhotoImageView);
        editInformationsButton = (ImageButton) view.findViewById(R.id.editInformationsButton);

        editInformationsButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = User.getInstance();

        databaseReference = Connection.getDatabaseUsersReference().child( user.getUUID() );
        storageReference = Connection.getStorageReference().child( user.getUUID() ).child("UserPhoto");

        databaseReference.addValueEventListener(this);


        //Picasso.with( getContext() ).load("gs://adoptcat-ee784.appspot.com/" + user.getUUID() + "/UserPhoto").
          //      resize(userPhotoImageView.getMaxWidth(), userPhotoImageView.getMaxHeight()).into(userPhotoImageView);

        storageReference.getBytes( THREE_MB ).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                options.inSampleSize = calculateSize( options, userPhotoImageView.getWidth(), userPhotoImageView.getHeight());

                options.inJustDecodeBounds = false;
                userPhoto = BitmapFactory.decodeByteArray( bytes, 0, bytes.length, options );
                updateImageView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage( getString(R.string.account_failed_donwload_uphoto ) );
            }
        });

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


    public void showChangeInfoDialog() {

        FragmentManager manager = getActivity().getFragmentManager();
        ChangeInfoDialogFragment dialog = new ChangeInfoDialogFragment();
        dialog.show( manager, DIALOG_TAG );

    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener( this );
    }

    @Override
    public void onClick(View v) {
        showChangeInfoDialog();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User u = dataSnapshot.getValue( User.class );
        User.cloneState( u );
        updateViews();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
