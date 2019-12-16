package com.codeapex.simplrpostprod.Activity;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class TermActivity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    TextView textView;
    ImageView imageView;
    private String TAG;
    ProgressBar Loader;
    TextView texta;
    ConstraintLayout root_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_condition);
        textView = findViewById(R.id.text);
        textView.setText("Terms & Conditions");
        Loader  = findViewById(R.id.Loaderr);
        root_lay  = findViewById(R.id.root_lay);
        relativeLayout = findViewById(R.id.toolbar_lay);
        texta = findViewById(R.id.textttt);
        termsConditions();

        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }


        });
    }

   //...........................API...........................//

    private void termsConditions() {
        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {

                Call<Object> call = RetrofitInstance.getdata().create(Api.class).termsConditions();
                UtilityClass.showLoading(Loader,TermActivity.this);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.d(TAG, "onResponseabout_us: "+response);
                        if (response.isSuccessful()) {
                            UtilityClass.hideLoading(Loader,TermActivity.this);
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
                                        texta.setText(Html.fromHtml(strContent, Html.FROM_HTML_MODE_COMPACT));
                                    }


                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        new UtilityClass().alertBox(TermActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);

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

