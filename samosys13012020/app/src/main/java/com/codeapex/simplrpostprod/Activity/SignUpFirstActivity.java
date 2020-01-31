package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFirstActivity extends AppCompatActivity {
    //==========================Defined Globle variables=====================================//

    LinearLayout linearLayout, linearLayoutt;
    Button btn_signIn;
    EditText contactno, password;
    LinearLayout root_lay;
    ImageView back_press;
    TextView txt_cCode;
    Spinner spinnerCountry;
    String spinnerValue;
    ProgressBar Loader;
    String otpId = "";
    String otpType = "";
    String user_Id = "";
    String mobileNumber = "";
    ImageView btn_eye;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        try {
            setContentView(R.layout.activity_sign_up);
        } catch (Exception e) {
            e.printStackTrace();
            setContentView(R.layout.activity_sign_up);
        }

        /*if (hasPermissions(PERMISSIONS)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }*/
        //============================FindView and Listeners=======================================//

        back_press = findViewById(R.id.back_press);
        btn_signIn = findViewById(R.id.btn_signIn);
        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearLayoutt = findViewById(R.id.liner);
        root_lay = findViewById(R.id.root_lay);

        contactno = findViewById(R.id.signup_contactno);
        password = findViewById(R.id.signup_password);
        Loader = findViewById(R.id.Loader);
        spinnerCountry = findViewById(R.id.spinnerCountry);

        btn_eye = findViewById(R.id.btn_eye);
        btn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().length() > 0) {
                    if (v.getTag().toString().equals("close")) {
                        v.setTag("open");
                        btn_eye.setImageResource(R.drawable.eye_close);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        v.setTag("close");
                        btn_eye.setImageResource(R.drawable.eye_icon);
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    signup_validation();
                    return true;
                } else {
                    return false;
                }
            }
        });

        txt_cCode = findViewById(R.id.txt_cCode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);

        spinnerValue = "+251";

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (hasPermissions(PERMISSIONS)) {
                    signup_validation();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                    }
                }*/
                signup_validation();
            }
        });


        linearLayout = findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //====================================Functions===========================================//

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                Toast.makeText(SignUpFirstActivity.this, "Some Permission denied", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[] PERMISSIONS = {Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,};*/

    public void signup_validation() {
        try {
            if (contactno.getText().toString().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter mobile number");
                contactno.requestFocus();
            } else if (spinnerValue.equals("+254") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+91") && contactno.getText().toString().trim().length() != 10) {
                new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+255") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+1") && contactno.getText().toString().trim().length() != 10) {
                new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+251") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+256") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+234") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (password.getText().toString().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter password");
                password.requestFocus();
            } else if (password.getText().toString().trim().length() < 8) {
                new Message().showSnack(root_lay, "Password should be 8 characters.");
                password.requestFocus();
                return;
            } else if (spinnerValue.equals("+213") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+244") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+267") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+237") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+233") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+258") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+212") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+250") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+221") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+216") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+263") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+225") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+220") && contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Mobile number should be in 9 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+20") && contactno.getText().toString().trim().length() != 10) {
                new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                contactno.requestFocus();
                return;
            } else if (spinnerValue.equals("+27") && contactno.getText().toString().trim().length() != 10) {
                new Message().showSnack(root_lay, "Mobile number should be in 10 digit");
                contactno.requestFocus();
                return;
            } else {
                signup();
            }

        } catch (Exception e) {
        }
    }

    //================================End Functions===========================================//

    public void signup() {
        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {

                Parser.callApi(SignUpFirstActivity.this, "Sign up...", true, ApiClient.getClient().create(ApiInterface.class).signUp(spinnerValue + contactno.getText().toString().trim(), password.getText().toString().trim()), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("Sign up response::", "" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString("response");
                                JSONObject jsonObject1 = new JSONObject(result_data);
                                JSONObject otpData = jsonObject1.optJSONObject("otpData");
                                String status = otpData.optString("status");

                                if (status.equals("1")) {
                                    JSONObject data = otpData.optJSONObject("data");
                                    otpId = data.optString("otpId");
                                    otpType = data.optString("type");
                                }
                                user_Id = jsonObject1.getString(Constants.userId);
                                mobileNumber = jsonObject1.getString(Constants.contactNumber);

                                //register_device();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SignUpFirstActivity.this, OTPActivity.class);
                                        intent.putExtra("otp_id", otpId);
                                        intent.putExtra("otpType", otpType);
                                        intent.putExtra("user_id", user_Id);
                                        intent.putExtra("mobile", mobileNumber);
                                        intent.putExtra("from", "signUp");
                                        startActivity(intent);
                                    }
                                }, 300);
                            }
                            if (result_code.equals("-6.0")) {
                                new Message().showSnack(root_lay, "" + jsonObject.optString("resultData"));

                            } else if (result_code.equals("0")) {
                                new Message().showSnack(root_lay, "" + jsonObject.optString("resultData"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new Message().showSnack(root_lay, Constants.noInternetMessage);
            }

        } catch (Exception e) {
        }

    }

    public void register_device() {
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId, "");

        try {
            final String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, struserId);
            hashMap.put(Constants.deviceId, strDeviceid);
            hashMap.put(Constants.deviceType, "a");
            hashMap.put(Constants.pushToken, " ");

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).registerDevice(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        try {

                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                                new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                                new Message().showSnack(root_lay, "No data found.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");


                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "All fields not sent.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    new UtilityClass().alertBox(SignUpFirstActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);

                }
            });
/*


            String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Parser.callApi(SignUpFirstActivity.this, "Sign up...", true, ApiClient.getClient().create(ApiInterface.class).registerDevice(struserId,strDeviceid,"a",""), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("Reg device response::", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "No data found.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
*/


        } catch (Exception e) {

        }
    }
}