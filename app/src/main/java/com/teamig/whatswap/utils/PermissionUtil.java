package com.teamig.whatswap.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


/**
 * Created by lyk on 26/5/17.
 */

public class PermissionUtil {

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;


    public static boolean checkPermissionForExternalStorage(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissionForCamera(Activity activity){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissionForExternalStorage(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestPermissionForCamera(Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)){
            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

}
