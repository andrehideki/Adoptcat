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
import android.widget.ImageView;
import android.widget.Toast;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.activities.RegisterActivity;

import static android.app.Activity.RESULT_OK;


public class PhotoDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageView photoImageView;
    private Button confirmButton, cancelButton;
    public final int CAMERA_REQUEST_CODE = 9999;
    private Uri photo;
    private Bitmap photoBitMap;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.dialog_take_picture, null);
        photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
        confirmButton = (Button) view.findViewById(R.id.confirmButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        photoImageView.setOnClickListener( this );
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        if(photoBitMap!=null) photoImageView.setImageBitmap( photoBitMap );

        return view;
    }


    public void takePicture() {
        Intent cameraItent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraItent, CAMERA_REQUEST_CODE );
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch( id ) {
            case R.id.photoImageView:
                takePicture();
                break;
            case R.id.confirmButton:
                if( photo != null )dismiss();
                else showMessage( getString(R.string.dig_photo_empty));
                break;
            case R.id.cancelButton:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ) {
            Bundle bundle = data.getExtras();
            this.photo = data.getData();
            photoBitMap = (Bitmap) bundle.get("data");
            photoImageView.setImageBitmap(photoBitMap);
            RegisterActivity.setPhoto( photo );
        }
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public void showMessage( String msg ) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
