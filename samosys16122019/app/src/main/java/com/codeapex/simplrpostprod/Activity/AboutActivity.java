package com.codeapex.simplrpostprod.Activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class AboutActivity extends AppCompatActivity {
    TextView txtToolbar, txtContent;
    ImageView imgAboutUs,imgBackButton ;
    ProgressBar Loader;
    private String TAG;
    Call<Object> call;

    String strToolbarHeading;
    ConstraintLayout root_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        //.....................All find views...................//

        imgBackButton = findViewById(R.id.back_press);
        root_lay = findViewById(R.id.root_lay);
        imgAboutUs = findViewById(R.id.forgotimage);
        Loader = findViewById(R.id.Loader);
        txtContent = findViewById(R.id.aboutus_texttwo);
        txtToolbar = findViewById(R.id.toolbar_text);

        strToolbarHeading = getIntent().getExtras().getString("toolbar_heading");
        txtToolbar.setText(strToolbarHeading);
        imgAboutUs.setVisibility(View.VISIBLE);
        about_us();


    //.............................................Clilck.....................//
        imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
    }



//...............................................Api.................................................//
    private void about_us() {
        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {

                Call<Object> call = RetrofitInstance.getdata().create(Api.class).about_us();
                UtilityClass.showLoading(Loader,AboutActivity.this);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        UtilityClass.hideLoading(Loader,AboutActivity.this);
                        if (response.isSuccessful()) {

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
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    String strContent = jsonObject1.getString("content");


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        txtContent.setText(Html.fromHtml(strContent, Html.FROM_HTML_MODE_COMPACT));
                                    }


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        new UtilityClass().alertBox(AboutActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                        UtilityClass.hideLoading(Loader,AboutActivity.this);
                    }
                });
            }
            else {
                new Message().showSnack(root_lay,Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }

    }
        }


