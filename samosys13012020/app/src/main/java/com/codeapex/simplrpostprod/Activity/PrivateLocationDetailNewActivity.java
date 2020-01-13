package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class PrivateLocationDetailNewActivity extends AppCompatActivity implements OnMapReadyCallback {


    String address_id, user_Id, pictureURL, shortName, addressReferenceId, address, emailId, plusCode, website, facebook, description, addressId, twitter, linkedin, instagram, latitude, longitude, qrCodeURL, currentLatitude, currentLongitude, privateAddressData;


    GoogleMap mapObject;
    SupportMapFragment fgmtMap;
    ConstraintLayout clytRootLayer;
    String provider;
    NestedScrollView nscrlMainContainer;
    ImageView imgBackButton, imgEditButton, imgDirectionButton, imgHeart, imgLocationPictureButton, imgCallButton, imgMessageButton, imgLocation, imgDelete;
    TextView txtvTitle, txtvAddress, txtvLandmark, txtvLocationName, txtvPhoneNumber, txtvEmailId;
    LocationManager locationManager;
    LinearLayout lnlytLocationPhotosButton, lnlytQRCodeButton, lnlytSaveAddressButton, lnlytShareAddress, lnlytAddressSharedWith;
    ProgressBar Loader;

    NestedScrollView nestedScrollView;


    int requestCode = 200;


    PlaceDetectionClient mPlaceDetectionClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_private_location_detail_new);

        //Map scroll


        mapObject = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        nestedScrollView = findViewById(R.id.mainContainer);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                nestedScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        nscrlMainContainer = findViewById(R.id.mainContainer);
        clytRootLayer = findViewById(R.id.rootLayer);
        imgBackButton = findViewById(R.id.back_press);
        imgEditButton = findViewById(R.id.editButton);
        txtvTitle = findViewById(R.id.titleText);
        txtvPhoneNumber = findViewById(R.id.phoneNumber);
        txtvEmailId = findViewById(R.id.emailId);
        imgCallButton = findViewById(R.id.callButton);
        imgMessageButton = findViewById(R.id.messageButton);
        Loader = findViewById(R.id.Loader);
        imgLocation = findViewById(R.id.imgLogo);
        fgmtMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        txtvAddress = findViewById(R.id.address);
        txtvLandmark = findViewById(R.id.landmark);
        lnlytLocationPhotosButton = findViewById(R.id.locationPhotosLayout);
        txtvLocationName = findViewById(R.id.locationName);
        lnlytQRCodeButton = findViewById(R.id.qrCode);
        lnlytSaveAddressButton = findViewById(R.id.saveAddress);
        imgHeart = findViewById(R.id.imgHeart);
        imgDirectionButton = findViewById(R.id.direction);
        imgLocationPictureButton = findViewById(R.id.locationPictures);
        imgDelete = findViewById(R.id.imgDelete);
        lnlytShareAddress = findViewById(R.id.shareAddress);
        lnlytAddressSharedWith = findViewById(R.id.lnlytAddressSharedWith);

//Registering broadcast receiver for GPS
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());

        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        address_id = getIntent().getStringExtra("addressId");



        if (getIntent().getBooleanExtra("isOwn",false)) {
            imgEditButton.setVisibility(View.VISIBLE);
            imgDelete.setVisibility(View.VISIBLE);
            lnlytSaveAddressButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnlytQRCodeButton.getLayoutParams();
            params.weight = 0.5f;
            lnlytQRCodeButton.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) lnlytShareAddress.getLayoutParams();
            params1.weight = 0.5f;
            lnlytShareAddress.setLayoutParams(params1);
        }
        else {
            lnlytAddressSharedWith.setVisibility(View.GONE);
            imgEditButton.setVisibility(View.GONE);
            imgDelete.setVisibility(View.GONE);
            lnlytSaveAddressButton.setVisibility(View.VISIBLE);
        }



        //Setting Back button code
        imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EditPrivateAddressOption.class);
                intent.putExtra("privateAddressData",privateAddressData);
                intent.putExtra("userId",user_Id);
                startActivity(intent);
            }
        });
        checkGPSAndPermission();


        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UtilityClass.isNetworkConnected(PrivateLocationDetailNewActivity.this)) {
                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(PrivateLocationDetailNewActivity.this);
                    builder.setMessage("Are you sure you want to delete ?");

                    builder.setCancelable(false);
                    builder
                            .setPositiveButton(
                                    "Yes",
                                    new DialogInterface
                                            .OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            HashMap<String, String> hashMap = new HashMap<>();

                                            hashMap.put(Constants.userId, user_Id);
                                            hashMap.put(Constants.addressId, address_id);
                                            Log.d(TAG, "getPrivateAddresses: " + user_Id + address_id);

                                            UtilityClass.showLoading(Loader,PrivateLocationDetailNewActivity.this);
                                            Call<Object> call = RetrofitInstance.getdata().create(Api.class).deletePrivateAddress(hashMap);
                                            call.enqueue(new Callback<Object>() {
                                                @Override
                                                public void onResponse(Call<Object> call, Response<Object> response) {
                                                    Log.d(TAG, "onResponse: " + response);
                                                    UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);

                                                    try {

                                                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                                        if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                                            AlertDialog.Builder builder
                                                                    = new AlertDialog
                                                                    .Builder(PrivateLocationDetailNewActivity.this);
                                                            builder.setMessage("Address deleted successfully.");

                                                            builder.setCancelable(false);
                                                            builder
                                                                    .setPositiveButton(
                                                                            "Ok",
                                                                            new DialogInterface
                                                                                    .OnClickListener() {

                                                                                @Override
                                                                                public void onClick(DialogInterface dialog,
                                                                                                    int which) {

                                                                                    Intent intent = new Intent();
                                                                                    setResult(Activity.RESULT_OK,intent);
                                                                                    finish();
                                                                                }
                                                                            });

                                                            AlertDialog alertDialog = builder.create();
                                                            alertDialog.show();



                                                        }


                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                                @Override
                                                public void onFailure(Call<Object> call, Throwable t) {
                                                    Log.d(TAG, "onFailure: " + t);
                                                    UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);

                                                }
                                            });
                                        }
                                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    new Message().showSnack(clytRootLayer,Constants.noInternetMessage);
                }


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrivateAddressDetail();
    }

    public void checkGPSAndPermission() {

        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(
                    "Your GPS seems to be disabled, please enable it from settings?")
                    .setCancelable(false).setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,
                                            final int id) {
                            startActivity(new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,
                                            final int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        300);
            }
            else {
                Loader.setVisibility(View.VISIBLE);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        Loader.setVisibility(View.GONE);
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            if (likelyPlaces.getCount() > 0) {
                                currentLatitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().latitude);
                                currentLongitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().longitude);
                            }
                            else {
                                new Message().showSnack(clytRootLayer, "Location can't be retrieved.");
                            }
                        }
                    }
                });
            }
        }
    }



    public void getPrivateAddressDetail() {
        if (UtilityClass.isNetworkConnected(PrivateLocationDetailNewActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put(Constants.userId,user_Id);
            hashMap.put(Constants.addressId,address_id);
            Log.d(TAG, "getOwnPrivateAddressDetail: "+user_Id+address_id);
            UtilityClass.showLoading(Loader,PrivateLocationDetailNewActivity.this);

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPrivateAddressDetail(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);;

                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                            privateAddressData = jsonObject.getString(Constants.RESULT_DATA);
                            Log.d(TAG, "tanunnnnnnnnnnn: " + response);

                            final JSONObject jsonObject1 = new JSONObject(privateAddressData);

                            address_id = jsonObject1.getString("addressId");
                            latitude = jsonObject1.getString("latitude");
                            longitude = jsonObject1.getString("longitude");

                            txtvLocationName.setText(jsonObject1.getString("shortName"));
                            txtvAddress.setText(jsonObject1.getString("address")+ " / " +jsonObject1.getString("plusCode"));
                            txtvLandmark.setText(jsonObject1.getString("landmark"));
                            String contactNumber = jsonObject1.getString("contactNumber");
                            JSONArray jsonArray = new JSONArray(contactNumber);
                            txtvPhoneNumber.setText(jsonArray.getJSONObject(0).getString("phoneNumber"));
                            String addressReferenceId = jsonObject1.getString("addressReferenceId");
                            txtvEmailId.setText(jsonObject1.getString("emailId"));

                            pictureURL = jsonObject1.getString("pictureURL");
                            address = jsonObject1.getString("address");
//                            Picasso.with(getApplicationContext()).load(Constants.IMG_URL.concat(pictureURL)).placeholder(R.drawable.profile_public).into(imgLocation);
                            Picasso.with(getApplicationContext())
                                    .load(Constants.IMG_URL.concat(pictureURL))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .placeholder(R.drawable.profile_public)
                                    .into(imgLocation, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                            imgLocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (pictureURL.equals("")) {
                                        new Message().showSnack(clytRootLayer,"No location picture available.");
                                    }
                                    else {
                                        Intent intent = new Intent(PrivateLocationDetailNewActivity.this,ImagePreviewActivity.class);
                                        intent.putExtra("image",Constants.IMG_URL.concat(pictureURL));
                                        startActivity(intent);
                                    }
                                }
                            });

                            if (jsonObject1.getInt("isSaved") > 0) {
                                imgHeart.setImageResource(R.drawable.hearticon);

                            }
                            else {

                                imgHeart.setImageResource(R.drawable.like);
                            }

                            //Setting pin on map
                            fgmtMap.getMapAsync(PrivateLocationDetailNewActivity.this);


                            //Setting click for call button
                           /* imgCallButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                        ActivityCompat.requestPermissions(PrivateLocationDetailNewActivity.this,
                                                new String[]{Manifest.permission.CALL_PHONE},
                                                100);
                                    }
                                    else {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + txtvPhoneNumber.getText()));
                                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        getApplicationContext().startActivity(callIntent);
                                    }
                                }
                            });*/

                            //Shared Address With Click

                            lnlytAddressSharedWith.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PrivateLocationDetailNewActivity.this, SharedListActivity.class);
                                    try {
                                        intent.putExtra("addressID",jsonObject1.getString("addressId"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.putExtra("isPublic","0");
                                    startActivity(intent);
                                }
                            });

                            //Setting the click of message button
                           /* imgMessageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("smsto:"+txtvPhoneNumber.getText());
                                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                    it.putExtra("sms_body", "");
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(it);
                                }
                            });*/

                            qrCodeURL = Constants.IMG_URL + jsonObject1.getString("qrCodeURL");


                            //Setting click on QR code
                            lnlytQRCodeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PrivateLocationDetailNewActivity.this, QrCodeActivity.class);

                                    try {
                                        intent.putExtra("address", jsonObject1.getString("address"));
                                        intent.putExtra("shortName", jsonObject1.getString("shortName"));
                                        intent.putExtra("qrCodeURL", qrCodeURL);
                                        intent.putExtra("plusCode", jsonObject1.getString("plusCode"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, "onClick: " + qrCodeURL);
                                    startActivity(intent);
                                }
                            });

                            //Setting click on share button
                            lnlytShareAddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    try {
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PRI-"+jsonObject1.getString("addressReferenceId"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(Intent.createChooser(intent, "Share"));
                                }
                            });


                            //Setting on click of direction button
                            imgDirectionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestCode = 300;
                                    final LocationManager manager = (LocationManager) getSystemService(
                                            Context.LOCATION_SERVICE);

                                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        checkGPSAndPermission();
                                    }
                                    else {
                                        if (ActivityCompat.checkSelfPermission(PrivateLocationDetailNewActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            if (currentLongitude == null || currentLatitude == null) {
                                                new Message().showSnack(clytRootLayer, "Fetching your current position.");
                                                UtilityClass.showLoading(Loader,PrivateLocationDetailNewActivity.this);
                                            }
                                            else  {
                                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + latitude + "," + longitude + ""));
                                                startActivity(intent);
                                            }

                                        }
                                        else {
                                            checkGPSAndPermission();
                                        }
                                    }
                                }
                            });


                            //Setting the click for save button
                            lnlytSaveAddressButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Bitmap bmap = ((BitmapDrawable)imgHeart.getDrawable()).getBitmap();
                                    Drawable myDrawable = getResources().getDrawable(R.drawable.hearticon);
                                    final Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                                    if(getIntent().getBooleanExtra("isFromShared",true) == false){
                                        String ListID = getIntent().getStringExtra("listID");
                                        if (bmap.sameAs(myLogo)) {
                                            imgHeart.setImageResource(R.drawable.like);
                                            saveUnsaveAddress(ListID,false);
                                        } else {
//                                        saveAddressToSavedList(ListID);
                                            imgHeart.setImageResource(R.drawable.hearticon);
                                            saveUnsaveAddress(ListID,true);
                                        }


                                    }else {

                                        if (bmap.sameAs(myLogo)) {
                                            new Message().showSnack(clytRootLayer, "You can unsave this address from profile saved address section.");
                                        } else {
                                            Intent intent = new Intent(PrivateLocationDetailNewActivity.this, SavedAddressListFragment.class);
                                            intent.putExtra("isSavingProcess", true);
                                            startActivityForResult(intent, 1);
                                        }
                                    }

                                }
                            });

                        }
                        Loader.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t);



                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer,Constants.noInternetMessage);
        }



//    }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                imgHeart.setImageResource(R.drawable.hearticon);
                saveUnsaveAddress(data.getStringExtra("code"),true);
//                saveAddressToSavedList(data.getStringExtra("code"));
            }
        }
    }


    public void saveUnsaveAddress(String listId, Boolean isSave) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.addressId, address_id);
        hashMap.put(Constants.isPublic, "0");
        hashMap.put(Constants.listId,listId );
        Call<Object> call;
        if(isSave == true){
            call = RetrofitInstance.getdata().create(Api.class).saveAddressToSavedList(hashMap);
        }else{
            call = RetrofitInstance.getdata().create(Api.class).unsaveAddressToSavedList(hashMap);
        }
        UtilityClass.showLoading(Loader,PrivateLocationDetailNewActivity.this);


        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, ":saveUnsaveAddress " + response);

                UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);
                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {
                        new Message().showSnack(clytRootLayer, "This account has been deactivated from this platform.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                        new Message().showSnack(clytRootLayer, "This account has been deleted from this platform.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                        new Message().showSnack(clytRootLayer, "Something went wrong. Please try after some time.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        new Message().showSnack(clytRootLayer, "No data found.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(clytRootLayer, "This account has been blocked.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(clytRootLayer, "All fields not sent.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(clytRootLayer, " Please check the request method.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);
            }
        });

    }


    //Broadcast receiver method for GPS
    /**
     * Following broadcast receiver is to listen the Location button toggle state in Android.
     */
    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // Make an action or refresh an already managed state.
                final LocationManager manager = (LocationManager) getSystemService(
                        Context.LOCATION_SERVICE);

                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(PrivateLocationDetailNewActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                300);
                    }
                }
            }
        }
    };


    //Permission request callback method
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                /*if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //imgCallButton.performClick();
                    } else {
                        new Message().showSnack(clytRootLayer, "The permission for calling to this app is denied.");
                    }
                }*/
            }
        } else if (requestCode == 200 || requestCode == 300) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(
                                Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        provider = locationManager.getBestProvider(criteria, false);
                        if (provider != null && !provider.equals("")) {
                            if (!provider.contains("gps")) {
                                final Intent poke = new Intent();
                                poke.setClassName("com.android.settings",
                                        "com.android.settings.widget.SettingsAppWidgetProvider");
                                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                                poke.setData(Uri.parse("3"));
                                sendBroadcast(poke);
                            }
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                return;
                            }
                            UtilityClass.showLoading(Loader,PrivateLocationDetailNewActivity.this);
                            Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                                    UtilityClass.hideLoading(Loader,PrivateLocationDetailNewActivity.this);
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                                        if (likelyPlaces.getCount() > 0) {
                                            currentLatitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().latitude);
                                            currentLongitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().longitude);
                                            if (requestCode == 300) {
                                                imgDirectionButton.performClick();
                                            }
                                        }
                                        else {
                                            new Message().showSnack(clytRootLayer, "Location can't be retrieved.");
                                        }
                                    }
                                }
                            });


                        } else {
                            new Message().showSnack(clytRootLayer, "No Provider Found.");
                        }
                    } else {
                        new Message().showSnack(clytRootLayer, "The permission for accessing the current location to this app is denied.");
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapObject = googleMap;
        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)),17f));

        mapObject.getUiSettings().setMapToolbarEnabled(false);

        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)));

        // Clears the previously touched position
        googleMap.clear();

        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions);

        fgmtMap.getView().setClickable(false);
    }
}
