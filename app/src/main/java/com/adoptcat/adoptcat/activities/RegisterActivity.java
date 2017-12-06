package com.adoptcat.adoptcat.activities;

import android.app.FragmentManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.dialog.PhotoDialogFragment;
import com.adoptcat.adoptcat.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton takePhotoButton;
    private EditText nameRegisterEditText, emailRegisterEditText, phoneRegisterEditText,
            passwordRegisterEditText;
    private Button finishRegisterButton;
    private static Uri photo;

    private PhotoDialogFragment dialog;

    public final static String DIALOG_TAG = "PhotoDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_register );

        takePhotoButton = (ImageButton) findViewById(R.id.takePhotoButton);
        nameRegisterEditText = (EditText) findViewById(R.id.nameRegisterEditText);
        emailRegisterEditText = (EditText) findViewById(R.id.emailRegisterEditText);
        phoneRegisterEditText = (EditText) findViewById(R.id.phoneRegisterEditText);
        passwordRegisterEditText = (EditText) findViewById(R.id.passwordRegisterEditText);
        finishRegisterButton = (Button) findViewById(R.id.finishRegisterButton);
        finishRegisterButton.setOnClickListener( this );
    }

    public void showTakePictureDialog( View view ) {

        FragmentManager manager = getFragmentManager();
        if( dialog == null ) dialog = new PhotoDialogFragment();
        dialog.show( manager, DIALOG_TAG );

    }

    @Override
    protected void onResume() {
        super.onResume();
        if( photo != null ) takePhotoButton.setBackgroundColor(Color.parseColor("#00E676"));
    }

    public static void setPhoto(Uri photo) {
        RegisterActivity.photo = photo;

    }

    public void finishRegister() {

        String name, email, phone, password;

        name = nameRegisterEditText.getText().toString();
        email = emailRegisterEditText.getText().toString();
        phone = phoneRegisterEditText.getText().toString();
        password = passwordRegisterEditText.getText().toString();

        if(verifyFields()) {
            final User user = new User( name, email, phone );
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
                photo != null;
    }


    private void showMessage( String msg ) {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        finishRegister();
    }
}
