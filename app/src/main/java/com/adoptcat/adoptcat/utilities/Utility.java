package com.adoptcat.adoptcat.utilities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Utility {


    public static final int PERMISSION_REQUEST = 4456;
    public static boolean checkPermission(Context context ) {

        int currentAPIVersion = Build.VERSION.SDK_INT;
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


}
