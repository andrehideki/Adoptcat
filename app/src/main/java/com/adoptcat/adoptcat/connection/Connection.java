package com.adoptcat.adoptcat.connection;


import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Connection {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    private static FirebaseUser firebaseUser;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference databaseReference;
    private static FirebaseStorage firebaseStorage;
    private static StorageReference storageReference;


    public static FirebaseAuth getFirebaseAuth() {
        if(firebaseAuth == null ) {
            firebaseAuth = FirebaseAuth.getInstance();
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if( user != null ) firebaseUser = user;
                }
            };
            firebaseAuth.addAuthStateListener( authStateListener );
        }
        return firebaseAuth;
    }

    public static DatabaseReference getDatabaseUsersReference() {
        if(databaseReference == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled( true );
            databaseReference = firebaseDatabase.getReference();
        }
        return databaseReference.child("Users");
    }

    public static DatabaseReference getProblemsDatabaseReference() {
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().
                    getReferenceFromUrl("https://urban-fix-teste.firebaseio.com/Alerts" );
            firebaseDatabase.setPersistenceEnabled( true );
        }
        return databaseReference;
    }


    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static FirebaseStorage getFirebaseStorage() {
        if( firebaseStorage == null )
            firebaseStorage = FirebaseStorage.getInstance();
        return firebaseStorage;
    }

    public static StorageReference getStorageReference() {
        if( firebaseStorage == null ) firebaseStorage = FirebaseStorage.getInstance();
        if( storageReference == null ) {
            storageReference = firebaseStorage.getReference();
        }
        return storageReference;
    }

    public static void logout() {
        firebaseAuth.signOut();
    }
}
