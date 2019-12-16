package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codeapex.simplrpostprod.Adapter.AdaterSharedAndSavedAddresses;

import com.codeapex.simplrpostprod.Adapter.AdatperSharedWithMe;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.PublicAddress;
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


public class SharedWithMeActivity extends AppCompatActivity implements SaveUnsaveAddress {
    RecyclerView recyclerView;
    RecyclerView recyclervieww;
    CardView cardView;
    RelativeLayout relativeLayout;
    ImageView imageView;
    List<PublicAddress> publiclist;
    List<PrivateAddress> privatelist;
    TextView textone, texttwo;
    String listId,listname,user_Id;
    ScrollView scrollViewone,scrollViewtwo;
    SaveUnsaveAddress saveInterface;
    ProgressBar loader;
    Call<Object> call;
    ConstraintLayout root_lay;
    TextView txtTitle;
    boolean checkList = false;
    TextView txtError;
    String strErrorMsg;

    ConstraintLayout consEmptyScreen;
    ImageView imdError;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.friends_recycler);

                txtError =findViewById(R.id.txtError);
                imdError =findViewById(R.id.imdError);
                consEmptyScreen =findViewById(R.id.consEmptyScreen);
                recyclerView =findViewById(R.id.recyclerview);
                loader =findViewById(R.id.Loader);
                root_lay =findViewById(R.id.root_lay);
                recyclervieww = findViewById(R.id.recyclervieww);
                scrollViewone =findViewById(R.id.publicdefualt);
                scrollViewtwo = findViewById(R.id.privatedeault);
                cardView = findViewById(R.id.cvFinalImage);
                relativeLayout = findViewById(R.id.toolbar_lay);

                textone = findViewById(R.id.txtCurrentPost);
                texttwo = findViewById(R.id.txtHistoryPost);
                txtTitle = findViewById(R.id.txtTitle);
                saveInterface = this;

                listId = getIntent().getStringExtra("listIddd");
                listname = getIntent().getStringExtra("listname");

                SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                user_Id = preferences.getString(Constants.userId, "");
                Log.d(TAG, "onCreate: "+listId+user_Id);
                publiclist = new ArrayList<>();
                privatelist = new ArrayList<>();

        textone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consEmptyScreen.setVisibility(View.GONE);
                changeTextApearanceone(textone,texttwo);
                checkList = true;
                recyclerView.setVisibility(View.GONE);
                recyclervieww.setVisibility(View.VISIBLE);
                if (publiclist.isEmpty()){
                    consEmptyScreen.setVisibility(View.VISIBLE);
                    imdError.setImageResource(R.drawable.publicdefault);
                    txtError.setText(strErrorMsg+"public location found");
                }else {
                    consEmptyScreen.setVisibility(View.GONE);
                }
            }
        });

        texttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consEmptyScreen.setVisibility(View.GONE);
                changeTextApearance(texttwo,textone);
                recyclerView.setVisibility(View.VISIBLE);
                recyclervieww.setVisibility(View.GONE);
                if (privatelist.isEmpty()){
                    consEmptyScreen.setVisibility(View.VISIBLE);
                    imdError.setImageResource(R.drawable.privatedefault);
                    txtError.setText(strErrorMsg+"private location found");
                }else {
                    consEmptyScreen.setVisibility(View.GONE);
                }
//
                checkList = false;
            }
        });


            imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getBooleanExtra("isShared",true)==true){
            if (getIntent().getStringExtra("shareType").equals("user")){
                txtTitle.setText("Shared with me");
                strErrorMsg = "No shared ";
                getSharedWithUser();
            }else {
                txtTitle.setText("Shared with business");
                strErrorMsg = "No shared ";
                getSharedWithBusiness();
            }
        }else {
            txtTitle.setText(listname);
            strErrorMsg = "No saved ";
            getSavedListAddresses();
        }

        if (getIntent().hasExtra("addressId") && getIntent().hasExtra("isAddressPublic")) {
            if (getIntent().getStringExtra("isAddressPublic").equals("1")) {
                Intent intent = new Intent(SharedWithMeActivity.this, PublicLocationDetailNewActivity.class);
                intent.putExtra("addressId", getIntent().getStringExtra("addressId"));
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",true);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(SharedWithMeActivity.this, PrivateLocationDetailNewActivity.class);
                intent.putExtra("addressId", getIntent().getStringExtra("addressId"));
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",true);
                startActivity(intent);
            }
        }

    }

    public void changeTextApearance(TextView txtChange,TextView txtChange2nd){
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.white);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }


    public void changeTextApearanceone(TextView txtChange,TextView txtChange2nd){
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.
                whitetwo);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }


//=======================================GET SAVED ADDRESS API============================================//
    public void getSavedListAddresses() {
                if (UtilityClass.isNetworkConnected(SharedWithMeActivity.this)) {
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(Constants.userId, user_Id);
                    hashMap.put(Constants.listId, listId);
                    Log.d(TAG, "getSavedListAddresses: "+user_Id+listId);

                    UtilityClass.showLoading(loader, SharedWithMeActivity.this);

                    Call<Object> call = RetrofitInstance.getdata().create(Api.class).getSavedListAddresses(hashMap);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                if (result_code.equals(Constants.ZERO)) {


                                    new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                    new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                    new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");


                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                    new Message().showSnack(root_lay, "List does not exist.");

                                } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {

                                    consEmptyScreen.setVisibility(View.VISIBLE);
                                    imdError.setImageResource(R.drawable.publicdefault);
                                    txtError.setText("No saved public location found.");

                                    new Message().showSnack(root_lay, "No data found.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                    new Message().showSnack(root_lay, "This account has been blocked.");


                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                    new Message().showSnack(root_lay, "All fields not sent.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                    new Message().showSnack(root_lay, " Please check the request method.");
                                }

                                else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                    consEmptyScreen.setVisibility(View.GONE);
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                    Log.d(TAG, "tanunnnnnnnnnnn: " + response);

                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    String publicAddresses = jsonObject1.getString("publicAddresses");
                                    JSONArray jsonArray = new JSONArray(publicAddresses);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                        String addressId = jsonObject2.getString("addressId");
                                        String pictureURL = jsonObject2.getString("pictureURL");
                                        String shortName = jsonObject2.getString("shortName");
                                        String plusCode = jsonObject2.getString("plusCode");
                                        String categoryName = jsonObject2.getString("categoryName");
                                        String addressReferenceId = jsonObject2.getString("addressReferenceId");
                                        String description = jsonObject2.getString("description");

                                        PublicAddress publicAddress = new PublicAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId,description);
                                        publiclist.add(publicAddress);
                                    }
                                    LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getApplicationContext());
                                    recyclervieww.setLayoutManager(linearLayoutManagerr);
                                    recyclervieww.setHasFixedSize(true);

                                    AdatperSharedWithMe custom_adaterFriendss = new AdatperSharedWithMe(getApplicationContext(),publiclist,saveInterface,listId,false);
                                    recyclervieww.setAdapter(custom_adaterFriendss);

                                    String privateAddresses = jsonObject1.getString("privateAddresses");
                                    JSONArray jsonArrayy = new JSONArray(privateAddresses);

                                    for (int i = 0; i < jsonArrayy.length(); i++) {
                                        JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                        String addressId = jsonObject2.getString("addressId");
                                        String pictureURL = jsonObject2.getString("pictureURL");
                                        String shortName = jsonObject2.getString("shortName");
                                        String plusCode = jsonObject2.getString("plusCode");
                                        String categoryName = jsonObject2.getString("categoryName");
                                        String addressReferenceId = jsonObject2.getString("addressReferenceId");

                                        PrivateAddress privateAddress = new PrivateAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId);
                                        privatelist.add(privateAddress);


                                    }

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setHasFixedSize(true);

                                    AdaterSharedAndSavedAddresses _adaterFriends = new AdaterSharedAndSavedAddresses(getApplicationContext(),privatelist,saveInterface,listId,false);
                                    recyclerView.setAdapter(_adaterFriends);


                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t);
                            UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                        }

                    });
                }
                else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }

    }




//==============================================GET SHARED WITH USER API=========================================//

    public void getSharedWithUser() {

        if (UtilityClass.isNetworkConnected(SharedWithMeActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();

            hashMap.put(Constants.userId, user_Id);
            hashMap.put(Constants.listId, listId);
            Log.d(TAG, "getSavedListAddres2233: "+user_Id);

            UtilityClass.showLoading(loader, SharedWithMeActivity.this);

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getUserSharedAddresses(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                        }
                        else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                            new Message().showSnack(root_lay, "List does not exist.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            consEmptyScreen.setVisibility(View.VISIBLE);
                            imdError.setImageResource(R.drawable.publicdefault);
                            txtError.setText("No shared public location found.");
                            new Message().showSnack(root_lay, "No data found.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        }

                        else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            consEmptyScreen.setVisibility(View.GONE);
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            Log.d(TAG, "tanunnnnnnnnnnn: " + response);

                            JSONObject jsonObject1 = new JSONObject(result_data);
                            if (jsonObject1.has("publicAddresses")) {
                                String publicAddresses = jsonObject1.getString("publicAddresses");
                                JSONArray jsonArray = new JSONArray(publicAddresses);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String addressId = jsonObject2.getString("addressId");
                                    String pictureURL = jsonObject2.getString("pictureURL");
                                    String shortName = jsonObject2.getString("shortName");
                                    String plusCode = jsonObject2.getString("plusCode");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String addressReferenceId = jsonObject2.getString("addressReferenceId");
                                    String description = jsonObject2.getString("description");

                                    PublicAddress publicAddress = new PublicAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId,description);
                                    publiclist.add(publicAddress);
                                    LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getApplicationContext());
                                    recyclervieww.setLayoutManager(linearLayoutManagerr);
                                    recyclervieww.setHasFixedSize(true);

                                    AdatperSharedWithMe custom_adaterFriendss = new AdatperSharedWithMe(getApplicationContext(),publiclist,saveInterface,listId,true);
                                    recyclervieww.setAdapter(custom_adaterFriendss);

                                    Log.e("publicData",addressId+plusCode);
                                }
                            }

                            if (jsonObject1.has("privateAddresses")) {
                                String privateAddresses = jsonObject1.getString("privateAddresses");
                                JSONArray jsonArrayy = new JSONArray(privateAddresses);
                                for (int i = 0; i < jsonArrayy.length(); i++) {
                                    JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                    String addressId = jsonObject2.getString("addressId");
                                    String pictureURL = jsonObject2.getString("pictureURL");
                                    String shortName = jsonObject2.getString("shortName");
                                    String plusCode = jsonObject2.getString("plusCode");
                                    String categoryName = "";
                                    if(jsonObject2.has("categoryName")){
                                        categoryName = jsonObject2.getString("categoryName");
                                    }
                                    String addressReferenceId = jsonObject2.getString("addressReferenceId");

                                    PrivateAddress privateAddress = new PrivateAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId);
                                    privatelist.add(privateAddress);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setHasFixedSize(true);

                                    AdaterSharedAndSavedAddresses _adaterFriends = new AdaterSharedAndSavedAddresses(getApplicationContext(),privatelist,saveInterface,listId,true);
                                    recyclerView.setAdapter(_adaterFriends);
                                    Log.e("privateData",addressId+plusCode);
                                }
                            }




                            if (publiclist.isEmpty() && privatelist.isEmpty()){
                                consEmptyScreen.setVisibility(View.VISIBLE);
                                imdError.setImageResource(R.drawable.publicdefault);
                                txtError.setText("No shared public location found.");
                            }else {
                                consEmptyScreen.setVisibility(View.GONE);
                            }

                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    UtilityClass.hideLoading(loader, SharedWithMeActivity.this);

                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }






//    }
    }


//=======================================GET SHARED WITH BUSINESS API==============================================//

    public void getSharedWithBusiness() {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.addressId, getIntent().getStringExtra("addressId"));
        Log.d(TAG, "getSavedList2323: "+user_Id+"    "+ getIntent().getStringExtra("addressId"));

        UtilityClass.showLoading(loader, SharedWithMeActivity.this);

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getBusinessSharedAddresses(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                Log.e("Business_Response", new Gson().toJson(response.body()));
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {
                        new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                        new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                        new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");


                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                        new Message().showSnack(root_lay, "List does not exist.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        consEmptyScreen.setVisibility(View.VISIBLE);
                        imdError.setImageResource(R.drawable.publicdefault);
                        txtError.setText("No shared public location found.");
                        new Message().showSnack(root_lay, "No data found.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(root_lay, "This account has been blocked.");


                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(root_lay, "All fields not sent.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(root_lay, " Please check the request method.");
                    }

                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                        consEmptyScreen.setVisibility(View.GONE);

                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
                        Log.d(TAG, "tanunnnnnnnnnnn: " + response);

                        JSONObject jsonObject1 = new JSONObject(result_data);
                        if(jsonObject1.has("publicAddresses")){
                            String publicAddresses = jsonObject1.getString("publicAddresses");
                            JSONArray jsonArray = new JSONArray(publicAddresses);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String addressId = jsonObject2.getString("addressId");
                                String pictureURL = jsonObject2.getString("pictureURL");
                                String shortName = jsonObject2.getString("shortName");
                                String plusCode = jsonObject2.getString("plusCode");
                                String categoryName = jsonObject2.getString("categoryName");
                                String addressReferenceId = jsonObject2.getString("addressReferenceId");
                                String description = jsonObject2.getString("description");

                                PublicAddress publicAddress = new PublicAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId,description);
                                publiclist.add(publicAddress);
                                LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getApplicationContext());
                                recyclervieww.setLayoutManager(linearLayoutManagerr);
                                recyclervieww.setHasFixedSize(true);

                                AdatperSharedWithMe custom_adaterFriendss = new AdatperSharedWithMe(getApplicationContext(),publiclist,saveInterface,listId,true);
                                recyclervieww.setAdapter(custom_adaterFriendss);
                                Log.e("publicData",addressId+plusCode);
                            }
                        }


                        if(jsonObject1.has("privateAddresses")){
                            String privateAddresses = jsonObject1.getString("privateAddresses");
                            JSONArray jsonArrayy = new JSONArray(privateAddresses);
                            for (int i = 0; i < jsonArrayy.length(); i++) {
                                JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                String addressId = jsonObject2.getString("addressId");
                                String pictureURL = jsonObject2.getString("pictureURL");
                                String shortName = jsonObject2.getString("shortName");
                                String plusCode = jsonObject2.getString("plusCode");
                                String categoryName = "";
                                if(jsonObject2.has("categoryName")){
                                    categoryName = jsonObject2.getString("categoryName");
                                }
                                String addressReferenceId = jsonObject2.getString("addressReferenceId");

                                PrivateAddress privateAddress = new PrivateAddress(addressId,pictureURL,shortName,plusCode,categoryName,addressReferenceId);
                                privatelist.add(privateAddress);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                recyclerView.setHasFixedSize(true);

                                AdaterSharedAndSavedAddresses _adaterFriends = new AdaterSharedAndSavedAddresses(getApplicationContext(),privatelist,saveInterface,listId,true);
                                recyclerView.setAdapter(_adaterFriends);
                                Log.e("privateData",addressId+plusCode);
                            }
                        }

                        if (publiclist.isEmpty() && privatelist.isEmpty()){
                            consEmptyScreen.setVisibility(View.VISIBLE);
                            imdError.setImageResource(R.drawable.publicdefault);
                            txtError.setText("No shared public location found.");
                        }
                        else {
                            consEmptyScreen.setVisibility(View.GONE);
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(loader, SharedWithMeActivity.this);

            }
        });


//    }
    }




//====================================SAVE UNSAVED ADDRESS API==============================================//

    @Override
    public void saveUnsaveAddress(HashMap data, Boolean isSave) {

                if(isSave == true){
                 call = RetrofitInstance.getdata().create(Api.class).saveAddressToSavedList(data);
                }else{
                    call = RetrofitInstance.getdata().create(Api.class).unsaveAddressToSavedList(data);
                }
        UtilityClass.showLoading(loader, SharedWithMeActivity.this);


        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, ":saveUnsaveAddress " + response);

                UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {
                        new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                        new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                        new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        new Message().showSnack(root_lay, "No data found.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(root_lay, "This account has been blocked.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(root_lay, "All fields not sent.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(root_lay, " Please check the request method.");
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
                    }
                    UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(loader, SharedWithMeActivity.this);
            }
        });

    }
}



