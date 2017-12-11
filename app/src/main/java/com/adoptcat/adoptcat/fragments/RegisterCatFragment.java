package com.adoptcat.adoptcat.fragments;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.model.Announcement;
import com.adoptcat.adoptcat.utilities.Utility;

import static android.app.Activity.RESULT_OK;

public class RegisterCatFragment extends Fragment implements View.OnClickListener {

    private ImageView catImageView;
    private EditText descriptionEditText, amountEditText;
    private Button deflocationButton;
    private CheckBox vaccinetedCheckbox, dewomedCheckbox, spayedCheckbox;
    private FloatingActionButton cancelFloatingActionButton, finishFloatingActionButton;

    private Announcement announcement;

    private Bitmap photoBitmap;
    private Uri photoUri;

    private final int SELECT_PHOTO_REQUEST = 1433;
    private final int TAKE_PHOTO_REQUEST = 1444;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_register_cat,container,false );

        catImageView = (ImageView) view.findViewById(R.id.catImageView);
        descriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
        amountEditText = (EditText) view.findViewById(R.id.amountEditText);
        deflocationButton = (Button) view.findViewById(R.id.deflocationButton);
        vaccinetedCheckbox = (CheckBox) view.findViewById(R.id.vaccinetedCheckbox);
        dewomedCheckbox = (CheckBox) view.findViewById(R.id.dewomedCheckbox);
        spayedCheckbox = (CheckBox) view.findViewById(R.id.spayedCheckbox);
        cancelFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.cancelFloatingActionButton);
        finishFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.finishFloatingActionButton);

        catImageView.setOnClickListener( this );

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.announcement = new Announcement();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.catImageView:
                selectPhoto();
                break;
        }
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
                this.photoUri = data.getData();
            }
        }
    }

    private void selectImage() {

        final CharSequence[] options = {getString(R.string.dialog_takephoto_option),
        getString(R.string.dialog_choosephoto_option), getString(R.string.dialog_cancel_option)};

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle( getString(R.string.dialog_title) );
        alertBuilder.setItems(options, new DialogInterface.OnClickListener() {

            boolean havePermission = Utility.checkPermission( getContext() );

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://take photo
                        if( havePermission ) cameraIntent();
                        break;
                    case 1://select from library
                        if( havePermission ) libraryIntent();
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
}
