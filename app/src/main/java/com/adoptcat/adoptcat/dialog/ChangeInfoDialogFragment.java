package com.adoptcat.adoptcat.dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.activities.RegisterActivity;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.User;
import com.google.firebase.database.DatabaseReference;

import static android.app.Activity.RESULT_OK;


public class ChangeInfoDialogFragment extends DialogFragment implements View.OnClickListener {


    private EditText nameEditText, phoneEditText, cityEditText;
    private Button confirmButton, cancelButton;
    private String name, phone, city;
    private User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.dialog_change_information, null);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);
        confirmButton = (Button) view.findViewById(R.id.confirmButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


        initViewsWithUserValues();

        return view;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch( id ) {
            case R.id.confirmButton:
                if( verifyFieds() ) {
                    finishUpdate();
                    dismiss();
                }
                else
                    showMessage( getString(R.string.dig_field_empty));
                break;
            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }


    private void finishUpdate() {
        DatabaseReference databaseReference = Connection.getDatabaseUsersReference().child( user.getUUID() );
        user.setName( this.name );
        user.setCity( this.city );
        user.setPhone( this.phone );
        databaseReference.setValue( user );
    }

    public void showMessage( String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void initViewsWithUserValues() {
        user = User.getInstance();
        nameEditText.setText( user.getName() );
        phoneEditText.setText( user.getPhone() );
        cityEditText.setText( user.getCity() );
    }

    private boolean verifyFieds() {
        this.name = nameEditText.getText().toString().trim();
        this.phone = phoneEditText.getText().toString().trim();
        this.city = cityEditText.getText().toString().trim();
        return !name.isEmpty() && !phone.isEmpty() && !city.isEmpty();
    }
}
