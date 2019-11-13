package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

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

public class ResetPasswordActivity extends AppCompatActivity {

    //............Golobal Variable.............//
    CardView submitBtncard;
    ImageView imgBackPress;
    Boolean isLoading = false;
    ProgressBar Loader;
    String StrOtp_id;
    AlertDialog.Builder alertBox;
    EditText edtNew_password, edtConfrim_password;
    ConstraintLayout root_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        //............gET fROM iNTENT...........//

        StrOtp_id = getIntent().getExtras().getString("otpId");

        //............all ids Click lISTENSER .............//


        root_lay = findViewById(R.id.root_lay);
        edtNew_password = findViewById(R.id.newpassword);
        edtConfrim_password = findViewById(R.id.confrimpassword);
        Loader = findViewById(R.id.Loader);
        submitBtncard = findViewById(R.id.cardView);

        //...........................aPI AND clickListeners.........................//



        imgBackPress = findViewById(R.id.back_press);
        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });


        submitBtncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (edtNew_password.getText().toString().trim().length() == 0) {
                        new Message().showSnack(root_lay, "Please enter new password.");
                        edtNew_password.requestFocus();
                        return;
                    } else if (edtNew_password.getText().toString().trim().length() < 8 || edtNew_password.getText().toString().trim().length() > 20) {
                        new Message().showSnack(root_lay,"Password length should be in between 8 & 20.");
                    }
                    else if (edtConfrim_password.getText().toString().trim().length() == 0) {
                        new Message().showSnack(root_lay, "Please enter a confirm password.");
                        edtConfrim_password.requestFocus();
                        return;

                    }
                    else if (!(edtNew_password.getText().toString().trim().equals(edtConfrim_password.getText().toString().trim()))) {
                        new Message().showSnack(root_lay,"Both new passwords didn't matched.");
                    }
                    else {
                        try {
                            if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(Constants.otp_id, StrOtp_id.trim());
                                hashMap.put(Constants.new_password, edtNew_password.getText().toString().trim());
                                Call<Object> call = RetrofitInstance.getdata().create(Api.class).reset_password(hashMap);
                                UtilityClass.showLoading(Loader,ResetPasswordActivity.this);
                                call.enqueue(new Callback<Object>() {
                                    @Override
                                    public void onResponse(Call<Object> call, Response<Object> response) {
                                        UtilityClass.hideLoading(Loader,ResetPasswordActivity.this);
                                        if (response.isSuccessful()) {
                                            try {
                                                //Log.e("TAG---", "Data get>>>\n: " + new Gson().toJson(response.body()));
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


                                                } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                                    new Message().showSnack(root_lay, " This session has expired.");

                                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                                    new Message().showSnack(root_lay, "This account has been blocked.");


                                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                                    new Message().showSnack(root_lay, "All fields not sent.");

                                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                                    new Message().showSnack(root_lay, " Please check the request method.");
                                                }

                                                else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                                                    alertDialogBuilder.setMessage("Password has been reset successfully.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            //prefManager.setFirstTimeLaunch(true);
                                                            startActivity(new Intent(ResetPasswordActivity.this, SignInActivity.class));
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.show();
                                                }
                                                isLoading = false;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Object> call, Throwable t) {
                                        new UtilityClass().alertBox(ResetPasswordActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                                        UtilityClass.hideLoading(Loader,ResetPasswordActivity.this);
                                    }
                                });

                            }
                            else {
                                new Message().showSnack(root_lay,Constants.noInternetMessage);
                            }

                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
            }


        });


    }


}


