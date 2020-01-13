package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import static android.content.ContentValues.TAG;

public class ChangePasswordActivity extends AppCompatActivity {
    //............globelVarible...........//
    CardView cardViewSubmit;
    ImageView imgBack;
    ConstraintLayout root_lay;
    String struserId;
    AlertDialog.Builder alertBox;
    ProgressBar Loader;
    EditText edtConfrimPassworsd,edtNewPassword,edtCurrentrPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_design);
        //...........All IdS.........///



        root_lay = findViewById(R.id.root_lay);

        edtConfrimPassworsd = findViewById(R.id.confrim_password);
        edtNewPassword = findViewById(R.id.new_password);
       edtCurrentrPassword = findViewById(R.id.curnnt_password);
        cardViewSubmit = findViewById(R.id.cardView);
        Loader = findViewById(R.id.Loader);

        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        struserId = preferences.getString(Constants.userId,"");


        //............All CLiclks..............//

        imgBack = findViewById(R.id.back_press);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
        cardViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(ChangePasswordActivity.this)) {
                    if (edtCurrentrPassword.getText().toString().trim().isEmpty()) {
                        new Message().showSnack(root_lay, "Please enter current password.");
                        edtCurrentrPassword.requestFocus();

                    } else if (edtNewPassword.getText().toString().trim().isEmpty()) {
                        new Message().showSnack(root_lay, "Please enter new password.");
                        edtNewPassword.requestFocus();
                    }else if ((edtNewPassword.getText().toString().trim().length() < 8) || (edtNewPassword.getText().toString().trim().length() > 20)) {
                        new Message().showSnack(root_lay, "Password length should be between 8 & 20.");
                        edtNewPassword.requestFocus();
                        return;
                    }  else if (edtConfrimPassworsd.getText().toString().trim().isEmpty()) {
                        new Message().showSnack(root_lay, "Please enter confirm password.");
                        edtConfrimPassworsd.requestFocus();
                    } else if (!(edtConfrimPassworsd.getText().toString().equals(edtNewPassword.getText().toString()))) {
                        new Message().showSnack(root_lay, "Both new passwords didn't matched.");
                        edtConfrimPassworsd.requestFocus();
                        return;
                    } else {
                        changepassword();
                    }
                }
                else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }

            }

        });

}


//.............................................api.........................//
    public void changepassword() {


        try {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);
            hashMap.put(Constants.currentPassword,edtCurrentrPassword.getText().toString().trim());
            hashMap.put(Constants.newPassword,edtNewPassword.getText().toString().trim());

            Log.d(TAG, "changepasswordhashmap: "+struserId+edtCurrentrPassword+edtNewPassword);
            UtilityClass.showLoading(Loader,ChangePasswordActivity.this);

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).changePassword(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,ChangePasswordActivity.this);
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
                                new Message().showSnack(root_lay, " Current Password didn't matched.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");


                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "All fields not sent.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            }

                            else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                AlertDialog.Builder builder
                                        = new AlertDialog
                                        .Builder(ChangePasswordActivity.this);
                                builder.setMessage("Your password has been changed successfully.");
                                builder.setTitle("Success");
                                builder.setCancelable(false);
                                builder
                                        .setPositiveButton(
                                                "Ok",
                                                new DialogInterface
                                                        .OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {

                                                        finish();
                                                    }
                                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,ChangePasswordActivity.this);
                }
            });
        } catch (Exception e) {

        }
    }

}
