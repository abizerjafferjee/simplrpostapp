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
import com.codeapex.simplrpostprod.ModelClass.ResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
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
    List<ResultPrivateAddress> models;
    String strFrom;
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
        models = new ArrayList<>();
        getPrivateAddresses();
        strFrom = getIntent().getStringExtra("formwhere");
        clytRoot = findViewById(R.id.root);

        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("addressId","");
                intent.putExtra("isPrivate",true);
                setResult(10,intent);
                finish();
            }
        });
    }


    //...................................................api.............................//
    public void getPrivateAddresses() {
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
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddressesActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(AddressesActivity.this, models,strFrom,null);
                        recyclerView.setAdapter(custom_adaterFriends);

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



//    }
    }

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
