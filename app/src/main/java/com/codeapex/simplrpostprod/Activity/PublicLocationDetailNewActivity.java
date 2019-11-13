package com.codeapex.simplrpostprod.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Color;
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

import com.codeapex.simplrpostprod.Adapter.AdaterPublicAddImage;
import com.codeapex.simplrpostprod.Adapter.AdaterPublicAddServices;
import com.codeapex.simplrpostprod.Adapter.AdaterWorkingHours;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.AddPhoneData;
import com.codeapex.simplrpostprod.ModelClass.Image;
import com.codeapex.simplrpostprod.ModelClass.Service;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class PublicLocationDetailNewActivity extends AppCompatActivity implements OnMapReadyCallback {


    //Data Members
    int requestCode = 200;
    List<Service> serviceList;
    List<Image> imageList;
    JSONObject businessDetail;
    LocationManager locationManager;
    BottomSheetDialog dialog;
    String provider;
    List<AddPhoneData> workingHour;
    GoogleMap mapObject;
    ProgressBar Loader;
    int deliveryAvailable;
    Boolean isSavingProess;
    int isSaved;
    String address_id, user_Id, pictureURL, locationPictureURL, shortName, addressReferenceId, address, emailId, plusCode, website, facebook, description, addressId, twitter, linkedin, instagram, latitude, longitude, qrCodeURL;
    String currentLatitude, currentLongitude, publicAddressData;
    PlaceDetectionClient mPlaceDetectionClient;


    //All View Related Variables
    RecyclerView serviceProvidedRecycler, openTimeRecycle, photoImagesRecycler;
    ImageView imgTimingDropDown, imgBackButton, imgEditButton, imgCallButton, imgMessageButton, imgLogo, imgHeart, imgDirectionButton, imgLocationPictureButton, imgDelete;
    TextView txtvTitle, txtvAddress, txtvLandmark, txtvLocationName, txtvCategory, txtvCurrentOpenStatus, txtvAboutContent, txtvPhoneNumber, txtvEmailId, txtvServiceDescription, txtvDelivery, txtvServiceLable, txtvImageLable, seeBelowServiceText;
    SupportMapFragment fgmtMap;
    LinearLayout lnlytLocationPhotosButton, lnlytShareMyAddressButton, lnlytQRCodeButton, lnlytSaveAddressButton, lnlytShareAddressButton, lnlytOpenTimeLayout, lnlytLeaveAMessage, lnlytFacebook, lnlytLinkedin, lnlytInstagram, lnlytTwitter, lnlytWebsite, lnlytAddressSharedWith;
    CardView crdOptionsButton,crdAddressSharedWith,crdSharedWithMe;
    ConstraintLayout clytRootLayer;
    NestedScrollView nscrlMainContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_location_detail_new);
        nscrlMainContainer = findViewById(R.id.mainContainer);

        mapObject = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        nscrlMainContainer = findViewById(R.id.mainContainer);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                nscrlMainContainer.requestDisallowInterceptTouchEvent(true);
            }
        });


        clytRootLayer = findViewById(R.id.rootLayer);
        imgBackButton = findViewById(R.id.back_press);
        imgEditButton = findViewById(R.id.editButton);
        imgDelete = findViewById(R.id.imgDelete);
        txtvTitle = findViewById(R.id.titleText);
        fgmtMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        crdSharedWithMe = findViewById(R.id.crdSharedWithMe);
        crdAddressSharedWith = findViewById(R.id.crdAddressSharedWith);
        txtvAddress = findViewById(R.id.address);
        txtvLandmark = findViewById(R.id.landmark);
        lnlytLocationPhotosButton = findViewById(R.id.locationPhotosLayout);
        txtvLocationName = findViewById(R.id.locationName);
        txtvCategory = findViewById(R.id.category);
        lnlytOpenTimeLayout = findViewById(R.id.openTimeLayout);
        txtvAboutContent = findViewById(R.id.aboutDescription);
        lnlytLeaveAMessage = findViewById(R.id.leaveAMessage);
        crdOptionsButton = findViewById(R.id.socialMediaOptions);
        txtvPhoneNumber = findViewById(R.id.phoneNumber);
        txtvEmailId = findViewById(R.id.emailId);
        imgCallButton = findViewById(R.id.callButton);
        imgMessageButton = findViewById(R.id.messageButton);
        lnlytShareMyAddressButton = findViewById(R.id.shareMyAddress);
        lnlytQRCodeButton = findViewById(R.id.qrCode);
        lnlytSaveAddressButton = findViewById(R.id.saveAddress);
        lnlytShareAddressButton = findViewById(R.id.shareAddress);
        txtvServiceDescription = findViewById(R.id.serviceContent);
        txtvDelivery = findViewById(R.id.delivery);
        txtvServiceLable = findViewById(R.id.serviceLabel);
        txtvImageLable = findViewById(R.id.imageLable);
        Loader = findViewById(R.id.Loader);
        imgTimingDropDown = findViewById(R.id.dropDownImage);
        serviceProvidedRecycler = findViewById(R.id.serviceProvidedRecycler);
        photoImagesRecycler = findViewById(R.id.photoImagesRecycle);
        openTimeRecycle = findViewById(R.id.openTimeRecycle);
        imgLogo = findViewById(R.id.imgLogo);
        imgHeart = findViewById(R.id.imgHeart);
        imgDirectionButton = findViewById(R.id.direction);
        imgLocationPictureButton = findViewById(R.id.locationPictures);
        lnlytAddressSharedWith = findViewById(R.id.lnlytAddressSharedWith);
        seeBelowServiceText = findViewById(R.id.seeBelowServiceText);
        View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        dialog = new BottomSheetDialog(PublicLocationDetailNewActivity.this);
        dialog.setContentView(dialogView);
        txtvCurrentOpenStatus = findViewById(R.id.openStatus);

        lnlytFacebook = dialog.findViewById(R.id.lnlytFacebook);
        lnlytInstagram = dialog.findViewById(R.id.lnlytInstagram);
        lnlytLinkedin = dialog.findViewById(R.id.lnlytLinkedIn);
        lnlytTwitter = dialog.findViewById(R.id.lnlytTwitter);
        lnlytWebsite = dialog.findViewById(R.id.lnlytWebsite);




        serviceList = new ArrayList<>();
        imageList = new ArrayList<>();
        workingHour = new ArrayList<>();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());


        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        address_id = getIntent().getStringExtra("addressId");



        if (getIntent().hasExtra("isOwn")){
            if (getIntent().getBooleanExtra("isOwn",false) == true) {
                imgEditButton.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnlytQRCodeButton.getLayoutParams();
                params.weight = 0.5f;
                lnlytQRCodeButton.setLayoutParams(params);

                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) lnlytShareAddressButton.getLayoutParams();
                params1.weight = 0.5f;
                lnlytShareAddressButton.setLayoutParams(params1);

                lnlytShareMyAddressButton.setVisibility(View.GONE);
                lnlytSaveAddressButton.setVisibility(View.GONE);


            }
            else {
                lnlytAddressSharedWith.setVisibility(View.GONE);
                imgEditButton.setVisibility(View.GONE);
                imgDelete.setVisibility(View.GONE);
            }
        }
        else {
            imgEditButton.setVisibility(View.GONE);
            imgDelete.setVisibility(View.GONE);
        }


        addBusinessView();



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
                Intent intent = new Intent(getApplicationContext(),EditPublicAddressOptionActivity.class);
                intent.putExtra("publicAddressData",publicAddressData);
                intent.putExtra("userId",user_Id);
                startActivity(intent);
            }
        });


/// Balance sheet
        crdOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });





        lnlytOpenTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openTimeRecycle.getVisibility() == View.VISIBLE) {
                    openTimeRecycle.setVisibility(View.GONE);
                    imgTimingDropDown.setImageResource(R.drawable.drop_down_arrow_down);

                } else {
                    openTimeRecycle.setVisibility(View.VISIBLE);
                    imgTimingDropDown.setImageResource(R.drawable.drop_down_arrow_up);
                }
            }
        });

        //Registering broadcast receiver for GPS
        registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        //Code to check the permission regarding the location

        checkGPSAndPermission();




        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (UtilityClass.isNetworkConnected(PublicLocationDetailNewActivity.this)) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(PublicLocationDetailNewActivity.this);
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

                                        UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
                                        Call<Object> call = RetrofitInstance.getdata().create(Api.class).deletePublicAddress(hashMap);
                                        call.enqueue(new Callback<Object>() {
                                            @Override
                                            public void onResponse(Call<Object> call, Response<Object> response) {
                                                Log.d(TAG, "onResponse: " + response);
                                                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);

                                                try {

                                                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                                        AlertDialog.Builder builder
                                                                = new AlertDialog
                                                                .Builder(PublicLocationDetailNewActivity.this);
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
                                                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);

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


        if (getIntent().hasExtra("isFromNotification")) {

            if (getIntent().getStringExtra("isAddressPublic").equals("1")) {
                Intent intent = new Intent(PublicLocationDetailNewActivity.this, PublicLocationDetailNewActivity.class);
                intent.putExtra("addressId", getIntent().getStringExtra("sharedAddressId"));
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",true);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(PublicLocationDetailNewActivity.this, PrivateLocationDetailNewActivity.class);
                intent.putExtra("addressId", getIntent().getStringExtra("sharedAddressId"));
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",true);
                startActivity(intent);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getPublicAddressDetail();
    }

    //Function to check gps and location permission
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
                        requestCode);
            }
            else {
                UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
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

                        ActivityCompat.requestPermissions(PublicLocationDetailNewActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                requestCode);
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

                if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        imgCallButton.performClick();
                    } else {
                        new Message().showSnack(clytRootLayer, "The permission for calling to this app is denied.");
                    }
                }
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
                            UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
                            Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                                    UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
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



    //Method to call API

    public void addBusinessView() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.addressId, address_id);

        UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).addBusinessView(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
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
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);

            }
        });


    }

    public void getPublicAddressDetail() {



        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.addressId, address_id);
        UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
        nscrlMainContainer.setVisibility(View.GONE);
        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPublicAddressDetail(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
                nscrlMainContainer.setVisibility(View.VISIBLE);
                try {
                    serviceList.clear();
                    imageList.clear();
                    workingHour.clear();
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


                        publicAddressData = jsonObject.getString(Constants.RESULT_DATA);
                        final JSONObject jsonObject1 = new JSONObject(publicAddressData);
                        businessDetail = jsonObject;
                        addressId = jsonObject1.getString("addressId");
                        description = jsonObject1.getString("description");
                        addressReferenceId = jsonObject1.getString("addressReferenceId");
                        txtvCategory.setText(jsonObject1.getString("categoryName"));
                        txtvLandmark.setText(jsonObject1.getString("landmark"));
                        deliveryAvailable = Integer.parseInt(jsonObject1.getString("deliveryAvailable"));
                        if(deliveryAvailable == 1){
                            txtvDelivery.setText("Available");
                            txtvDelivery.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        }else {
                            txtvDelivery.setText("Not Available");
                            txtvDelivery.setTextColor(Color.RED);
                        }
                        txtvServiceDescription.setText(jsonObject1.getString("serviceDescription"));
                        shortName = jsonObject1.getString("shortName");
                        plusCode = jsonObject1.getString("plusCode");
                        emailId = jsonObject1.getString("emailId");
                        pictureURL = jsonObject1.getString("pictureURL");
                        locationPictureURL = jsonObject1.getString("locationPictureURL");
                        isSaved = jsonObject1.getInt("isSaved");
                        Log.d(TAG, "nafis: "+isSaved);
                        if (isSaved > 0) {
                            imgHeart.setImageResource(R.drawable.hearticon);

                        }
                        else {

                            imgHeart.setImageResource(R.drawable.like);
                        }



                        latitude = jsonObject1.getString("latitude");
                        longitude = jsonObject1.getString("longitude");
                        address = jsonObject1.getString("address");


                        qrCodeURL = Constants.IMG_URL + jsonObject1.getString("qrCodeURL");
                        Log.d(TAG, "onResponse: "+qrCodeURL);

                        String socialMedia = jsonObject1.getString("socialMedia");
                        JSONObject object = new JSONObject(socialMedia);
                        website = object.getString("website");
                        Log.d(TAG, "website: " + website);
                        facebook = object.getString("facebook");
                        Log.d(TAG, "facebook: " + facebook);
                        twitter = object.getString("twitter");

                        linkedin = object.getString("linkedin");
                        instagram = object.getString("instagram");

                        String contactNumber = jsonObject1.getString("contactNumber");
                        JSONArray jsonArray = new JSONArray(contactNumber);
                        if (jsonArray.length() > 0) {
                            txtvPhoneNumber.setText("N/A");
                        }
                        else {
                            txtvPhoneNumber.setText(jsonArray.getJSONObject(0).getString("phoneNumber"));
                        }




                        String services = jsonObject1.getString("services");
                        JSONArray arrServices = new JSONArray(services);
                        if (arrServices.length() > 0) {
                            for (int i = 0; i < arrServices.length(); i++) {
                                JSONObject jsonObject2 = arrServices.getJSONObject(i);
                                String serviceId = jsonObject2.getString("serviceId");
                                String serviceFileURL = jsonObject2.getString("serviceFileURL");
                                String fileExtention = jsonObject2.getString("fileExtention");
                                Log.d(TAG, "serviceFileURL: " + serviceFileURL);
                                Service service = new Service(serviceId, serviceFileURL, fileExtention);
                                serviceList.add(service);
                            }
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.HORIZONTAL, false);
                            serviceProvidedRecycler.setLayoutManager(gridLayoutManager);
                            serviceProvidedRecycler.setHasFixedSize(true);
                            AdaterPublicAddServices custom_adaterFriends1 = new AdaterPublicAddServices(getApplicationContext(), serviceList);
                            serviceProvidedRecycler.setAdapter(custom_adaterFriends1);
                        }
                        else  {
                            seeBelowServiceText.setVisibility(View.GONE);
                            serviceProvidedRecycler.setVisibility(View.GONE);
                        }
                        if (txtvServiceDescription.getText().toString().trim().equals("")) {
                            txtvServiceLable.setVisibility(View.GONE);
                        }
                        else {
                            txtvServiceLable.setVisibility(View.VISIBLE);
                        }


                        String images = jsonObject1.getString("images");
                        JSONArray jsonArra = new JSONArray(images);
                        if (jsonArra.length() > 0) {
                            for (int i = 0; i < jsonArra.length(); i++) {
                                JSONObject jsonObjec2 = jsonArra.getJSONObject(i);
                                String imageId = jsonObjec2.getString("imageId");
                                String imageURL = jsonObjec2.getString("imageURL");

                                Image service = new Image(imageId, imageURL);
                                imageList.add(service);

                            }
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, LinearLayoutManager.HORIZONTAL, false);
                            photoImagesRecycler.setLayoutManager(gridLayoutManager);
                            photoImagesRecycler.setHasFixedSize(true);

                            AdaterPublicAddImage custom_adaterFriends2 = new AdaterPublicAddImage(getApplicationContext(), imageList);
                            photoImagesRecycler.setAdapter(custom_adaterFriends2);
                        }
                        else {
                            txtvImageLable.setVisibility(View.GONE);
                            photoImagesRecycler.setVisibility(View.GONE);
                        }



                        String workingHours = jsonObject1.getString("workingHours");
                        JSONArray jsonArray1 = new JSONArray(workingHours);
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObjec2 = jsonArray1.getJSONObject(i);
                            int dayId = jsonObjec2.getInt("dayId");
                            int isOpen = jsonObjec2.getInt("isOpen");
                            String openTime = jsonObjec2.getString("openTime");
                            String closeTime = jsonObjec2.getString("closeTime");
                            String dayName = jsonObjec2.getString("dayName");

                            AddPhoneData service = new AddPhoneData(dayName,dayId,openTime,closeTime,isOpen);
                            workingHour.add(service);
                        }
                        LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getApplicationContext());
                        openTimeRecycle.setLayoutManager(linearLayoutManagerr);
                        openTimeRecycle.setHasFixedSize(true);

                        AdaterWorkingHours custom_adaterFriend3 = new AdaterWorkingHours(getApplicationContext(), workingHour);
                        openTimeRecycle.setAdapter(custom_adaterFriend3);

                        //Code to set the current open/close status UI
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

                        Date d = new Date();
                        String dayOfTheWeek = sdf.format(d);
                        for (int i = 0; i < workingHour.size(); i++) {
                            AddPhoneData data = workingHour.get(i);
                            if (data.getDayName().toLowerCase().equals(dayOfTheWeek.toLowerCase())) {
                                SimpleDateFormat sdf1 = new SimpleDateFormat("HH a");
                                sdf1.setTimeZone(TimeZone.getDefault());
                                String currentDateandTime = sdf1.format(new Date());
                                int openTime = (data.getOpenTime().toLowerCase().equals("12 am"))?0:(Integer.parseInt(data.getOpenTime().split(" ")[0]) % 12) +  ((data.getOpenTime().split(" ")[1].toLowerCase().equals("am"))?0:12);
                                int closeTime = (data.getCloseTime().toLowerCase().equals("12 am"))?0:(Integer.parseInt(data.getCloseTime().split(" ")[0]) % 12) +  ((data.getCloseTime().split(" ")[1].toLowerCase().equals("am"))?0:12);
                                int currentTime = (currentDateandTime.toLowerCase().equals("12 am"))?0:(Integer.parseInt(currentDateandTime.split(" ")[0]) % 12) +  ((currentDateandTime.split(" ")[1].toLowerCase().equals("am"))?0:12);
                                Log.d(TAG, currentDateandTime);
                                if (data.getIsOpen() == 0) {
                                    txtvCurrentOpenStatus.setText("Not Open Today");

                                }
                                else {
                                    if (openTime == closeTime) {
                                        txtvCurrentOpenStatus.setText("Open 24 hour");
                                    }
                                    else if (openTime < closeTime) {
                                        if (currentTime < openTime) {
                                            txtvCurrentOpenStatus.setText("Close now. Will open at "+data.getOpenTime());
                                        }
                                        else if (currentTime >= openTime && currentTime <= closeTime) {
                                            txtvCurrentOpenStatus.setText("Open now. Will close at "+data.getCloseTime());
                                        }
                                        else {
                                            txtvCurrentOpenStatus.setText("Close for today");
                                        }
                                    }
                                    else if (openTime > closeTime) {
                                        if (currentTime < openTime && currentTime < closeTime) {
                                            txtvCurrentOpenStatus.setText("Open now. Will close at "+data.getCloseTime());
                                        }
                                        else if (currentTime > openTime) {
                                            txtvCurrentOpenStatus.setText("Open now. Will close at "+data.getCloseTime());
                                        }
                                        else {
                                            txtvCurrentOpenStatus.setText("Closed now");
                                        }
                                    }

//                                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH z");
//                                    String currentDateandTime = sdf1.format(new Date());
//                                    Log.d(TAG, currentDateandTime);
                                }

                            }
                        }






//                        Picasso.with(getApplicationContext()).load(Constants.IMG_URL.concat(pictureURL)).placeholder(R.drawable.profile_public).into(imgLogo);
                        Picasso.with(getApplicationContext())
                                .load(Constants.IMG_URL.concat(pictureURL))
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.profile_public)
                                .into(imgLogo, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                        imgLogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pictureURL.equals("")) {
                                    new Message().showSnack(clytRootLayer,"No logo image is available.");
                                }
                                else {
                                    Intent intent = new Intent(PublicLocationDetailNewActivity.this,ImagePreviewActivity.class);
                                    intent.putExtra("image",Constants.IMG_URL.concat(pictureURL));
                                    startActivity(intent);
                                }

                            }
                        });

                        txtvLocationName.setText(shortName);
//                        txtvLandmark.setText(plusCode);
                        if (address.equals("")) {
                            txtvAddress.setText(plusCode);
                        }
                        else {
                            txtvAddress.setText(address+ " / " +plusCode);
                        }

                        txtvEmailId.setText(emailId);
                        txtvAboutContent.setText(description.substring(0,1).toUpperCase() + description.substring(1));




                        //-----------------------------------------------------------
                        //All click listner code

                        //Shared Address With Click

                        crdAddressSharedWith.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PublicLocationDetailNewActivity.this, SharedListActivity.class);
                                intent.putExtra("addressID",addressId);
                                intent.putExtra("isPublic","1");
                                startActivity(intent);
                            }
                        });

                        crdSharedWithMe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PublicLocationDetailNewActivity.this, SharedWithMeActivity.class);
                                intent.putExtra("isShared",true);
                                intent.putExtra("shareType","business");
                                intent.putExtra("addressId",getIntent().getStringExtra("addressId"));
                                startActivity(intent);
                            }
                        });

                        //Setting the visibility of ui of socila media pop up

                        if ((facebook == "" || facebook == null)) {
                            lnlytFacebook.setVisibility(View.GONE);
                        } else if ((instagram == "" || instagram == null)) {
                            lnlytInstagram.setVisibility(View.GONE);
                        } else if ((linkedin == "" || linkedin == null)) {
                            lnlytLinkedin.setVisibility(View.GONE);
                        } else if ((twitter == "" || twitter == null)) {
                            lnlytTwitter.setVisibility(View.GONE);
                        } else if ((website == "" || website == null)) {
                            lnlytWebsite.setVisibility(View.GONE);
                        }


                        //Setting on click for social media
                        lnlytFacebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ((facebook.trim().compareTo("") == 0 || facebook == null)) {
                                    new Message().showSnack(clytRootLayer, "No link available.");
                                    dialog.dismiss();
                                }
                                else {

                                    if (!facebook.startsWith("http://",0) && !facebook.startsWith("https://",0)) {
                                        facebook = "https://" + facebook;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
                                    startActivity(browserIntent);
                                }

                            }
                        });

                        lnlytInstagram.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ((instagram.trim().compareTo("") == 0 || instagram == null)) {
                                    new Message().showSnack(clytRootLayer, "No links available.");
                                    dialog.dismiss();
                                }
                                else {
                                    if (!instagram.startsWith("http://") && !instagram.startsWith("https://")) {
                                        instagram = "https://" + instagram;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram));
                                    startActivity(browserIntent);
                                }

                            }
                        });

                        lnlytLinkedin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ((linkedin.trim().compareTo("") == 0 || linkedin == null)) {
                                    new Message().showSnack(clytRootLayer, "No links available.");
                                    dialog.dismiss();
                                }
                                else {
                                    if (!linkedin.startsWith("http://") && !linkedin.startsWith("https://")) {
                                        linkedin = "https://" + linkedin;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedin));
                                    startActivity(browserIntent);
                                }

                            }
                        });

                        lnlytTwitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ((twitter.trim().compareTo("") == 0 || twitter == null)) {
                                    new Message().showSnack(clytRootLayer, "No links available.");
                                    dialog.dismiss();
                                }
                                else {
                                    if (!twitter.startsWith("http://") && !twitter.startsWith("https://")) {
                                        twitter = "https://" + twitter;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                                    startActivity(browserIntent);
                                }

                            }
                        });

                        lnlytWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ((website.trim().compareTo("") == 0 || website == null)) {
                                    new Message().showSnack(clytRootLayer, "No links available.");
                                    dialog.dismiss();
                                }
                                else {
                                    if (!website.startsWith("http://") && !website.startsWith("https://")) {
                                        website = "http://" + website;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                                    startActivity(browserIntent);
                                }

                            }
                        });

                        //Setting click for call button
                        imgCallButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(PublicLocationDetailNewActivity.this,
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
                        });

                        //Setting the click of message button
                        imgMessageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("smsto:"+txtvPhoneNumber.getText());
                                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                it.putExtra("sms_body", "");
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(it);
                            }
                        });

                        //Setting pin on map
                        fgmtMap.getMapAsync(PublicLocationDetailNewActivity.this);

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
                                        Intent intent = new Intent(PublicLocationDetailNewActivity.this, SavedAddressListActivity.class);
                                        intent.putExtra("isSavingProcess", true);
                                        startActivityForResult(intent, 1);
                                    }
                                }

                            }
                        });

                        //Setting the click listner for leave a message
                        lnlytLeaveAMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri data = Uri.parse("mailto:"+txtvEmailId.getText());
                                intent.setData(data);
                                startActivity(intent);
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
                                    if (ActivityCompat.checkSelfPermission(PublicLocationDetailNewActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        if (currentLongitude == null || currentLatitude == null) {
                                            new Message().showSnack(clytRootLayer, "Fetching your current position.");
                                            UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
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


                        //Setting on click for location picture
                        imgLocationPictureButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (locationPictureURL.equals("")) {
                                    new Message().showSnack(clytRootLayer,"No location picture available for this address.");
                                }
                                else {
                                    Intent intent = new Intent(PublicLocationDetailNewActivity.this,ImagePreviewActivity.class);
                                    intent.putExtra("image",Constants.IMG_URL.concat(locationPictureURL));
                                    startActivity(intent);
                                }

                            }
                        });

                        //Setting on click for options icon




                        //Setting click on share my address button
                        lnlytShareMyAddressButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PublicLocationDetailNewActivity.this, AddressesActivity.class);
                                intent.putExtra("formwhere", "shareAdd");
                                startActivityForResult(intent, 10);
                            }
                        });

                        //Setting click on share button
                        lnlytShareAddressButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                try {
                                    intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PUB-"+jsonObject1.getString("addressReferenceId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(Intent.createChooser(intent, "Share"));
                            }
                        });


                        //Setting click on QR code
                        lnlytQRCodeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PublicLocationDetailNewActivity.this, QrCodeActivity.class);
                                intent.putExtra("address", address);
                                intent.putExtra("shortName", shortName);
                                intent.putExtra("qrCodeURL", qrCodeURL);
                                intent.putExtra("plusCode", plusCode);
                                Log.d(TAG, "onClick: " + qrCodeURL);
                                startActivity(intent);
                            }
                        });










                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);

            }
        });




    }


    public void saveUnsaveAddress(String listId, final Boolean isSave) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.addressId, address_id);
        hashMap.put(Constants.isPublic, "1");
        hashMap.put(Constants.listId,listId );
        Call<Object> call;
        if(isSave == true){
            call = RetrofitInstance.getdata().create(Api.class).saveAddressToSavedList(hashMap);
        }else{
            call = RetrofitInstance.getdata().create(Api.class).unsaveAddressToSavedList(hashMap);
        }
        UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);


        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, ":saveUnsaveAddress " + response);

                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
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
                    else if (result_code.equals("-11.0")) {
                        new Message().showSnack(clytRootLayer, "This address is already saved.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
                        if (isSave == true)
                        imgHeart.setImageResource(R.drawable.hearticon);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
            }
        });

    }




    public void shareAddressWithBusiness(String addressID,String isPublic) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.receiverId, address_id);
        hashMap.put(Constants.addressId, addressID);
        hashMap.put(Constants.isAddressPublic,isPublic );
        Log.d(TAG, "saveAddressToSavedList: " + user_Id +"  "+ address_id+"  "+addressID+"  "+isPublic);
        UtilityClass.showLoading(Loader,PublicLocationDetailNewActivity.this);
        Call<Object> call = RetrofitInstance.getdata().create(Api.class).shareAddressWithBusiness(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("share_Response", new Gson().toJson(response.body()));
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
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


                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                        new Message().showSnack(clytRootLayer, "List does not exist.");


                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_EIGHT)) {
                        new Message().showSnack(clytRootLayer, "Address does not exist.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(clytRootLayer, "This account has been blocked.");


                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(clytRootLayer, "All fields not sent.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(clytRootLayer, " Please check the request method.");
                    }

                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                        new UtilityClass().simpleAlertBox(PublicLocationDetailNewActivity.this,"Your address shared successfully.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(Loader,PublicLocationDetailNewActivity.this);
            }
        });


    }

    //...............................OnActivityResult...................//

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            imgHeart.setImageResource(R.drawable.hearticon);
            if(resultCode == RESULT_OK) {

                saveUnsaveAddress(data.getStringExtra("code"),true);
//                saveAddressToSavedList(data.getStringExtra("code"));
            }
        }
        else if(requestCode == 10){
            try {
                String  strAddressID = data.getStringExtra("addressId");
                boolean  isPrivate = data.getBooleanExtra("isPrivate",false);
                Log.e("backIntent",strAddressID+"    "+isPrivate);
                String strPublicorPrivate;
                if(isPrivate==true){
                    strPublicorPrivate = "0";
                }else {
                    strPublicorPrivate = "1";
                }


                if(strAddressID.isEmpty()){

                }else {
                    shareAddressWithBusiness(strAddressID,strPublicorPrivate);
                }

            }catch (Exception e){

            }

        }
        else if (requestCode == 1098 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGpsSwitchStateReceiver);
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





//    override fun onMapReady(googleMap: GoogleMap) {
//
//
//        mMap = googleMap
//        mMap?.setOnCameraIdleListener {
//            if (latLng != null && !isFirstSet) {
//                isFirstSet = true
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
//            }
//
//            latLng = mMap?.cameraPosition?.target
//            Log.d(strTAG, "latest lat ${latLng?.latitude} long ${latLng?.longitude}")
//        }
//    }
}
