package com.adoptcat.adoptcat.activities;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLoginEditText, passwordLoginEditText;
    private Button loginLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailLoginEditText = (EditText) findViewById(R.id.emailLoginEditText);
        passwordLoginEditText = (EditText) findViewById(R.id.passwordLoginEditText);
        loginLoginButton = (Button) findViewById(R.id.loginLoginButton);
    }

    public void openResgisterActivity(View view) {
        startActivity( new Intent(LoginActivity.this, RegisterActivity.class ) );
        clearPassword();
    }

    public void openForgotPasswordActivity(View view) {
        startActivity( new Intent(LoginActivity.this, ForgotPasswordActivity.class ) );
        clearPassword();
    }


    private void showMessage( String msg ) {
        Toast.makeText( this, msg, Toast.LENGTH_SHORT).show();
    }

    public void doLogin(View view) {

        String email, password;
        email = emailLoginEditText.getText().toString().trim();
        password = passwordLoginEditText.getText().toString().trim();

        if( !email.isEmpty() && !password.isEmpty() ) {
            FirebaseAuth auth = Connection.getFirebaseAuth();
            auth.signInWithEmailAndPassword( email, password ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        showMessage( getString(R.string.login_sucessful));
                        startActivity( new Intent( LoginActivity.this, MainActivity.class) );
                        finish();
                    } else {
                        showMessage(getString(R.string.login_invalid_login_or_passwd));
                        clearPassword();
                    }
                }
            });
        } else {
            showMessage( getString(R.string.login_fields_empty));
        }
    }

    private void clearPassword() {
        passwordLoginEditText.setText("");
    }


}
