package com.codeapex.simplrpostprod.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codeapex.simplrpost.Adapter.AdaterCategoryBussiness;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.ModelCategoryBusiness;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CategoryBussinessActivity extends AppCompatActivity implements ProfileScreenInterface {


    String user_Id, strCategoryId, currentLatitude, currentLongitude;
    ProgressBar Loader;
    List<ModelCategoryBusiness> models = new ArrayList<>();
    RecyclerView recyclerView;
    ConstraintLayout root_lay;
    int requestCode = 200;
    ImageView back;
    PlaceDetectionClient mPlaceDetectionClient;
    ProfileScreenInterface interface1;
    TextView txtvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_bussiness);
        txtvTitle = findViewById(R.id.txtvTitle);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        root_lay = findViewById(R.id.root_lay);
        Loader = findViewById(R.id.Loader);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        strCategoryId = getIntent().getStringExtra("categoryId_explore");
        txtvTitle.setText(getIntent().getStringExtra("categoryName"));
        checkGPSAndPermission();
        interface1 = this;

        back = findViewById(R.id.back_press);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });

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
                        requestCode);
            }
            else {
                UtilityClass.showLoading(Loader,CategoryBussinessActivity.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader, CategoryBussinessActivity.this);
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            if (likelyPlaces.getCount() > 0) {
                                currentLatitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().latitude);
                                currentLongitude = String.valueOf(likelyPlaces.get(0).getPlace().getLatLng().longitude);
                                getCategoryBusiness();
                            }
                            else {
                                new Message().showSnack(root_lay, "Location can't be retrieved.");
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
                getCategoryBusiness();
            }
            else {
                finish();
            }
        }
    }


    public void getCategoryBusiness() {
        if (UtilityClass.isNetworkConnected(CategoryBussinessActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.currentLatitude, currentLatitude);
            hashMap.put(Constants.userId, user_Id);
            hashMap.put(Constants.currentLongitude, currentLongitude);
            hashMap.put(Constants.categoryId, strCategoryId);
            hashMap.put(Constants.start, String.valueOf(models.size()));
            hashMap.put(Constants.count, "20");


            UtilityClass.showLoading(Loader,CategoryBussinessActivity.this);

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getCategoryBusiness(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.d(TAG, "nbewwww: " + response);
                    UtilityClass.hideLoading(Loader,CategoryBussinessActivity.this);

                    try {

                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        Log.d(TAG, "nbewwww: " + jsonObject);
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "No data found.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                            String result_data = jsonObject.getString(Constants.RESULT_DATA);

                            JSONArray jsonArray = new JSONArray(result_data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String addressId = jsonObject1.getString("addressId");

                                String pictureURL = jsonObject1.getString("pictureURL");
                                String shortName = jsonObject1.getString("shortName");

                                String plusCode = jsonObject1.getString("plusCode");

                                String categoryName = jsonObject1.getString("categoryName");


                                String addressReferenceId = jsonObject1.getString("addressReferenceId");

                                String description = jsonObject1.getString("description");

                                ModelCategoryBusiness modelCategoryBusiness = new ModelCategoryBusiness(addressId, pictureURL,shortName,plusCode,categoryName,addressReferenceId,description);
                                models.add(modelCategoryBusiness);
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            AdaterCategoryBussiness adaterCategoryBussiness = new AdaterCategoryBussiness(getApplicationContext(), models, interface1);
                            recyclerView.setAdapter(adaterCategoryBussiness);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    UtilityClass.hideLoading(Loader,CategoryBussinessActivity.this);

                }
            });
        }
        else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }



//    }
    }

    @Override
    public void openPrivateAddress(Intent objIntent) {

    }

    @Override
    public void openPublicAddress(Intent objIntent) {
        startActivity(objIntent);
    }

}