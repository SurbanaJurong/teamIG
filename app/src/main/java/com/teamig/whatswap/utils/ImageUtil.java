package com.teamig.whatswap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lyk on 26/5/17.
 */

public class ImageUtil {
    public static File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/DCIM");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public static void rotatePhoto(Uri uri, Context context, String path) throws IOException {

        //get exifOrientation
        int exifOrientation = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION,0);

        int rotate;
        boolean switchWH = true;
        boolean need2Rotate = true;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                switchWH = false;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                switchWH = true;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                switchWH = false;
                break;
            default:
                rotate = 0;
                need2Rotate = false;
                switchWH = false;
                break;
        }

        if(need2Rotate){
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);
            Bitmap rotated;
            Bitmap scaledBitmap = null;

            if(bitmap.getWidth()==bitmap.getHeight())
                switchWH = false;

            if(switchWH){
                scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getHeight(),bitmap.getWidth(),true);
                rotated = Bitmap.createBitmap(scaledBitmap,0,0,scaledBitmap.getWidth(),scaledBitmap.getHeight(),mtx,false);
            }

            else
                rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, false);

            //write to file
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
                rotated.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(scaledBitmap!=null)
                scaledBitmap.recycle();
            bitmap.recycle();
        }

    }
}
