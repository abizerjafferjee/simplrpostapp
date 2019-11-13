package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.theartofdev.edmodo.cropper.CropImage;

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

public class SignupSecondActivity extends AppCompatActivity {

    //==========================Defined Globle variables=====================================//

    CardView cardView;
    ImageView imageView;
    LinearLayout linearLayout;
    EditText email_id, password, contactno;
    ConstraintLayout root_lay;
    Boolean isLoading = false;
    String strname, strusername, strimage,strEmail;
    ProgressBar loader;
    Spinner spinnerPhone;

    LinearLayout linearLayoutt;
    private String TAG = "signuptwo";
    private int RESULT_LOAD_IMAGE = 222;
    String spinnerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        //============================FindView and Listners=======================================//

        imageView = findViewById(R.id.back_press);
        loader = findViewById(R.id.Loader);
        linearLayout = findViewById(R.id.linear);

        root_lay = findViewById(R.id.root_lay);
        email_id = findViewById(R.id.signup_emailid);
        password = findViewById(R.id.signup_password);
        contactno = findViewById(R.id.signup_contactno);
        cardView = findViewById(R.id.cardView4);
        linearLayoutt = findViewById(R.id.linearclick);


        strname = getIntent().getExtras().getString("nameee");
        strusername = getIntent().getExtras().getString("usernameee");
        strimage = getIntent().getExtras().getString("Imgbase64");
        strEmail = getIntent().getExtras().getString("emailID");

        email_id.setText(strEmail);

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







        linearLayoutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(SignupSecondActivity.this);
            }
        });


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearLayout = findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupSecondActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    //====================================Functions===========================================//


    public void Login() {
        try {
            if (email_id.getText().toString().trim().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter email_id.");
                email_id.requestFocus();
                return;
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email_id.getText().toString().trim()).matches()) {
                new Message().showSnack(root_lay, "Entered email id is not in correct format.");
                email_id.requestFocus();
                return;
            } else if (contactno.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter a contact number.");
                contactno.requestFocus();
                return;

            }
            else if (contactno.getText().toString().trim().length() != 9) {
                new Message().showSnack(root_lay, "Contact number should of 9 digits.");
                contactno.requestFocus();
                return;
            }
            else if (password.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter a password.");
                password.requestFocus();
                return;
            }
            else if (password.getText().toString().trim().length() < 8 || password.getText().toString().trim().length() > 20) {
                new Message().showSnack(root_lay,"Password length should be in between 8 & 20.");
            }
            else {
                signup();

            }
        } catch (Exception e) {
        }
    }

    //================================Api===========================================//


    public void signup() {

        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.profilePicURL,strimage);
                hashMap.put(Constants.name, strname);
                hashMap.put(Constants.userName, strusername);
                hashMap.put(Constants.email_id, email_id.getText().toString().trim());

                hashMap.put(Constants.password, password.getText().toString().trim());

                hashMap.put(Constants.contactNumber, spinnerValue + contactno.getText().toString().trim());
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).signUp(hashMap);
                UtilityClass.showLoading(loader, SignupSecondActivity.this);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {

                        UtilityClass.hideLoading(loader, SignupSecondActivity.this);
//
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
                                    new Message().showSnack(root_lay, "Email Id is already registered.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                    new Message().showSnack(root_lay, "This account has been blocked.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                    new Message().showSnack(root_lay, "Username already registered.");


                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                    new Message().showSnack(root_lay, "Contact number already registered.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                    new Message().showSnack(root_lay, " Please check the request method.");
                                }

                                else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                    Log.e("LoginResponse", new Gson().toJson(response.body()));
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(Constants.isUserLoggedIn, true);
                                    editor.putString(Constants.userId, jsonObject1.getString(Constants.userId));
                                    editor.commit();

                                    register_device();

                                    final String otp_id = jsonObject1.getString(Constants.otpId);



//                                    Intent intent = new Intent(SignupSecondActivity.this, OTPVerificationActivity.class);
//                                    intent.putExtra("otp_id",otp_id);
//                                    startActivity(intent);




                                   Intent intent = new Intent (SignupSecondActivity.this,HomeActivity.class);
                                    startActivity(intent);



                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        UtilityClass.hideLoading(loader, SignupSecondActivity.this);
                        new UtilityClass().alertBox(SignupSecondActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);

                    }
                });
            }
            else {
                new Message().showSnack(root_lay,Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }

    }

    //================================Api===========================================//


    public void register_device() {
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId,"");


        try {



            final String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);
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

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    new UtilityClass().alertBox(SignupSecondActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);

                }
            });
        } catch (Exception e) {

        }
    }
    }



