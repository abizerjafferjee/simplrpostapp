package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

public class Security_Activity extends AppCompatActivity {

    //.............GLOBAL vARIABLE..............//
    ImageView imgBackPress;
    LinearLayout root_lay;
    TextView btn_submit,txt_question;
    EditText edt_answer;
    Spinner spinnerPhone;
    String spinnerValue;
    String Question,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        Question = this.getIntent().getStringExtra("question");
        userId = this.getIntent().getStringExtra("userId");
        //......................ALL IDS.................//
        imgBackPress = findViewById(R.id.back_press);
        root_lay = findViewById(R.id.root_lay);
        btn_submit = findViewById(R.id.btn_submit);
        edt_answer = findViewById(R.id.edt_answer);
        txt_question = findViewById(R.id.txt_question);
        imgBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });

        getQuestion();

        edt_answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

               if (edt_answer.getText().toString().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter your answer.");
                    return;
                }else {
                    call_api();
                }
            }
        });
    }

    private void call_api() {
        Parser.callApi(Security_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).submit_security_question(userId,edt_answer.getText().toString().trim()), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Verify answer res :", ""+userId+edt_answer.getText().toString().trim()+"\n" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString("status");
                    if (result_code.equals("1")) {

                        startActivity(new Intent(Security_Activity.this,GeneratePassword_Activity.class)
                                .putExtra("userId",userId)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, "Answer not registered.");
                    }else if (result_code.equals("2")) {
                        new Message().showSnack(root_lay, "Answer not matched, try again.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getQuestion(){
        Parser.callApi(Security_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getQuestion(userId), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Question response::", ""+userId + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("status");
                    if (result_code.equals("1")){
                        String result_data = jsonObject.getString("data");
                        JSONObject jsonObject1 = new JSONObject(result_data);
                        String security_question = jsonObject1.optString("security_question");
                        if (security_question!=null && !security_question.isEmpty()) {
                            char num = security_question.charAt(0);
                            txt_question.setText("" + security_question.replace("" + num, ""));
                            txt_question.setTextColor(Color.BLACK);
                            edt_answer.setVisibility(View.VISIBLE);
                            btn_submit.setVisibility(View.VISIBLE);
                        }else {
                            txt_question.setText("You have not added Security question.");
                            txt_question.setTextColor(Color.RED);
                            edt_answer.setVisibility(View.GONE);
                            btn_submit.setVisibility(View.GONE);

                        }


                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)){
                        // new Message().showSnack(root_lay, "OTP entered is incorrect.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

