package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;

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

public class ForgetPasswordActivity extends AppCompatActivity {

    //.............GLOBAL vARIABLE..............//
    ImageView imgBackPress;
    ProgressBar Loader;
    ScrollView scrollOtpemail, scrollOtpcontact;
    EditText Edtemail_id,Edtcontactno;
    ConstraintLayout root_lay;
    Spinner spinnerPhone;
    String spinnerValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword_two);

        //......................ALL IDS.................//

        scrollOtpemail = findViewById(R.id.scrolloptone);
        scrollOtpcontact = findViewById(R.id.scrollotp);
        Edtemail_id = findViewById(R.id.forgotpassword_email);
        Loader = findViewById(R.id.Loader);
        root_lay = findViewById(R.id.root_lay);
        Edtcontactno = findViewById(R.id.forgotpassword_contactno);
        spinnerPhone = findViewById(R.id.spinnerPhone);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(adapter);

        spinnerPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerValue = parent.getItemAtPosition(position).toString();
                 }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.emailbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(ForgetPasswordActivity.this)) {
                    validateEmail();
                }
                else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }
            }
        });

        findViewById(R.id.contactbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(ForgetPasswordActivity.this)) {
                    validateContactNo();
                }
                else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }
            }
        });

        findViewById(R.id.textmobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollOtpcontact.setVisibility(View.VISIBLE);
                scrollOtpemail.setVisibility(View.GONE);

            }
        });

        findViewById(R.id.textt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollOtpcontact.setVisibility(View.GONE);
                scrollOtpemail.setVisibility(View.VISIBLE);

            }
        });


        imgBackPress = findViewById(R.id.back_press);
        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
    }



    //====================================Functions===========================================//
    public void validateEmail() {
        try {
            if (Edtemail_id.getText().toString().trim().isEmpty()) {
                new Message().showSnack(scrollOtpemail, "Please enter email_id.");
                Edtemail_id.requestFocus();
                return;

            } else {
                forgotPassword(1);

            }
        } catch (Exception e) {
        }
    }



    public void validateContactNo() {
        try {
            if (Edtcontactno.getText().toString().trim().isEmpty()) {
                new Message().showSnack(scrollOtpcontact, "Please enter contactno.");
                Edtcontactno.requestFocus();
                return;

            } else {
                forgotPassword(0);

            }
        } catch (Exception e) {
        }
    }


    //================================Api===============================//


    void forgotPassword (final int isEmail) {
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.emailforget, Edtemail_id.getText().toString().trim());
            hashMap.put(Constants.contactno, spinnerValue + Edtcontactno.getText().toString().trim());
            hashMap.put(Constants.isEmailUsed, Integer.toString(isEmail));
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).forgot_password(hashMap);
            UtilityClass.showLoading(Loader,ForgetPasswordActivity.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,ForgetPasswordActivity.this);
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


                                 }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                new Message().showSnack(root_lay, "This account does not exist.");


                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "All fields not sent.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            }

                            else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                final JSONObject jsonObject1 = new JSONObject(result_data);
                                final String otp_id = jsonObject1.getString(Constants.otpId);
                                Intent intent = new Intent(ForgetPasswordActivity.this,OTPActivity.class);
                                intent.putExtra("otpId", otp_id);
                                intent.putExtra("contact", (isEmail == 0)?Edtcontactno.getText().toString().trim():"");
                                intent.putExtra("email", (isEmail == 1)?Edtemail_id.getText().toString().trim():"");
                                intent.putExtra("isEmailUsed", Integer.toString(isEmail));

                                startActivity(intent);
//
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    new UtilityClass().alertBox(ForgetPasswordActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                    UtilityClass.showLoading(Loader,ForgetPasswordActivity.this);
                }
            });
        } catch (Exception e) {
        }
    }
    }

