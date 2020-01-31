package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
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

import java.net.URI;
import java.net.URISyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "sa";
    //============Defined Globles Vriables===================//
    ProgressBar loader;
    ConstraintLayout root_lay;
    String type = null;
    boolean struserId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        Log.e("appLinkData", "" +  appLinkData);
        if (appLinkData != null && appLinkData.toString().contains("http://simplrpost.com/")) {
            String url = appLinkData.toString();
            URI uri = null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String[] segments = uri.getPath().split("/");
            String idStr = segments[segments.length-1];
            String prefix = idStr;
            String newUrl = "http://simplrpost.com/"+prefix;
            Log.e("newUrl",""+newUrl);

            SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
            struserId = preferences.getBoolean(Constants.isUserLoggedIn, false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (struserId == false) {
                        Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                        i.putExtra("intent_data",url);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        get_uniqueLinkAddress(newUrl);
                    }
                }
            }, 2000);

        }else {
            loader = findViewById(R.id.Loader);
            root_lay = findViewById(R.id.root_lay);
            type = getIntent().getStringExtra("type");

            SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
            struserId = preferences.getBoolean(Constants.isUserLoggedIn, false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (struserId == false) {
                        Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, Home_Activity_new.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                }
            }, 2000);

        }

    }

        public void get_uniqueLinkAddress(String addressLink) {
            try {
                if (UtilityClass.isNetworkConnected(SplashActivity.this)) {
                    Parser.callApi(SplashActivity.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getuniqueLinkAddress(addressLink), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("ADD DATA", "Address response::" + response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result_code = jsonObject.getString("resultCode");
                                if (result_code.equals("1")) {

                                    JSONObject object = jsonObject.optJSONObject("resultData");
                                    String addressId = object.optString("addressId");
                                    String userId = object.optString("userId");
                                    String profilePicURL = object.optString("profilePicURL");
                                    String userName = object.optString("name");
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


                                    startActivity(new Intent(SplashActivity.this, Activity_ViewPublicAddress.class)
                                            .putExtra("address_id", addressId)
                                            .putExtra("user_id", userId)
                                            .putExtra("profile_img", profilePicURL)
                                            .putExtra("user_name", userName)
                                            .putExtra("plus_code", plus_code)
                                            .putExtra("public_private_tag", address_tag)
                                            .putExtra("qr_code_img", qrCode_image)
                                            .putExtra("street_img", street_image)
                                            .putExtra("building_img", building_image)
                                            .putExtra("entrance_img", entrance_image)
                                            .putExtra("address_unique_link", unique_link)
                                            .putExtra("country", country)
                                            .putExtra("city", city)
                                            .putExtra("street", street_name)
                                            .putExtra("building", building_name)
                                            .putExtra("entrance", entrance_name)
                                            .putExtra("latitude", String.valueOf(latitude))
                                            .putExtra("longitude", String.valueOf(longitude))
                                            .putExtra("street_img_type", street_img_type)
                                            .putExtra("building_img_type", building_img_type)
                                            .putExtra("entrance_img_type", entrance_img_type)
                                            .putExtra("from", "home")
                                            .putExtra("direction_txt", direction_text).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();


                                } else if (result_code.equals("0")) {
                                    Toast.makeText(SplashActivity.this,"Address not found.",Toast.LENGTH_SHORT).show();
                                    SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                                    struserId = preferences.getBoolean(Constants.isUserLoggedIn, false);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (struserId == false) {
                                                Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                Intent i = new Intent(SplashActivity.this, Home_Activity_new.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }, 2000);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }
            } catch (Exception e) {
            }
        }

}

