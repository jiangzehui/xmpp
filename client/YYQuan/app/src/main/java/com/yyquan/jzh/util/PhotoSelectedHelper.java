package com.yyquan.jzh.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jack on 2015/7/14.
 */
public class PhotoSelectedHelper {

    public static final int TAKE_PHOTO = 2000;
    public static final int PIC_PHOTO = 3000;
    public static final int PHOTO_CROP = 4000;
    public static String camera_path;
    Activity mActivity;

    private Uri captureUri;

    private Uri cropUri;

    public PhotoSelectedHelper(Activity activity) {
        this.mActivity = activity;
    }

    public void imageSelection(String user, String action) {
        if (action.equals("take")) {
            intentCamera(user);
        } else if (action.equals("pic")) {
            intentPhoto();
        }


    }

    private void intentCamera(String user) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureUri = getOutputMediaFileUri(mActivity, user);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
        mActivity.startActivityForResult(intent, TAKE_PHOTO);
    }

    private void intentPhoto() {
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        mActivity.startActivityForResult(i, PICK_PHOTO);
        Intent intentss = new Intent(Intent.ACTION_GET_CONTENT);
        intentss.setType("image/*");
        mActivity.startActivityForResult(intentss, PIC_PHOTO);
    }

    public Uri getCaptureUri() {
        return captureUri;
    }

    public String getCapturePath() {
        return captureUri.getPath();
    }

    /*public String getPickPath(Context context,Uri data){
        String[] projection={MediaStore.Images.Media.DATA};
        Cursor cursor=context.getContentResolver().query(data,projection,null,null,null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(projection[0]);
        String picturePath=cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }*/

    public String getPickPath(Context context, Uri data) {
        return PhotoURIUtils.getPath(context, data);
    }

    public String getCropPath() {
        return cropUri.getPath();
    }

    public static Uri getOutputMediaFileUri(Context context, String user) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getPackageName());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("OutputMediaFileUri", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        camera_path = mediaStorageDir.getPath() + File.separator + user + "_" + timeStamp
                + ".jpg";
        File mediaFile = new File(camera_path);
        return Uri.fromFile(mediaFile);
    }


//    public static String getRealPathFromURI(Context context, Uri contentUri) {
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    public void cropImageUri(Uri uri, int outputX, int outputY, String user) {
        cropUri = getOutputMediaFileUri(mActivity, user);
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            mActivity.startActivityForResult(intent, PHOTO_CROP);
        } catch (Exception e) {

        }
    }


}
