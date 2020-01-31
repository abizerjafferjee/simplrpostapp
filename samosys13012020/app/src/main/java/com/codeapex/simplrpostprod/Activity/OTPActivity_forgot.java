package com.codeapex.simplrpostprod.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class OTPActivity_forgot extends AppCompatActivity {
    Button btn_verify;
    ImageView ImgbackPress;
    ProgressBar loader;
    TextView resend, txt_mobileNumber;
    String otp;
    LinearLayout layout_dialog;
    EditText editText_otp1, editText_otp2, editText_otp3, editText_otp4;
    FrameLayout root_lay;
    ACProgressFlower progressbar;
    String userId, mobileNumber, otpId,otpType;
    String from = "";
    String email;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpId = this.getIntent().getStringExtra("otpId");
        otpType = this.getIntent().getStringExtra("otpType");
        mobileNumber = this.getIntent().getStringExtra("mobileNumber");
        userId = this.getIntent().getStringExtra("userId");
        from = this.getIntent().getStringExtra("from");
        email = this.getIntent().getStringExtra("email");

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

        if (email != null && from.equals("email")) {
            txt_mobileNumber.setText(email);
        } else if (email != null && from.equals("email_edit")) {
            txt_mobileNumber.setText(email);
        } else {
            txt_mobileNumber.setText(mobileNumber);
            //layout_dialog.setVisibility(View.VISIBLE);
            //progressbar = Parser.initProgressDialog(this, "");
        }

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

                if (from.equals("email") || from.equals("email_edit")) {

                    Parser.callApi(OTPActivity_forgot.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).resend_otp_for_email_varification(otpType,userId), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Forgot 1 In response::", "" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String result_code = jsonObject.getString("resultCode");
                                if (result_code.equals("1")) {
                                    JSONObject data = jsonObject.optJSONObject("response");

                                    otpId = data.optString("otpId");
                                    otpType = data.optString("type");

                                    new Message().showSnackGreen(root_lay, "OTP send successfully!");

                                } else if (result_code.equals("0")) {
                                    new Message().showSnack(root_lay, "Email not registered!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    /*layout_dialog.setVisibility(View.VISIBLE);
                    progressbar = Parser.initProgressDialog(OTPActivity_forgot.this, "");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout_dialog.setVisibility(View.GONE);
                            if (progressbar != null) {
                                progressbar.dismiss();
                            }
                        }
                    }, 5000);
*/
                    Parser.callApi(OTPActivity_forgot.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).resend_otp(mobileNumber,otpType), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("OTP verify response::", "" + mobileNumber + response);
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
                                    new Message().showSnack(root_lay, "OTP entered is incorrect.");
                                } else if (jsonObject.optString("resultData").equals("Contact number not registerd ")) {
                                    new Message().showSnack(root_lay, "Contact number not registerd.");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit. \nif you go back, Process will end.", Toast.LENGTH_SHORT).show();
        new Message().showSnack(root_lay, "Click BACK again to exit.\nif you go back, process will end.");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        if (from.equals("email_edit")) {
            Parser.callApi(OTPActivity_forgot.this, "verifying OTP...", true, ApiClient.getClient().create(ApiInterface.class).validate_otp_email_edit(otpId,otp,otpType), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("OTP verify response::", "" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals("1")) {
                            if (from.equals("email_edit")) {
                                //new Message().showSnackGreen(root_lay, "Email verified successfully");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        final Dialog dialog = new Dialog(OTPActivity_forgot.this, R.style.DialogSlideAnim);
                                        LayoutInflater inflater = getLayoutInflater();
                                        View view = inflater.inflate(R.layout.edit_confirmation_dialog, null);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.setCancelable(false);
                                        dialog.setContentView(view);

                                        TextView btn_yes = view.findViewById(R.id.btn_yes);
                                        TextView btn_no = view.findViewById(R.id.btn_no);

                                        btn_yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(OTPActivity_forgot.this, AddLocation_Activity.class)
                                                        .putExtra("address_id", "")
                                                        .putExtra("user_id", "")
                                                        .putExtra("profile_img", "")
                                                        .putExtra("user_name", "")
                                                        .putExtra("plus_code", "")
                                                        .putExtra("public_private_tag", "")
                                                        .putExtra("qr_code_img", "")
                                                        .putExtra("street_img", "")
                                                        .putExtra("building_img", "")
                                                        .putExtra("entrance_img", "")
                                                        .putExtra("address_unique_link", "")
                                                        .putExtra("country", "")
                                                        .putExtra("city", "")
                                                        .putExtra("street", "")
                                                        .putExtra("building", "")
                                                        .putExtra("entrance", "")
                                                        .putExtra("latitude", "")
                                                        .putExtra("longitude", "")
                                                        .putExtra("direction_txt", "")
                                                        .putExtra("from", "add"));
                                            }
                                        });

                                        btn_no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(OTPActivity_forgot.this, Home_Activity_new.class)
                                                        .putExtra("userId", userId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                finish();
                                            }
                                        });

                                        dialog.show();

                                    }
                                }, 300);

                            } else {
                                /*startActivity(new Intent(OTPActivity_forgot.this, GeneratePassword_Activity.class).putExtra("userId", userId).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();*/
                            }
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "OTP entered is incorrect.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Parser.callApi(OTPActivity_forgot.this, "verifying OTP...", true, ApiClient.getClient().create(ApiInterface.class).validate_otp(otpId, otp, otpType), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("pwd verify response::", "" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals("1")) {
                            if (from.equals("email_edit")) {

                            } else {
                                startActivity(new Intent(OTPActivity_forgot.this, GeneratePassword_Activity.class).putExtra("userId", userId).putExtra("mobileNumber", mobileNumber).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "OTP entered is incorrect.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}