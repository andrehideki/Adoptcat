package com.adoptcat.adoptcat.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button resetPasswordButton;
    private EditText emailForgotPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        emailForgotPasswordEditText = (EditText) findViewById(R.id.emailForgotPasswordEditText);
    }

    public void sendPasswdToEmail(View view) {
        final String email = emailForgotPasswordEditText.getText().toString().trim();
        if( !email.isEmpty() ) {
            FirebaseAuth auth = Connection.getFirebaseAuth();
            auth.sendPasswordResetEmail( email ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( task.isSuccessful() ) {
                        showMessage( getString(R.string.forgot_password_emai_sended) + " " + email );
                    } else {
                        showMessage( getString( R.string.forgot_password_fail_to_send) );
                    }
                }
            });
        } else {
            showMessage( getString(R.string.forgot_password_email_is_empty));
        }
    }

    private void showMessage( String msg ) {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
    }
}
