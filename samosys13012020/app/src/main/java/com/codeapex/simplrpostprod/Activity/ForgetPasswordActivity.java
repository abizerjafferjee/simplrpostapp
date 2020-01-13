package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity extends AppCompatActivity {

    //.............GLOBAL VARIABLE..............//
    EditText edt_email;
    ImageView imgBackPress;
    LinearLayout root_lay, btn_otp;
    Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword_one);

        //......................ALL IDS.................//
        imgBackPress = findViewById(R.id.back_press);
        root_lay = findViewById(R.id.root_lay);
        btn_submit = findViewById(R.id.btn_submit);
        edt_email = findViewById(R.id.edt_email);
        btn_otp = findViewById(R.id.btn_otp);
        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });


        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, ForgetPasswordActivity_second.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        edt_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    callApi();
                    return true;
                } else {
                    return false;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_email.getText().toString().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter registered email.");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edt_email.getText().toString()).matches()) {
                    new Message().showSnack(root_lay, "Invalid email.");
                    return;
                } else {
                    callApi();
                }
            }
        });
    }

    private void callApi() {

        Parser.callApi(ForgetPasswordActivity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).forgot_password(edt_email.getText().toString().trim()), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Forgot 1 In response::", "" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString("resultCode");
                    if (result_code.equals("1")) {
                        JSONObject data = jsonObject.optJSONObject("response");
                        final String mob = data.optString("mobile");
                        final String userid = data.optString("userid");
                        final String otpid = data.optString("otpid");
                        final String otpType = data.optString("type");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(ForgetPasswordActivity.this, OTPActivity_forgot.class)
                                        .putExtra("otpId", otpid)
                                        .putExtra("otpType", otpType)
                                        .putExtra("userId", userid)
                                        .putExtra("from", "email")
                                        .putExtra("email", edt_email.getText().toString().trim())
                                        .putExtra("mobileNumber", mob));
                                finish();
                            }
                        }, 300);

                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, "Email not registered!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //====================================Functions===========================================//
    public void validateEmail() {
        try {
            if (edt_email.getText().toString().trim().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter email_id.");
                edt_email.requestFocus();
                return;

            } else {
                forgotPassword(1);

            }
        } catch (Exception e) {
        }
    }

    void forgotPassword(final int isEmail) {
       /* try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.emailforget, edt_email.getText().toString().trim());
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
        }*/
    }
}

