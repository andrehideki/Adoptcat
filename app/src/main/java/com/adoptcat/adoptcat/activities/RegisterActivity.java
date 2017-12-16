package com.adoptcat.adoptcat.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.User;
import com.adoptcat.adoptcat.utilities.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton takePhotoButton;
    private EditText nameRegisterEditText, emailRegisterEditText, phoneRegisterEditText,
            passwordRegisterEditText, cityRegisterEditText;
    private Button finishRegisterButton;
    private static Uri photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_register );

        takePhotoButton = (ImageButton) findViewById(R.id.takePhotoButton);
        nameRegisterEditText = (EditText) findViewById(R.id.nameRegisterEditText);
        emailRegisterEditText = (EditText) findViewById(R.id.emailRegisterEditText);
        phoneRegisterEditText = (EditText) findViewById(R.id.phoneRegisterEditText);
        passwordRegisterEditText = (EditText) findViewById(R.id.passwordRegisterEditText);
        cityRegisterEditText = (EditText) findViewById(R.id.cityRegisterEditText);
        finishRegisterButton = (Button) findViewById(R.id.finishRegisterButton);

        finishRegisterButton.setOnClickListener( this );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( photo != null ) takePhotoButton.setBackgroundColor(Color.parseColor("#00E676"));
    }

    public static void setPhoto(Uri photo) {
        RegisterActivity.photo = photo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch( requestCode ) {
            case Utility.CAMERA_REQUEST_CODE: {
                if(resultCode == RESULT_OK)  {
                    photo = data.getData();
                }
                break;
            }
            case Utility.READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    photo = data.getData();
                }
                break;
            }
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

    public void finishRegister() {

        final String name, email, phone, password, city;

        name = nameRegisterEditText.getText().toString();
        email = emailRegisterEditText.getText().toString();
        phone = phoneRegisterEditText.getText().toString();
        password = passwordRegisterEditText.getText().toString();
        city = cityRegisterEditText.getText().toString();

        if(verifyFields()) {
            final User user = User.getInstance();
            user.setName( name );
            user.setEmail( email );
            user.setPhone( phone );
            user.setCity( city );
            FirebaseAuth auth = Connection.getFirebaseAuth();

            auth.createUserWithEmailAndPassword( email, password ).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                DatabaseReference databaseReference = Connection.getDatabaseUsersReference();
                                StorageReference storageReference = Connection.getStorageReference();
                                user.setUUID( Connection.getFirebaseUser().getUid() );

                                databaseReference.child( user.getUUID() ).setValue( user );
                                if( photo != null )
                                    storageReference.child( user.getUUID() ).child("UserPhoto").putFile( photo );

                                showMessage( getString(R.string.register_finished ));
                                finish();
                            } else {
                                showMessage( getString(R.string.register_failed) );
                            }
                        }
                    });
        }
    }

    private boolean verifyFields() {
        return  !(nameRegisterEditText.getText().toString().isEmpty()) &&
                !(emailRegisterEditText.getText().toString().isEmpty()) &&
                !(phoneRegisterEditText.getText().toString().isEmpty()) &&
                !(passwordRegisterEditText.getText().toString().isEmpty()) &&
                !(cityRegisterEditText.getText().toString().isEmpty());
    }


    private void showMessage( String msg ) {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
    }

    @Override

    public void onClick(View v) {
        finishRegister();
    }



    public void selectImage(View view ) {

        final CharSequence[] options = {getString(R.string.dialog_takephoto_option),
                getString(R.string.dialog_choosephoto_option), getString(R.string.dialog_cancel_option)};

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder( RegisterActivity.this );
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
        if(ActivityCompat.checkSelfPermission( RegisterActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale( RegisterActivity.this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions( RegisterActivity.this, new String[] { Manifest.permission.CAMERA }, Utility.CAMERA_REQUEST_PERMISSION );
            } else {
                ActivityCompat.requestPermissions( RegisterActivity.this, new String[] { Manifest.permission.CAMERA },Utility.CAMERA_REQUEST_PERMISSION );
            }
        } else {
            takePicture();
        }
    }

    private void libraryIntent() {
        if( ActivityCompat.checkSelfPermission( RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) !=
                PackageManager.PERMISSION_GRANTED ) {
            if( ActivityCompat.shouldShowRequestPermissionRationale( RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE )) {
                ActivityCompat.requestPermissions( RegisterActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION);
            } else {
                ActivityCompat.requestPermissions( RegisterActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                        Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION);
            }
        } else {
            selectPhoto();
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
}

