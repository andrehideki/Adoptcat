package com.adoptcat.adoptcat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.adoptcat.adoptcat.dialog.PhotoDialogFragment;
import com.adoptcat.adoptcat.model.User;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton takePhotoButton;
    private EditText nameRegisterEditText, emailRegisterEditText, phoneRegisterEditText,
            passwordRegisterEditText;
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

    public void finishRegister(View view) {

        String name, email, phone, password;
        name = email = phone = password = "";
        getFildValues(name, email, phone, password);

        if(verifyFields()) {
            User user = new User( name, email, phone );
        }
    }

    private boolean verifyFields() {
        return  !(nameRegisterEditText.getText().toString().isEmpty()) &&
                !(emailRegisterEditText.getText().toString().isEmpty()) &&
                !(phoneRegisterEditText.getText().toString().isEmpty()) &&
                !(passwordRegisterEditText.getText().toString().isEmpty()) &&
                photo != null;
    }

    private void getFildValues( String name, String email, String phone, String password ) {
        name = nameRegisterEditText.getText().toString();
        email = emailRegisterEditText.getText().toString();
        phone = phoneRegisterEditText.getText().toString();
        password = passwordRegisterEditText.getText().toString();
    }
}
