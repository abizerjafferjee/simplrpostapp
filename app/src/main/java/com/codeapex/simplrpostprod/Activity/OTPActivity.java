package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    CardView cardSubmitButtn;
    ImageView ImgbackPress;
    ProgressBar loader;
    TextView resend;
    String Otp_id, otp,email,contact,isEmailUsed;
    EditText editText_otp1, editText_otp2, editText_otp3, editText_otp4, editText_otp5, editText_otp6;
    RelativeLayout root_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        root_lay = findViewById(R.id.root_lay);
        loader = findViewById(R.id.Loader);
        resend = findViewById(R.id.resend);



        editText_otp1 = findViewById(R.id.editText1);
        editText_otp2 = findViewById(R.id.editText2);
        editText_otp3 = findViewById(R.id.editText3);
        editText_otp4 = findViewById(R.id.editText4);
        editText_otp5 = findViewById(R.id.editText5);
        editText_otp6 = findViewById(R.id.editText6);


        Otp_id = getIntent().getExtras().getString("otpId");
        email = getIntent().getExtras().getString("email");
        contact = getIntent().getExtras().getString("contact");
        isEmailUsed = getIntent().getExtras().getString("isEmailUsed");





        cardSubmitButtn = findViewById(R.id.cardView);
        cardSubmitButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otp = editText_otp1.getText().toString().trim() + editText_otp2.getText().toString().trim() + editText_otp3.getText().toString().trim() + editText_otp4.getText().toString().trim() + editText_otp5.getText().toString().trim() + editText_otp6.getText().toString().trim();
                OtpEnter();
            }
        });

        editText_otp1.addTextChangedListener(new OtpTextWatcher(editText_otp1));
        editText_otp2.addTextChangedListener(new OtpTextWatcher(editText_otp2));
        editText_otp3.addTextChangedListener(new OtpTextWatcher(editText_otp3));
        editText_otp4.addTextChangedListener(new OtpTextWatcher(editText_otp4));
        editText_otp5.addTextChangedListener(new OtpTextWatcher(editText_otp5));
        editText_otp6.addTextChangedListener(new OtpTextWatcher(editText_otp6));


        ImgbackPress = findViewById(R.id.back_press);
        ImgbackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }


        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp();
                timercount();
            }
        });

    }
    @Override
    public void onBackPressed() {
        //Execute your code here
        finish();

    }



    //====================================Functions===========================================//
    public void OtpEnter() {
        try {
            if (editText_otp1.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp1.requestFocus();
                return;
            } else if (editText_otp2.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp2.requestFocus();
                return;
            } else if (editText_otp3.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp3.requestFocus();
                return;
            } else if (editText_otp4.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp4.requestFocus();
                return;
            } else if (editText_otp5.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp5.requestFocus();
                return;
            } else if (editText_otp6.getText().toString().trim().length() == 0) {
                new Message().showSnack(root_lay, "Please enter complete OTP.");
                editText_otp6.requestFocus();
                return;
            } else {
                validate_otp();

            }
        } catch (Exception e) {
        }
    }
    //================================End Functions===========================================//


    public class OtpTextWatcher implements TextWatcher {
        private View view;

        private OtpTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.editText1:
                    if (text.length() == 1)
                        editText_otp2.requestFocus();
                    break;
                case R.id.editText2:
                    if (text.length() == 1)
                        editText_otp3.requestFocus();
                    else if (text.length() == 0)
                        editText_otp1.requestFocus();
                    break;
                case R.id.editText3:
                    if (text.length() == 1)
                        editText_otp4.requestFocus();
                    else if (text.length() == 0)
                        editText_otp2.requestFocus();
                    break;

                case R.id.editText4:
                    if (text.length() == 1)
                        editText_otp5.requestFocus();
                    else if (text.length() == 0)
                        editText_otp3.requestFocus();
                    break;
                case R.id.editText5:
                    if (text.length() == 1)
                        editText_otp6.requestFocus();
                    else if (text.length() == 0)
                        editText_otp4.requestFocus();
                    break;
                case R.id.editText6:
                    if (text.length() == 0)
                        editText_otp5.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    //================================Intent===========================================//


    //================================Api services============================================//

    public void validate_otp() {
        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.otp_id, Otp_id.trim());
                hashMap.put(Constants.otp,otp);


                Call<Object> call = RetrofitInstance.getdata().create(Api.class).validate_otp(hashMap);
                UtilityClass.showLoading(loader,OTPActivity.this);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        UtilityClass.hideLoading(loader,OTPActivity.this);
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
                                    new Message().showSnack(root_lay, "OTP did not matched.");

                                } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                    new Message().showSnack(root_lay, "This account has been blocked.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                    new Message().showSnack(root_lay, "This session has expired.");


                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                    new Message().showSnack(root_lay, "All fields not sent.");

                                }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                    new Message().showSnack(root_lay, " Please check the request method.");
                                }

                                else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);

                                    Intent in = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                                    in.putExtra("otpId", Otp_id);
                                    startActivity(in);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        new UtilityClass().alertBox(OTPActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                        UtilityClass.hideLoading(loader,OTPActivity.this);
                    }
                });
            }
            else {
                new Message().showSnack(root_lay,Constants.noInternetMessage);
            }

        } catch (Exception e) {
        }
    }


    public void timercount() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                resend.setText("Seconds remaining: " + millisUntilFinished / 1000);

            }

            public void onFinish() {
                String first = "Did not receive code?";
                String next = "<font color='#eab615'> Resend</font>";
                resend.setText(Html.fromHtml(first + next));
            }
        }.start();

    }


//...........................................aPI...........................//
    public void resendOtp() {
        try {
            if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.otp_id, Otp_id);
                hashMap.put(Constants.email_id, email);
                hashMap.put(Constants.contactno,contact);
                hashMap.put(Constants.isEmailUsed,isEmailUsed);



                Call<Object> call = RetrofitInstance.getdata().create(Api.class).resend_otp(hashMap);
                UtilityClass.showLoading(loader,OTPActivity.this);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            UtilityClass.hideLoading(loader,OTPActivity.this);
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

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OTPActivity.this);
                                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                                    alertDialogBuilder.setMessage("Please Check your OTP in Email.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            timercount();

                                        }
                                    });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                                UtilityClass.hideLoading(loader,OTPActivity.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        new UtilityClass().alertBox(OTPActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                        UtilityClass.hideLoading(loader,OTPActivity.this);
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