package com.adoptcat.adoptcat.utilities;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adoptcat.adoptcat.R;

public class Utility {


    public static int currentAPIVersion = Build.VERSION.SDK_INT;

    public static final int PERMISSION_REQUEST = 4456;

    public static boolean checkPermission(Context context ) {

        if(currentAPIVersion >= android.os.Build.VERSION_CODES.M ) {
            if(ContextCompat.checkSelfPermission( context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                if( ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST);
                }
                return false;
            }
            else
                return true;
        }
        else
            return true;
    }

    public static void checkLocationPermission( Context context ) {
        if( ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if( ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_FINE_LOCATION )) {
                callDialog( context.getString(R.string.request_permission),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, context);
            }
            else
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST);
        }
    }

    public static void checkAccesStoragePermission( Context context ) {
        if( ContextCompat.checkSelfPermission( context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if( ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE )) {
                callDialog( context.getString(R.string.request_permission),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, context);
            }
            else
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        }
    }

    public static void checkCameraPermission( Context context ) {
        if( ContextCompat.checkSelfPermission( context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if( ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.CAMERA )) {
                callDialog( context.getString(R.string.request_permission),
                        new String[]{Manifest.permission.CAMERA}, context);
            }
            else
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST);
        }
    }

    private static void callDialog(String msg, final String[] permission, final Context context) {

        AlertDialog.Builder dialog = new AlertDialog.Builder( context );
        dialog.setTitle( context.getString( R.string.request_permission_title) );
        dialog.setMessage( msg );
        dialog.setPositiveButton(context.getString(R.string.request_permission_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity) context, permission, PERMISSION_REQUEST);
                dialog.dismiss();
            }
        })
        .setNegativeButton(context.getString(R.string.request_permission_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
