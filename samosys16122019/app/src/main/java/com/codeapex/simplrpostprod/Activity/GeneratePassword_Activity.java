package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class GeneratePassword_Activity extends AppCompatActivity {

    //.............GLOBAL vARIABLE..............//
    ImageView imgBackPress;
    LinearLayout root_lay;
    Button btn_submit;
    EditText edt_newPass,edt_confirmPass;
    Spinner spinnerPhone;
    String spinnerValue;
    String userId,mobileNumber;
    ImageView btn_eye,btn_eye2;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_password);

        userId = this.getIntent().getStringExtra("userId");
        mobileNumber = this.getIntent().getStringExtra("mobileNumber");
        //......................ALL IDS.................//
        imgBackPress = findViewById(R.id.back_press);
        root_lay = findViewById(R.id.root_lay);
        btn_submit = findViewById(R.id.btn_submit);
        edt_newPass = findViewById(R.id.edt_newPass);
        edt_confirmPass = findViewById(R.id.edt_confirmPass);
        btn_eye = findViewById(R.id.btn_eye);
        btn_eye2 = findViewById(R.id.btn_eye2);
        btn_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_newPass.getText().toString().length() > 0) {
                    if (v.getTag().toString().equals("close")) {
                        v.setTag("open");
                        btn_eye.setImageResource(R.drawable.eye_close);
                        edt_newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        v.setTag("close");
                        btn_eye.setImageResource(R.drawable.eye_icon);
                        edt_newPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            }
        });
        btn_eye2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_confirmPass.getText().toString().length() > 0) {
                    if (v.getTag().toString().equals("close")) {
                        v.setTag("open");
                        btn_eye2.setImageResource(R.drawable.eye_close);
                        edt_confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        v.setTag("close");
                        btn_eye2.setImageResource(R.drawable.eye_icon);
                        edt_confirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }
            }
        });

        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });

        edt_confirmPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if((actionId == EditorInfo.IME_ACTION_DONE ) || (actionId == EditorInfo.IME_ACTION_NEXT )) {
                    call_api();
                    return true;
                }else {
                    return false;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (edt_newPass.getText().toString().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter password.");
                    return;
                }else if (edt_newPass.getText().toString().trim().length() < 8) {
                   new Message().showSnack(root_lay, "Password should be 8 characters.");
                   edt_newPass.requestFocus();
                   return;
               } else if (edt_confirmPass.getText().toString().isEmpty()){
                   new Message().showSnack(root_lay, "Please enter confirm password.");
                   return;
               }else if (edt_confirmPass.getText().toString().trim().length() < 8) {
                   new Message().showSnack(root_lay, "Password should be 8 characters.");
                   edt_newPass.requestFocus();
                   return;
               } else if (!edt_newPass.getText().toString().equals(edt_confirmPass.getText().toString())){
                   new Message().showSnack(root_lay, "Password not matched.");
                   return;
               }else {
                   call_api();
                }
            }
        });
    }

    private void call_api() {
        Parser.callApi(GeneratePassword_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).GeneratePassword(userId,edt_newPass.getText().toString().trim()), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("New Pass response :", "" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString("status");
                    if (result_code.equals("1")) {
                        new Message().showSnackGreen(root_lay, "Password updated successfully for "+mobileNumber);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(GeneratePassword_Activity.this,SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        },1500);

                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, ""+jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
}

