package com.adoptcat.adoptcat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.fragments.AccountFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mainBottomNavView;
    private FirebaseAuth auth;
    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavItemSelectedListener;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBottomNavView = (BottomNavigationView) findViewById(R.id.mainBottomNavView);

        mBottomNavItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch( id ) {
                    case R.id.nav_profile:
                        openAccountFragment();
                        return true;
                    case R.id.nav_logout:
                        showLogoffDialog();
                        return true;
                }
                return false;
            }
        };

        mainBottomNavView.setOnNavigationItemSelectedListener(mBottomNavItemSelectedListener);

        auth = Connection.getFirebaseAuth();
    }



    private void showLogoffDialog() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle( getString( R.string.main_loggof_alert_title))
                .setMessage( getString( R.string.main_loggof_alert_message ) )
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        startActivity( new Intent( MainActivity.this, LoginActivity.class ));
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setIcon(R.drawable.icon_alert_logout);
        builder.show();
    }

    private void openAccountFragment() {

        if(fragmentManager == null)
            fragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_content, new AccountFragment());
        ft.commit();
    }

}
