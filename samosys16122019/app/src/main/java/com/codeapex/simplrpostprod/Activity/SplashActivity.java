package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
        loader = findViewById(R.id.Loader);
        root_lay = findViewById(R.id.root_lay);

        type = getIntent().getStringExtra("type");


//        if (getIntent().hasExtra("notificationData")) {
//            Boolean obj = getIntent().getBooleanExtra("notificationData",false);
//            Log.d(TAG, String.valueOf(obj));
//            new UtilityClass().alertBox(SplashActivity.this,"message","title");
//        }
//        else {

            //baseURL_API();
//        }
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        struserId = preferences.getBoolean(Constants.isUserLoggedIn,false);
        Log.e("struserId",""+struserId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (struserId==false){
                    Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        },2000);










    }

    //==============Services Api========================================//


    public void baseURL_API(){
        UtilityClass.showLoading(loader,SplashActivity.this);
        Api apiService = RetrofitInstance.getdata().create(Api.class);
        Call<Object> call = apiService.getbaseurl();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    UtilityClass.hideLoading(loader,SplashActivity.this);
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);

                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "Something went wrong.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");


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
                            JSONObject result_data = jsonObject.getJSONObject(Constants.RESULT_DATA);
                            String strBaseURL = result_data.getString(Constants.BASE_URL1);
                            String strImageURL = result_data.getString(Constants.IMAGE_URL1);
                            strBaseURL = strBaseURL + "index.php/Api/user/";
                            SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.baseURLKey, strBaseURL);
                            editor.putString(Constants.imageBaseURLKey, strImageURL);
                            editor.commit();

                            if (getIntent().hasExtra("type")) {
                                //If flow is from notification
                                if (sharedPreferences.getBoolean(Constants.isUserLoggedIn, false) == true) {
                                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);

                                    i.putExtra("isNotification",true);
                                    i.putExtra("type",type);

                                    if (!type.equals("admin")) {
                                        i.putExtra("addressId", getIntent().getStringExtra("addressId"));
                                        i.putExtra("isAddressPublic", getIntent().getStringExtra("isAddressPublic"));
                                        if (type.equals("businessShare")) {
                                            i.putExtra("recipientBusinessId", getIntent().getStringExtra("recipientBusinessId"));
                                        }
                                    }
                                    startActivity(i);
                                    finish();


                                } else {
                                    Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
//                                    finish();
                                }
                            }
                            else {
                                //If flow is normal and not from notification
                                if (sharedPreferences.getBoolean(Constants.isUserLoggedIn, false) == true) {
                                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();


                                } else {
                                    if (sharedPreferences.getBoolean("isFirstLoadDone", false) == false) {

                                        editor.putBoolean("isFirstLoadDone",true);
                                        editor.commit();
                                        Intent i = new Intent(SplashActivity.this, TutorialActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else {
                                        Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                UtilityClass.hideLoading(loader,SplashActivity.this);
                new UtilityClass().alertBox(SplashActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);

            }
        });
    }
}

