package com.adoptcat.adoptcat.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.adoptcat.adoptcat.model.User;
import com.adoptcat.adoptcat.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class RegisterCatFragment extends Fragment implements View.OnClickListener {

    private ImageView catImageView;
    private EditText descriptionEditText, amountEditText, titleEditText;
    private TextView locationTextView;
    private Button deflocationButton;
    private CheckBox vaccinetedCheckbox, dewomedCheckbox, spayedCheckbox;
    private FloatingActionButton cancelFloatingActionButton;
    private FloatingActionButton finishFloatingActionButton;
    private static FloatingActionButton confirmFloatingActionButton;
    private CoordinatorLayout fragMap;

    private Announcement announcement;
    private User user;

    private Bitmap photoBitmap;
    private Uri photoUri;

    private final int SELECT_PHOTO_REQUEST = 1433;
    private final int TAKE_PHOTO_REQUEST = 1444;

    private String description, title;
    private int amount = 1;
    private boolean vaccinated, dewomed, spayed;

    final long THREE_MB = 1024 * 1024 * 4;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_register_cat,container,false );

        locationTextView = (TextView) view.findViewById(R.id.locationTextView);
        catImageView = (ImageView) view.findViewById(R.id.catImageView);
        descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
        titleEditText = (EditText) view.findViewById(R.id.titleEditText);
        amountEditText = (EditText) view.findViewById(R.id.amountEditText);
        deflocationButton = (Button) view.findViewById(R.id.deflocationButton);
        vaccinetedCheckbox = (CheckBox) view.findViewById(R.id.vaccinetedCheckbox);
        dewomedCheckbox = (CheckBox) view.findViewById(R.id.dewomedCheckbox);
        spayedCheckbox = (CheckBox) view.findViewById(R.id.spayedCheckbox);
        cancelFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.cancelFloatingActionButton);
        finishFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.finishFloatingActionButton);
        confirmFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.confirmFloatingActionButton);
        fragMap = (CoordinatorLayout) view.findViewById(R.id.fragMap);

        deflocationButton.setOnClickListener( this );
        catImageView.setOnClickListener( this );
        cancelFloatingActionButton.setOnClickListener( this );
        finishFloatingActionButton.setOnClickListener( this );
        confirmFloatingActionButton.setOnClickListener( this );

        try {
            this.announcement = (Announcement) (getArguments()).get("announcement");
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage() );
        }

        if( announcement != null ) fillFields();


        return view;
    }

    private void fillFields() {
        titleEditText.setText( this.announcement.getTitle() );
        descriptionEditText.setText( this.announcement.getDescription());
        amountEditText.setText( "" + this.announcement.getAmount() );
        vaccinetedCheckbox.setChecked( this.announcement.isVaccineted());
        spayedCheckbox.setChecked( this.announcement.isSpayed());
        dewomedCheckbox.setChecked( this.announcement.isDewomed());
        deflocationButton.setBackgroundColor( Color.GREEN );
        deflocationButton.setTextColor( Color.WHITE );

        if( announcement.isHasPhoto() ) {
            StorageReference storageReference = Connection.getStorageReference().child(announcement.getId());
            storageReference.getBytes(THREE_MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] result = task.getResult();
                    if (result.length > 0) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(result, 0, result.length, options);
                        options.inSampleSize = calculateSize(options, catImageView.getWidth(), catImageView.getHeight());

                        options.inJustDecodeBounds = false;
                        catImageView.setImageBitmap(BitmapFactory.decodeByteArray(result, 0, result.length, options));
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if( this.announcement == null)  this.announcement = new Announcement();
        user = User.getInstance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.deflocationButton:
                if(ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) !=
                        PackageManager.PERMISSION_GRANTED ) {
                    if( ActivityCompat.shouldShowRequestPermissionRationale( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) ) {
                        ActivityCompat.requestPermissions( getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                Utility.ACCESS_FINE_LOCATION_REQUEST_PERMISSION );
                    } else {
                        ActivityCompat.requestPermissions( getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                Utility.ACCESS_FINE_LOCATION_REQUEST_PERMISSION );
                    }
                } else {
                    defMyLocation();
                }
                break;
            case R.id.catImageView:
                selectImage();
                break;
            case R.id.finishFloatingActionButton:
                if( fieldsAreValid() ) finishAnnouncement();
                else showMessage( getString(R.string.registercat_fieldsproblem) );
                break;
            case R.id.cancelFloatingActionButton:
                getFragmentManager().popBackStack();
                break;
            case R.id.confirmFloatingActionButton:
                announcement.setLatitude( MapsFragment.getClickedLocation().latitude );
                announcement.setLongitude( MapsFragment.getClickedLocation().longitude );
                fragMap.setVisibility(CoordinatorLayout.GONE);
                finishFloatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
                cancelFloatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
                deflocationButton.setBackgroundColor( Color.GREEN );
                deflocationButton.setTextColor( Color.WHITE );
                locationTextView.setText("OK");
                MapsFragment.setIsDefineUserPositionFalse();
                break;
        }
    }

    private void defMyLocation() {
        finishFloatingActionButton.setVisibility(FloatingActionButton.GONE);
        cancelFloatingActionButton.setVisibility(FloatingActionButton.GONE);
        FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragMap, new MapsFragment());
        ft.commit();
        MapsFragment.setIsDefineUserPositionTrue();
        fragMap.setVisibility(CoordinatorLayout.VISIBLE);
    }

    private boolean fieldsAreValid() {
        description = descriptionEditText.getText().toString().trim();
        title = titleEditText.getText().toString().trim();
        amount = Integer.parseInt( amountEditText.getText().toString() );
        spayed = spayedCheckbox.isChecked();
        dewomed = dewomedCheckbox.isChecked();
        vaccinated = vaccinetedCheckbox.isChecked();
        return !description.isEmpty() && amount > 0 &&
                !(announcement.getLatitude() == 0 && announcement.getLongitude() == 0);
    }

    private void finishAnnouncement() {
        String userUUID = user.getUUID();
        int amountOfAnnounces = user.getAmountOfAnnounces();
        announcement.setDescription( description );
        announcement.setTitle( title );
        announcement.setAmount( amount );
        announcement.setDate( new Date().toString() );
        announcement.setDewomed( dewomed );
        announcement.setSpayed( spayed );
        announcement.setVaccineted( vaccinated );
        announcement.setPhone( user.getPhone() );
        announcement.setUserUUID( userUUID );
        if( announcement.getId() == null ) announcement.setId( userUUID + amountOfAnnounces );

        //Manda a foto caso o usuário defina a foto
        if(photoUri != null) {
            StorageReference storageReference = Connection.getStorageReference().child(announcement.getId());
            storageReference.putFile(photoUri);
            announcement.setHasPhoto( true );
        }
        //Salvando anuncio
        DatabaseReference databaseReference = Connection.getAnnouncementsDatabaseReference().
                child( announcement.getId() );
        databaseReference.setValue( announcement ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage( getString(R.string.registercat_success_));
            }
        });
        //Atualiza user
        user.setAmountOfAnnounces( user.getAmountOfAnnounces() + 1);
        databaseReference = Connection.getDatabaseUsersReference().child(userUUID);
        databaseReference.setValue( user );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch( requestCode ) {
            case Utility.CAMERA_REQUEST_CODE: {
                if(resultCode == RESULT_OK)  {
                    photoUri = data.getData();
                    photoBitmap = (Bitmap) ((data.getExtras()).get("data"));
                    catImageView.setImageBitmap( photoBitmap );
                }
                break;
            }
            case Utility.READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    photoUri = data.getData();
                    try {
                        photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catImageView.setImageBitmap(photoBitmap);
                }
                break;
            }
        }
    }

    private void selectImage() {

        final CharSequence[] options = {getString(R.string.dialog_takephoto_option),
        getString(R.string.dialog_choosephoto_option), getString(R.string.dialog_cancel_option)};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle( getString(R.string.dialog_photo_title) );
        alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://take photo
                        cameraIntent();
                        dialog.dismiss();
                        //Utility.checkCameraPermission( getContext() );
                        break;
                    case 1://select from library
                        //if( havePermission ) libraryIntent();
                        libraryIntent();
                        dialog.dismiss();
                        break;
                    case 2://cancel
                        dialog.cancel();
                }
            }
        });
        alertBuilder.show();
    }

    private void cameraIntent() {
        if(ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale( getActivity(), Manifest.permission.CAMERA)) {
                requestPermissions( new String[] { Manifest.permission.CAMERA },Utility.CAMERA_REQUEST_PERMISSION );
            } else {
                requestPermissions( new String[] { Manifest.permission.CAMERA },Utility.CAMERA_REQUEST_PERMISSION );
            }
        } else {
            takePicture();
        }
    }

    private void libraryIntent() {
        if( ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE ) !=
                PackageManager.PERMISSION_GRANTED ) {
            if( ActivityCompat.shouldShowRequestPermissionRationale( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE )) {
                requestPermissions( new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION);
            } else {
                requestPermissions( new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION);
            }
        } else {
            selectPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case Utility.CAMERA_REQUEST_PERMISSION: {
                if( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    takePicture();
            }
            case Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION: {
                if( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    selectPhoto();
            }
        }
    }


    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser( intent, getString(R.string.registercat_select_photo) ), Utility.READ_EXTERNAL_STORAGE_REQUEST_CODE);
    }

    private void takePicture() {
        startActivityForResult( new Intent(MediaStore.ACTION_IMAGE_CAPTURE), Utility.CAMERA_REQUEST_CODE );
    }


    public static void setVisibleConfirmFloatingActionButton() {
        confirmFloatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
    }

    private void showMessage( String msg ) {
        Toast.makeText( getContext(), msg, Toast.LENGTH_LONG ).show();
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
