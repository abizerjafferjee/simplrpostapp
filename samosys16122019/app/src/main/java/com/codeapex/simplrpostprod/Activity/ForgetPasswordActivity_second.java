package com.codeapex.simplrpostprod.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetPasswordActivity_second extends AppCompatActivity {

    //.............GLOBAL vARIABLE..............//
    ImageView imgBackPress;
    LinearLayout root_lay;
    TextView txt_email_sign;
    Button btn_submit;
    EditText signup_contactno;
    TextView spinnerPhone,txt_cCode;
    String spinnerValue;
    Spinner spinnerCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpassword_two);

        //......................ALL IDS.................//
        imgBackPress = findViewById(R.id.back_press);
        root_lay = findViewById(R.id.root_lay);
        btn_submit = findViewById(R.id.btn_submit);
        txt_cCode = findViewById(R.id.txt_cCode);
        signup_contactno = findViewById(R.id.signup_contactno);
        txt_email_sign = findViewById(R.id.txt_email_sign);
        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spanTextview();

        spinnerValue = "+91";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).toString().equals("India")) {
                    spinnerValue = "+91";
                    txt_cCode.setText("+91");
                } else if (parent.getItemAtPosition(position).toString().equals("Kenya")) {
                    spinnerValue = "+254";
                    txt_cCode.setText("+254");
                } else if (parent.getItemAtPosition(position).toString().equals("Tanzania")) {
                    spinnerValue = "+255";
                    txt_cCode.setText("+255");
                } else if (parent.getItemAtPosition(position).toString().equals("United States")) {
                    spinnerValue = "+1";
                    txt_cCode.setText("+1");
                }
                // spinnerValue = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        signup_contactno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    call_api();
                    return true;
                } else {
                    return false;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinnerValue.equals("+254") && signup_contactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    signup_contactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+91") && signup_contactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    signup_contactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+255") && signup_contactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    signup_contactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+1") && signup_contactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    signup_contactno.requestFocus();
                    return;
                } else {
                    call_api();
                }
            }
        });
    }

    private void spanTextview() {
        TextView tvTerms = findViewById(R.id.txt_email_sign);

        SpannableString SpanString = new SpannableString("You can reset password with your registered email address.");

        ClickableSpan terems = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                try {
                    startActivity(new Intent(ForgetPasswordActivity_second.this, ForgetPasswordActivity.class));

                } catch (Exception e) {
                }
            }
        };

        SpanString.setSpan(terems, 44, 58, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 44, 58, 0);
        SpanString.setSpan(new UnderlineSpan(), 44, 58, 0);

        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setText(SpanString, TextView.BufferType.SPANNABLE);
        tvTerms.setSelected(true);
    }

    private void call_api() {

        Parser.callApi(ForgetPasswordActivity_second.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).forgot_password_second(spinnerValue + signup_contactno.getText().toString().trim()), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Forgot Pass response :", "" + spinnerValue + signup_contactno.getText().toString() + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString("status");
                    if (result_code.equals("1")) {
                        JSONObject object = jsonObject.optJSONObject("data");
                        String userId = object.optString("userId");
                        JSONObject object_data = object.optJSONObject("data");
                        JSONObject object_data_otp = object_data.optJSONObject("data");

                        String otpId = object_data_otp.optString("otpId");
                        String otpType = object_data_otp.optString("type");

                        startActivity(new Intent(ForgetPasswordActivity_second.this, OTPActivity_forgot.class)
                                .putExtra("otpId", otpId)
                                .putExtra("otpType", otpType)
                                .putExtra("userId", userId)
                                .putExtra("from", "number")
                                .putExtra("mobileNumber", spinnerValue + signup_contactno.getText().toString()));
                        finish();
                    } else if (jsonObject.optString("message").equals("Failed !") && result_code.equals("0")) {
                        new Message().showSnack(root_lay, "Failed to send OTP!");
                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, "Mobile number not registered!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //====================================Functions===========================================//
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

