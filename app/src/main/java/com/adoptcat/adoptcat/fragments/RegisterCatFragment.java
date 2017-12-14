package com.adoptcat.adoptcat.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.adoptcat.adoptcat.model.User;
import com.adoptcat.adoptcat.utilities.Utility;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.Map;

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

    public final static String DIALOG_TAG = "LocationTag";


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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.announcement = new Announcement();
        user = User.getInstance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.deflocationButton:
                defMyLocation();
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
                deflocationButton.setBackgroundColor( Color.GREEN );
                deflocationButton.setTextColor( Color.WHITE );
                locationTextView.setText("OK");
                MapsFragment.setIsDefineUserPositionFalse();
                break;
        }
    }

    private void defMyLocation() {
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
        //TODO verificar se o usuário configurau a sua localização
        return !description.isEmpty() && amount > 0;
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
        //Manda a foto
        //StorageReference storageReference = Connection.getStorageReference().child(user.getUUID());
        //Salvando anuncio
        DatabaseReference databaseReference = Connection.getAnnouncementsDatabaseReference().child(userUUID + amountOfAnnounces);
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

    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser( intent, getString(R.string.registercat_select_photo) ), SELECT_PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == RESULT_OK ) {
            if( resultCode == SELECT_PHOTO_REQUEST ) {
                Bundle b = data.getExtras();
                photoBitmap = (Bitmap) b.get("data");
                this.photoUri = data.getData();
                catImageView.setImageBitmap( photoBitmap );
            }
        }
    }

    private void selectImage() {

        final CharSequence[] options = {getString(R.string.dialog_takephoto_option),
        getString(R.string.dialog_choosephoto_option), getString(R.string.dialog_cancel_option)};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle( getString(R.string.dialog_title) );
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

                }
            }
        });
        alertBuilder.show();
    }

    private void cameraIntent() {
        startActivityForResult( new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO_REQUEST );
    }

    private void libraryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser( intent, getString(R.string.registercat_select_photo) ), SELECT_PHOTO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case Utility.PERMISSION_REQUEST:
                for( int i = 0; i < permissions.length; i++ ) {
                     if( permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION )
                             && grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                         //Leia as minhas coordenadas
                     }else if( permissions[i].equalsIgnoreCase( Manifest.permission.READ_EXTERNAL_STORAGE )
                             && grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        libraryIntent();
                     }else if( permissions[i].equalsIgnoreCase( Manifest.permission.CAMERA )
                             && grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                         cameraIntent();
                     }
                }
        }
    }
    private void showMessage( String msg ) {
        Toast.makeText( getContext(), msg, Toast.LENGTH_LONG ).show();
    }

    public static void setVisibleConfirmFloatingActionButton() {
        confirmFloatingActionButton.setVisibility(FloatingActionButton.VISIBLE);
    }


}
