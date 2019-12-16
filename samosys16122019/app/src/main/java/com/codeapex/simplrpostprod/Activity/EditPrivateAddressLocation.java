package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.codeapex.simplrpostprod.Adapter.GoogleAutoCompletePlaceAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddressRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPrivateAddressLocation extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageView imgBack, imgLocationCrossButton;
    CardView crdSlide1Next, crdSlide2Next, crdSlide2Previous;
    LinearLayout llytSlide1, llytSlide2;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;
    EditText edtAddress, edtLandmark;
    AutoCompleteTextView atxtvAddress;
    TextView txtvCurrentLocation;
    SupportMapFragment mapView;


    GoogleAutoCompletePlaceAdapter autoCompleteAdapter;
    String latitude,longitude;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mMap;
    PlaceDetectionClient mPlaceDetectionClient;
    PrivateAddressRequest privateAddressData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_private_address_location);

        imgBack = findViewById(R.id.back_press);
        clytRootLayer = findViewById(R.id.rootLayer);
        Loader = findViewById(R.id.Loader);
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        edtAddress = findViewById(R.id.edtAddress);
        edtLandmark = findViewById(R.id.edtLandmark);
        atxtvAddress = findViewById(R.id.address_txt);
        txtvCurrentLocation = findViewById(R.id.txt_current_location);
        imgLocationCrossButton = findViewById(R.id.locationCross);




        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();
//        .setCountry("IN")
        autoCompleteAdapter = new GoogleAutoCompletePlaceAdapter(this,filter);

        atxtvAddress.setAdapter(autoCompleteAdapter);
        atxtvAddress.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        txtvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPrivateAddressLocation.this);
                atxtvAddress.clearFocus();
                atxtvAddress.setText("");
                checkGPSAndPermission();
            }
        });
        mapView.getMapAsync(EditPrivateAddressLocation.this);

        imgLocationCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atxtvAddress.setText("");
            }
        });

        if (getIntent().hasExtra("privateAddressData")) {
            privateAddressData = (PrivateAddressRequest) getIntent().getSerializableExtra("privateAddressData");
            edtAddress.setText(privateAddressData.getAddress());
            edtLandmark.setText(privateAddressData.getLandmark());
            latitude = privateAddressData.getLatitude();
            longitude = privateAddressData.getLongitude();
            latLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        }


        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPrivateAddressLocation.this);
//                if (edtAddress.getText().toString().trim().equals("")) {
//                    new Message().showSnack(clytRootLayer, "Please enter address.");
//                }
//                else {
                    llytSlide1.setVisibility(View.GONE);
                    llytSlide2.setVisibility(View.VISIBLE);
//                }
            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPrivateAddressLocation.this);
                //Code to call the API to save the primary information
                try {
                    editPrivateAddressLocation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });

    }

    public void checkGPSAndPermission() {

        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditPrivateAddressLocation.this);
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

        }
        else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        200);
            }
            else {
                UtilityClass.showLoading(Loader,EditPrivateAddressLocation.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,EditPrivateAddressLocation.this);
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            if (likelyPlaces.getCount() > 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(likelyPlaces.get(0).getPlace().getLatLng(),17f));

                                latLng = likelyPlaces.get(0).getPlace().getLatLng();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UtilityClass.showLoading(Loader,EditPrivateAddressLocation.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,EditPrivateAddressLocation.this);
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            if (likelyPlaces.getCount() > 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(likelyPlaces.get(0).getPlace().getLatLng(),17f));

                                latLng = likelyPlaces.get(0).getPlace().getLatLng();
                            }
                            else {
                                new Message().showSnack(clytRootLayer, "Location can't be retrieved.");
                            }
                        }
                    }
                });
            }
            else {
                finish();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoogleAutoCompletePlaceAdapter.PlaceAutoComplete pac = autoCompleteAdapter.getItem(position);
        if (pac != null) {
            String placeId = pac.getPlaceId();
            UtilityClass.hideKeyboard(EditPrivateAddressLocation.this);
            autoCompleteAdapter.getLatLong(placeId, new PlaceListner() {
                @Override
                public void latLong(LatLng latLng) {
                    EditPrivateAddressLocation.this.latLng = latLng;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng = mMap.getCameraPosition().target;
            }
        });
    }

    //API related methods

    private void editPrivateAddressLocation() throws JSONException {

        if (UtilityClass.isNetworkConnected(EditPrivateAddressLocation.this)) {
            PrivateAddressRequest request = new PrivateAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();



            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,"").toString());
            request.setAddressId(privateAddressData.getAddressId().toString());
            request.setAddress(edtAddress.getText().toString().trim().toString());
            request.setLandmark(edtLandmark.getText().toString().trim().toString());
            request.setLatitude(String.valueOf(latLng.latitude).toString());
            request.setLongitude(String.valueOf(latLng.longitude).toString());



            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPrivateLocation(request);
            UtilityClass.showLoading(Loader,EditPrivateAddressLocation.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPrivateAddressLocation.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPrivateAddressLocation.this);
                                alertbox.setMessage("Your address updated successfully.");
                                alertbox.setTitle("Success");
                                alertbox.setCancelable(false);
                                alertbox.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {

                                                finish();
                                            }
                                        });
                                alertbox.show();

                            }
                            else if (result_code == 0) {
                                new Message().showSnack(clytRootLayer,"This account has been deactivated.");
                            }
                            else if (result_code == -1) {
                                new Message().showSnack(clytRootLayer,"This account has been deleted.");
                            }
                            else if (result_code == -2) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }
                            else if (result_code == -3) {
                                new Message().showSnack(clytRootLayer,"No data found.");
                            }
                            else if (result_code == -5) {
                                new Message().showSnack(clytRootLayer,"This account has been blocked.");
                            }
                            else if (result_code == -6) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }



                        }
                        catch (Exception e) {
                            UtilityClass.hideLoading(Loader,EditPrivateAddressLocation.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPrivateAddressLocation.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }




    }
}
