package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {
      ImageView imageView;
    RatingBar ratingBar;
    EditText feedback_Message;
     Button button;
     String userId;
     SharedPreferences preferences;
    private String TAG;
    ProgressBar Loader;
    ConstraintLayout root_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        ratingBar = findViewById(R.id.rating);
        feedback_Message = findViewById(R.id.feedback_Message);
        root_lay = findViewById(R.id.root_lay);
        Loader = findViewById(R.id.Loader);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });


        preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        userId = preferences.getString(Constants.userId,"");
        Log.d(TAG, "onCreateView: "+userId);

        button = findViewById(R.id.submit_button_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(feedback_Message.getText().toString().trim().isEmpty()){
                        new UtilityClass().alertBox(FeedbackActivity.this,"Please enter feedback.","Error");
                    }else {
                        View view = getWindow().getCurrentFocus();
                        if (view != null) {
                            UtilityClass.hideKeyboard(FeedbackActivity.this);
                        }
                        feedbackAPI();
                    }

            }
        });

        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }


        });
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    public void feedbackAPI() {
        if (UtilityClass.isNetworkConnected(FeedbackActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, userId);
            hashMap.put("rating", String.valueOf(ratingBar.getRating()));
            hashMap.put("content",feedback_Message.getText().toString().trim());
            System.out.println("Data   :- "+hashMap);
            //UtilityClass.showLoading(Loader,FeedbackActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).userFeedback(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("feedbackResponse", new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        //UtilityClass.hideLoading(Loader,FeedbackActivity.this);
                        try {
                            Log.e("TAG", "Data get>>>\n: " + new Gson().toJson(response.body()));
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


                                AlertDialog.Builder alertbox = new AlertDialog.Builder(FeedbackActivity.this);
                                alertbox.setMessage("Your feedback has been submitted.");
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    //UtilityClass.hideLoading(Loader,FeedbackActivity.this);
                    new UtilityClass().alertBox(FeedbackActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);            }
            });
        }
        else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }

    }
}
