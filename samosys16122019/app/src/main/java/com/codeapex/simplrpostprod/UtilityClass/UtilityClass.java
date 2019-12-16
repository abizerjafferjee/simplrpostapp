package com.codeapex.simplrpostprod.UtilityClass;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

public class UtilityClass {

    public static final String TAG = "Utilityclass";
    public static final String PREF_NAME_USER_LOGIN = "NameUser";
    public static final String PREF_KEY_USER_LOGIN = "KeyUser";
    public static final int ACCESS_FINE_LOCATION_REQUEST = 4;


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void showLoading(ProgressBar loader, Activity activity) {
        loader.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void hideLoading(ProgressBar loader, Activity activity) {
        loader.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void alertBox(final Context context, String msg,String alert){

        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        alertbox.setMessage(msg);
        alertbox.setTitle(alert);
        alertbox.setCancelable(false);
        alertbox.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0,
                                        int arg1) {


                    }
                });
        alertbox.show();

    }

    public static void hideKeyboard(Activity currentActivity) {
        View view = currentActivity.getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static SaveUserDetail getLoginData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME_USER_LOGIN, Context.MODE_PRIVATE);
        Gson myObj = new Gson();
        String response = sharedPreferences.getString(PREF_KEY_USER_LOGIN, "");
        return myObj.fromJson(response, SaveUserDetail.class);
    }

    public String imageToBase64(ImageView imageView) {
        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String imgg = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        imageView.setDrawingCacheEnabled(false);
        Log.d("k", imgg);
        return imgg;
    }
    public String image_ToBase64(Bitmap bmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imgg = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        Log.d("k", imgg);
        return imgg;
    }

    public static void clearLoginData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME_USER_LOGIN, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
    public static <T> boolean isLocationPermissionGranted(Activity context, T objectInstance) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                if (objectInstance instanceof Fragment) {
                    ((Fragment) objectInstance)
                            .requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    ACCESS_FINE_LOCATION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(
                            context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_FINE_LOCATION_REQUEST);
                }
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public static <T> boolean isStroagePermissionGranted(Activity context, T objectInstance,int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                if (objectInstance instanceof Fragment) {
                    ((Fragment) objectInstance)
                            .requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    requestCode);
                } else {
                    ActivityCompat.requestPermissions(
                            context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            requestCode);
                }
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    public void simpleAlertBox(final Context context, String msg){
        AlertDialog.Builder alertBox = new AlertDialog.Builder(context);
        alertBox.setMessage(msg).setCancelable(false).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog alert1 = alertBox.create();
        alert1.setCancelable(false);
        alert1.setTitle("Simplr Post");
        alert1.show();
    }


}
