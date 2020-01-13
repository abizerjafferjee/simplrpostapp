package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.SmsListener;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.SmsReceiver;

import org.json.JSONException;
import org.json.JSONObject;
import cc.cloudist.acplibrary.ACProgressFlower;

public class OTPActivity extends AppCompatActivity {
    Button btn_verify;
    ImageView ImgbackPress;
    ProgressBar loader;
    TextView resend, txt_mobileNumber;
    String otp, otpType;
    LinearLayout layout_dialog;
    EditText editText_otp1, editText_otp2, editText_otp3, editText_otp4;
    FrameLayout root_lay;
    ACProgressFlower progressbar;
    String userId, mobileNumber, otpId, from;
    String intent_data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpId = this.getIntent().getStringExtra("otp_id");
        otpType = this.getIntent().getStringExtra("otpType");
        mobileNumber = this.getIntent().getStringExtra("mobile");
        intent_data = this.getIntent().getStringExtra("intent_data");

        editText_otp1 = findViewById(R.id.editText1);
        editText_otp2 = findViewById(R.id.editText2);
        editText_otp3 = findViewById(R.id.editText3);
        editText_otp4 = findViewById(R.id.editText4);
        layout_dialog = findViewById(R.id.layout_dialog);
        txt_mobileNumber = findViewById(R.id.txt_mobileNumber);
        root_lay = findViewById(R.id.root_lay);
        loader = findViewById(R.id.Loader);
        resend = findViewById(R.id.resend);
        btn_verify = findViewById(R.id.btn_verify);

        txt_mobileNumber.setText(mobileNumber);
        /*layout_dialog.setVisibility(View.VISIBLE);
        progressbar = Parser.initProgressDialog(OTPActivity.this, "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout_dialog.setVisibility(View.GONE);
                if (progressbar != null) {
                    progressbar.dismiss();
                }
            }
        }, 5000);*/

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otp = editText_otp1.getText().toString().trim() + editText_otp2.getText().toString().trim() + editText_otp3.getText().toString().trim() + editText_otp4.getText().toString().trim();
                otp = editText_otp1.getText().toString().trim() + editText_otp2.getText().toString().trim() + editText_otp3.getText().toString().trim() + editText_otp4.getText().toString().trim();
                OtpEnter();
            }
        });

        editText_otp1.addTextChangedListener(new OtpTextWatcher(editText_otp1));
        editText_otp2.addTextChangedListener(new OtpTextWatcher(editText_otp2));
        editText_otp3.addTextChangedListener(new OtpTextWatcher(editText_otp3));
        editText_otp4.addTextChangedListener(new OtpTextWatcher(editText_otp4));
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
//                layout_dialog.setVisibility(View.VISIBLE);
//                progressbar = Parser.initProgressDialog(OTPActivity.this, "");

               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout_dialog.setVisibility(View.GONE);
                        if (progressbar != null) {
                            progressbar.dismiss();
                        }
                    }
                }, 5000);*/

                Parser.callApi(OTPActivity.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).resend_otp(mobileNumber, otpType), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("OTP verify response::", "" + response);
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
                                }
                                new Message().showSnackGreen(root_lay, "OTP send successfully!");
                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                                new Message().showSnack(root_lay, "OTP not send, try again.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        /*SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                otp = messageText;
                Log.e("OTP:", "OTP __ " + messageText);
                char one = otp.charAt(0);
                char two = otp.charAt(1);
                char three = otp.charAt(2);
                char four = otp.charAt(3);
                editText_otp1.setText("" + one);
                editText_otp2.setText("" + two);
                editText_otp3.setText("" + three);
                editText_otp4.setText("" + four);

                layout_dialog.setVisibility(View.GONE);
                if (progressbar != null) {
                    progressbar.dismiss();
                }
            }
        });*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

   //====================================Functions===========================================//
    public void OtpEnter() {

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
        } else {
            validate_otp();
        }
    }

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
                    if (text.length() == 1) {
                        editText_otp4.requestFocus();
                        editText_otp4.setSelection(editText_otp4.getText().length());
                    } else if (text.length() == 0)
                        editText_otp2.requestFocus();
                    break;
                case R.id.editText4:
                    if (text.length() == 0)
                        editText_otp3.requestFocus();
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

    public void validate_otp() {

        Parser.callApi(OTPActivity.this, "verifying OTP...", true, ApiClient.getClient().create(ApiInterface.class).validate_otp(otpId, otp, otpType), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("OTP verify response::", "" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals("1")) {
                        JSONObject object = jsonObject.optJSONObject("response");
                        String userId = object.optString("userId");
                        SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.isUserLoggedIn, true);
                        editor.putString(Constants.userId, userId);
                        editor.putString(Constants.contactNumber, mobileNumber);
                        editor.commit();

                        startActivity(new Intent(OTPActivity.this, Home_Activity_new.class).putExtra("userId", userId)
                                .putExtra("intent_data",intent_data).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        new Message().showSnack(root_lay, "OTP entered is incorrect.");
                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, "" + jsonObject.getString("resultData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}