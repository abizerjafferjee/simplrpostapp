package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codeapex.simplrpostprod.Adapter.AdaptorAddresses;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.ResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
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

public class AddressesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SharedPreferences preferences;
    List<ModelResultPrivateAddress> private_address_model = new ArrayList<>();
    public static String strFrom,recieverId;
    ImageView back_press;
    ProgressBar Loader;
    ConstraintLayout consEmptyScreen,clytRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);
        consEmptyScreen = findViewById(R.id.consEmptyScreen);
        recyclerView = findViewById(R.id.recyecler);
        Loader = findViewById(R.id.Loader);
        back_press = findViewById(R.id.back_press);
        preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);

        get_private_address();
        strFrom = getIntent().getStringExtra("formwhere");
        recieverId = getIntent().getStringExtra("recieverId");
        clytRoot = findViewById(R.id.root);

        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void get_private_address() {
        try {
            if (UtilityClass.isNetworkConnected(AddressesActivity.this)) {
                Parser.callApi(AddressesActivity.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getPrivateAddress(preferences.getString(Constants.userId, "")), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        //Log.e("Private", "Address response::" + response);

                        if (private_address_model!=null){
                            private_address_model.clear();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result_code = jsonObject.getString("resultCode");
                            if (result_code.equals("1")) {
                                JSONArray resultData = jsonObject.optJSONArray("resultData");
                                for (int i = 0; i < resultData.length(); i++) {
                                    JSONObject object = resultData.optJSONObject(i);
                                    String addressId = object.optString("addressId");
                                    String userId = object.optString("userId");
                                    String profilePicURL = object.optString("profilePicURL");
                                    String userName = object.optString("userName");
                                    String address_tag = object.optString("address_tag");
                                    String latitude = object.optString("latitude");
                                    String longitude = object.optString("longitude");
                                    String plus_code = object.optString("plus_code");
                                    String unique_link = object.optString("unique_link");
                                    String country = object.optString("country");
                                    String city = object.optString("city");
                                    String street_name = object.optString("street_name");
                                    String building_name = object.optString("building_name");
                                    String entrance_name = object.optString("entrance_name");
                                    String direction_text = object.optString("direction_text");
                                    String street_image = object.optString("street_image");
                                    String building_image = object.optString("building_image");
                                    String entrance_image = object.optString("entrance_image");
                                    String qrCode_image = object.optString("qrCodeURL");
                                    String street_img_type = object.optString("street_img_type");
                                    String building_img_type = object.optString("building_img_type");
                                    String entrance_img_type = object.optString("entrance_img_type");

                                    private_address_model.add(new ModelResultPrivateAddress(addressId,userId,userName,profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image,qrCode_image,street_img_type,building_img_type,entrance_img_type));
                                }


                            } else if (result_code.equals("0")) {
                                new Message().showSnack(clytRoot, "" + jsonObject.optString("data"));
                            }

                            get_public_address();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new Message().showSnack(clytRoot, Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }

    public void get_public_address() {
        try {
            if (UtilityClass.isNetworkConnected(AddressesActivity.this)) {
                Parser.callApi(AddressesActivity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getPublicAddresses(preferences.getString(Constants.userId, "")), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        //Log.e("Public", "Address response::" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result_code = jsonObject.getString("resultCode");
                            if (result_code.equals("1")) {
                                JSONArray resultData = jsonObject.optJSONArray("resultData");
                                for (int i = 0; i < resultData.length(); i++) {
                                    JSONObject object = resultData.optJSONObject(i);
                                    String addressId = object.optString("addressId");
                                    String userId = object.optString("userId");
                                    String userName = object.optString("userName");
                                    String profilePicURL = object.optString("profilePicURL");
                                    String address_tag = object.optString("address_tag");
                                    String latitude = object.optString("latitude");
                                    String longitude = object.optString("longitude");
                                    String plus_code = object.optString("plus_code");
                                    String unique_link = object.optString("unique_link");
                                    String country = object.optString("country");
                                    String city = object.optString("city");
                                    String street_name = object.optString("street_name");
                                    String building_name = object.optString("building_name");
                                    String entrance_name = object.optString("entrance_name");
                                    String direction_text = object.optString("direction_text");
                                    String street_image = object.optString("street_image");
                                    String building_image = object.optString("building_image");
                                    String entrance_image = object.optString("entrance_image");
                                    String qrCode_image = object.optString("qrCodeURL");
                                    String street_img_type = object.optString("street_img_type");
                                    String building_img_type = object.optString("building_img_type");
                                    String entrance_img_type = object.optString("entrance_img_type");

                                    private_address_model.add(new ModelResultPrivateAddress(addressId,userId,userName,profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image,qrCode_image,street_img_type,building_img_type,entrance_img_type));
                                }

                            } else if (result_code.equals("0")) {
                                new Message().showSnack(clytRoot, "" + jsonObject.optString("data"));
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddressesActivity.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(AddressesActivity.this, private_address_model, "share my address", null);
                            recyclerView.setAdapter(custom_adaterFriends);

                            if (private_address_model.size() == 0) {
                                consEmptyScreen.setVisibility(View.VISIBLE);
                            } else {
                                consEmptyScreen.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new Message().showSnack(clytRoot, Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }

    //...................................................api.............................//
    /*public void getPrivateAddresses() {
        if (UtilityClass.isNetworkConnected(AddressesActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPrivateAddresses(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("Private_Response", new Gson().toJson(response.body()));
                    try {
                        models.clear();
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            consEmptyScreen.setVisibility(View.GONE);
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            JSONArray jsonArray = new JSONArray(result_data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String addressId = jsonObject1.getString("addressId");
                                String shortName = jsonObject1.getString("shortName");
                                String plusCode = jsonObject1.getString("plusCode");
                                String addressReferenceId = jsonObject1.getString("addressReferenceId");
                                String pictureURL = jsonObject1.getString("pictureURL");
                                ResultPrivateAddress resultPrivateAddress = new ResultPrivateAddress(addressId, shortName, plusCode, addressReferenceId, pictureURL,true,"","");
                                models.add(resultPrivateAddress);
                            }
                        }
                        getPublicAddresses();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                }
            });
        }
        else {
            new Message().showSnack(clytRoot, Constants.noInternetMessage);
        }

    }


    public void getPublicAddresses() {
        if (UtilityClass.isNetworkConnected(AddressesActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
            UtilityClass.showLoading(Loader,AddressesActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPublicAddresses(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("Public_Response", new Gson().toJson(response.body()));
                    UtilityClass.hideLoading(Loader,AddressesActivity.this);
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            JSONArray jsonArray = new JSONArray(result_data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String addressId = jsonObject1.getString("addressId");
                                String shortName = jsonObject1.getString("shortName");
                                String plusCode = jsonObject1.getString("plusCode");
                                String addressReferenceId = jsonObject1.getString("addressReferenceId");
                                String pictureURL = jsonObject1.getString("pictureURL");
                                String description = jsonObject1.getString("description");
                                String categoryName = jsonObject1.getString("categoryName");
                                ResultPrivateAddress resultPrivateAddress = new ResultPrivateAddress(addressId, shortName, plusCode, addressReferenceId, pictureURL,false,categoryName,description);
                                models.add(resultPrivateAddress);
                            }
                        }
                        *//*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddressesActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(AddressesActivity.this, models,strFrom,null);
                        recyclerView.setAdapter(custom_adaterFriends);*//*

                        if (models.size() == 0){
                            consEmptyScreen.setVisibility(View.VISIBLE);
                        }else {
                            consEmptyScreen.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    UtilityClass.hideLoading(Loader,AddressesActivity.this);

                }
            });
        }
        else {
            new Message().showSnack(clytRoot, Constants.noInternetMessage);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("addressId","");
        intent.putExtra("isPrivate",true);
        setResult(10,intent);
        finish();
    }
}
