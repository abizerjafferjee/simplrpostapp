package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.SmsListener;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.SmsReceiver;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class SignInActivity extends AppCompatActivity {

    //==========Defined Globle variables====================//

    ProgressBar loader;
    LinearLayout linearLayoutSignup;
    TextView textforgotPasswprd, txt_cCode;
    Button btn_signIn;
    EditText Edtcontactno, Edtpassword;
    LinearLayout root_lay;
    Spinner spinnerCountry;
    String spinnerValue;
    LinearLayout btn_sighInWithOtp;
    String otpId, otpType, otp;
    ImageView btn_eye;
    String intent_data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_one);
        intent_data = this.getIntent().getStringExtra("intent_data");
        findIds();
    }

    public void findIds() {
        //==========Find ids====================//
        linearLayoutSignup = findViewById(R.id.layoutSignup);
        loader = findViewById(R.id.Loader);
        textforgotPasswprd = findViewById(R.id.forget);
        btn_signIn = findViewById(R.id.btn_signIn);
        Edtcontactno = findViewById(R.id.signup_contactno);
        Edtpassword = findViewById(R.id.signup_password);
        root_lay = findViewById(R.id.root_lay);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        txt_cCode = findViewById(R.id.txt_cCode);
        btn_sighInWithOtp = findViewById(R.id.btn_sighInWithOtp);
        btn_eye = findViewById(R.id.btn_eye);
        btn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Edtpassword.getText().toString().length() > 0) {
                    if (v.getTag().toString().equals("close")) {
                        v.setTag("open");
                        btn_eye.setImageResource(R.drawable.eye_close);
                        Edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        v.setTag("close");
                        btn_eye.setImageResource(R.drawable.eye_icon);
                        Edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                otp = messageText;
                Log.e("OTP:", "OTP __ " + messageText);
            }
        });
        spinnerValue = "+251";

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
                } else if (parent.getItemAtPosition(position).toString().equals("Ethopia")) {
                    spinnerValue = "+251";
                    txt_cCode.setText("+251");
                } else if (parent.getItemAtPosition(position).toString().equals("Nigeria")) {
                    spinnerValue = "+234";
                    txt_cCode.setText("+234");
                } else if (parent.getItemAtPosition(position).toString().equals("Uganda")) {
                    spinnerValue = "+256";
                    txt_cCode.setText("+256");
                } else if (parent.getItemAtPosition(position).toString().equals("Algeria")) {
                    spinnerValue = "+213";
                    txt_cCode.setText("+213");
                } else if (parent.getItemAtPosition(position).toString().equals("Angola")) {
                    spinnerValue = "+244";
                    txt_cCode.setText("+244");
                } else if (parent.getItemAtPosition(position).toString().equals("Botswana")) {
                    spinnerValue = "+267";
                    txt_cCode.setText("+267");
                } else if (parent.getItemAtPosition(position).toString().equals("Cameroon")) {
                    spinnerValue = "+237";
                    txt_cCode.setText("+237");
                } else if (parent.getItemAtPosition(position).toString().equals("Ghana")) {
                    spinnerValue = "+233";
                    txt_cCode.setText("+233");
                } else if (parent.getItemAtPosition(position).toString().equals("Mozambique")) {
                    spinnerValue = "+258";
                    txt_cCode.setText("+258");
                } else if (parent.getItemAtPosition(position).toString().equals("Morocco")) {
                    spinnerValue = "+212";
                    txt_cCode.setText("+212");
                } else if (parent.getItemAtPosition(position).toString().equals("Rwanda")) {
                    spinnerValue = "+250";
                    txt_cCode.setText("+250");
                } else if (parent.getItemAtPosition(position).toString().equals("Senegal")) {
                    spinnerValue = "+221";
                    txt_cCode.setText("+221");
                } else if (parent.getItemAtPosition(position).toString().equals("Tunisia")) {
                    spinnerValue = "+216";
                    txt_cCode.setText("+216");
                } else if (parent.getItemAtPosition(position).toString().equals("Zambia")) {
                    spinnerValue = "+263";
                    txt_cCode.setText("+263");
                } else if (parent.getItemAtPosition(position).toString().equals("Cote D’Ivoire")) {
                    spinnerValue = "+225";
                    txt_cCode.setText("+225");
                } else if (parent.getItemAtPosition(position).toString().equals("Gambia")) {
                    spinnerValue = "+220";
                    txt_cCode.setText("+220");
                } else if (parent.getItemAtPosition(position).toString().equals("Egypt")) {
                    spinnerValue = "+20";
                    txt_cCode.setText("+20");
                } else if (parent.getItemAtPosition(position).toString().equals("South Africa")) {
                    spinnerValue = "+27";
                    txt_cCode.setText("+27");
                }
                // spinnerValue = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Edtpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    login_validation_1();
                    return true;
                } else {
                    return false;
                }
            }
        });


        btn_sighInWithOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Edtcontactno.getText().toString().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter mobile number.");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+254") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+91") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+255") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+1") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+251") && Edtcontactno.getText().toString().trim().length() != 8) {
                    new Message().showSnack(root_lay, "Mobile number should be in 8 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+256") && Edtcontactno.getText().toString().trim().length() != 8) {
                    new Message().showSnack(root_lay, "Mobile number should be in 8 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+234") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                }
                else if (spinnerValue.equals("+213") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                }
                if (spinnerValue.equals("+244") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+267") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+237") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+233") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+258") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+212") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+250") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+221") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+216") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+263") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+225") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+220") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+20") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+27") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else {

                    Edtpassword.setVisibility(View.VISIBLE);

                    Parser.callApi(SignInActivity.this, "Sign In...", true, ApiClient.getClient().create(ApiInterface.class).login(spinnerValue + Edtcontactno.getText().toString().trim(), Edtpassword.getText().toString().trim(), "otp"), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Sign In response::", "" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String result_code = jsonObject.getString("status");
                                if (result_code.equals("1")) {
                                    String result_data = jsonObject.getString("data");
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    otpId = jsonObject1.optString("otpId");
                                    otpType = jsonObject1.optString("type");
                                    String userId = jsonObject1.optString("userId");
                                    Log.e("OTP_ID", "" + otpId);
                                    Intent intent = new Intent(SignInActivity.this, OTPActivity.class);
                                    intent.putExtra("otp_id", otpId);
                                    intent.putExtra("otpType", otpType);
                                    intent.putExtra("user_id", userId);
                                    intent.putExtra("intent_data", intent_data);
                                    intent.putExtra("mobile", spinnerValue + Edtcontactno.getText().toString());
                                    intent.putExtra("from", "Sign");

                                    startActivity(intent);
                                } else if (result_code.equals("0") && jsonObject.optString("message").equals("Please verify account first.")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(SignInActivity.this);
                                    dialog.setMessage("Your account is not verified yet,\nPlease Press ok to verify.");
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Parser.callApi(SignInActivity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).resend_otp(spinnerValue + Edtcontactno.getText().toString(), "4"), new Response_Call_Back() {
                                                @Override
                                                public void getResponseFromServer(String response) {
                                                    Log.e("Sign with OTP", "response :" + response);
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                                        if (result_code.equals("1")) {
                                                            String result_data = jsonObject.getString("response");
                                                            JSONObject jsonObject1 = new JSONObject(result_data);
                                                            JSONObject otpData = jsonObject1.optJSONObject("otpData");
                                                            String status = otpData.optString("status");

                                                            if (status.equals("1")) {
                                                                JSONObject data = otpData.optJSONObject("data");
                                                                otpId = data.optString("otpId");
                                                                otpType = data.optString("type");
                                                                Log.e("OTP_ID", "" + otpId);
                                                                Intent intent = new Intent(SignInActivity.this, OTPActivity.class);
                                                                intent.putExtra("otp_id", otpId);
                                                                intent.putExtra("otpType", otpType);
                                                                intent.putExtra("user_id", "");
                                                                intent.putExtra("mobile", spinnerValue + Edtcontactno.getText().toString());
                                                                intent.putExtra("from", "Sign");

                                                                startActivity(intent);
                                                            }

                                                        } else if (result_code.equals("0")) {
                                                            new Message().showSnack(root_lay, "" + jsonObject.optString("resultData"));
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    new Message().showSnack(root_lay, "" + jsonObject.optString("message"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

            }
        });

        //==========All listeners and Apis====================//

        linearLayoutSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this, SignUpFirstActivity.class);
                startActivity(intent);
            }
        });

        textforgotPasswprd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(SignInActivity.this)) {
                    Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity_second.class);
                    startActivity(intent);
                } else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }
            }
        });

        //...................................Api...............................//

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login_validation_1();
            }
        });
    }

    private void login_validation_1() {

        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                View view = getWindow().getCurrentFocus();
                if (view != null) {
                    UtilityClass.hideKeyboard(SignInActivity.this);
                }
                if (Edtcontactno.getText().toString().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter mobile number.");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+254") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+91") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+255") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+1") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+251") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+256") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+234") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (Edtpassword.getText().toString().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter password.");
                    Edtpassword.requestFocus();
                    return;
                } else if (Edtpassword.getText().toString().trim().length() < 8) {
                    new Message().showSnack(root_lay, "Password should be 8 characters.");
                    Edtpassword.requestFocus();
                    return;
                } else if (spinnerValue.equals("+213") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                }
                if (spinnerValue.equals("+244") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+267") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+237") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+233") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+258") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+212") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+250") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+221") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+216") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+263") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+225") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+220") && Edtcontactno.getText().toString().trim().length() != 9) {
                    new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+20") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else if (spinnerValue.equals("+27") && Edtcontactno.getText().toString().trim().length() != 10) {
                    new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                    Edtcontactno.requestFocus();
                    return;
                } else {

                    Parser.callApi(SignInActivity.this, "Sign In...", true, ApiClient.getClient().create(ApiInterface.class).login(spinnerValue + Edtcontactno.getText().toString().trim(), Edtpassword.getText().toString().trim(), "simple"), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Sign In response::", "" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String result_code = jsonObject.getString("status");
                                if (result_code.equals("1")) {
                                    String result_data = jsonObject.getString("data");
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Constants.isUserLoggedIn, true);
                                    editor.putString(Constants.userId, jsonObject1.getString(Constants.userId));
                                    editor.commit();
                                    register_device();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(SignInActivity.this, Home_Activity_new.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            finish();

                                        }
                                    }, 200);

                                } else if (result_code.equals("0") && jsonObject.optString("message").equals("Please verify account first.")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(SignInActivity.this);
                                    dialog.setMessage("Your account is not verified yet,\nPlease Press ok to verify.");
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Parser.callApi(SignInActivity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).resend_otp(spinnerValue + Edtcontactno.getText().toString(), "4"), new Response_Call_Back() {
                                                @Override
                                                public void getResponseFromServer(String response) {
                                                    Log.e("Sign with OTP", "response :" + response);
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                                        if (result_code.equals("1")) {
                                                            String result_data = jsonObject.getString("response");
                                                            JSONObject jsonObject1 = new JSONObject(result_data);
                                                            JSONObject otpData = jsonObject1.optJSONObject("otpData");
                                                            String status = otpData.optString("status");

                                                            if (status.equals("1")) {
                                                                JSONObject data = otpData.optJSONObject("data");
                                                                otpId = data.optString("otpId");
                                                                otpType = data.optString("type");
                                                                Log.e("OTP_ID", "" + otpId);
                                                                Intent intent = new Intent(SignInActivity.this, OTPActivity.class);
                                                                intent.putExtra("otp_id", otpId);
                                                                intent.putExtra("otpType", otpType);
                                                                intent.putExtra("user_id", "");
                                                                intent.putExtra("mobile", spinnerValue + Edtcontactno.getText().toString());
                                                                intent.putExtra("from", "Sign");

                                                                startActivity(intent);
                                                            }

                                                        } else if (result_code.equals("0")) {
                                                            new Message().showSnack(root_lay, "" + jsonObject.optString("resultData"));
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    new Message().showSnack(root_lay, "" + jsonObject.optString("message"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } else {
                new Message().showSnack(root_lay, Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }


    //=========================================GOOGLE SIGN IN==================================================//
    //...................................Api...............................//
    public void register_device() {
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId, "");

        try {

            final String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, struserId);
            hashMap.put(Constants.deviceId, strDeviceid);
            hashMap.put(Constants.deviceType, "a");
            hashMap.put(Constants.pushToken, preferences.getString("token", ""));


            Call<Object> call = RetrofitInstance.getdata().create(Api.class).registerDevice(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        try {

                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            Log.e("REGISTER DEVICE RESP:", "" + jsonObject);
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                                new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                                new Message().showSnack(root_lay, "Email Id and password didn't matched.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                new Message().showSnack(root_lay, "This account does not exist.");


                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "Mobile number already registered.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                startActivity(new Intent(SignInActivity.this, Home_Activity_new.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(loader, SignInActivity.this);
                    new UtilityClass().alertBox(SignInActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);
                }
            });
        } catch (Exception e) {

        }
    }

}
