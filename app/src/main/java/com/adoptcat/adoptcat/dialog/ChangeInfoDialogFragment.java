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

import static android.app.Activity.RESULT_OK;


public class ChangeInfoDialogFragment extends DialogFragment implements View.OnClickListener {


    private EditText infoEditText;
    private Button confirmButton, cancelButton;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.dialog_change_information, null);

        infoEditText = (EditText) view.findViewById(R.id.infoEditText);
        confirmButton = (Button) view.findViewById(R.id.confirmButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);


        return view;
    }


    public void changeInformation() {

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch( id ) {
            case R.id.confirmButton:
                String info = infoEditText.getText().toString();
                if(!info.isEmpty()) finishUpdate();
                else showMessage( getString(R.string.dig_field_empty));
                break;
            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }


    private void finishUpdate() {

    }

    public void showMessage( String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
