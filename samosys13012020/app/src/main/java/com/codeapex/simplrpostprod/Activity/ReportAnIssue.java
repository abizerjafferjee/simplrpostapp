package com.codeapex.simplrpostprod.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class ReportAnIssue extends AppCompatActivity {
    ImageView back_press;
    EditText etName,etEmail,etNumber,etDiscription;
    LinearLayout llBusiness,llissue;
    Button btnReport;
    TextView txtBusiness,txtIssue;
    String strAddressID,issueID, name, spinnerValue;
    ConstraintLayout root_lay;
    ProgressBar Loaderr;
    Spinner spinnerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_and_issue);
        findIDs();
    }

//===================================FIND VIEW IDS==================================//
    public void findIDs(){
        root_lay = findViewById(R.id.root_lay);
        Loaderr = findViewById(R.id.Loaderr);
        txtIssue = findViewById(R.id.txtIssue);
        txtBusiness = findViewById(R.id.txtBusiness);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etNumber = findViewById(R.id.etNumber);
        etDiscription = findViewById(R.id.etDiscription);
        llBusiness = findViewById(R.id.llBusiness);
        llissue = findViewById(R.id.llissue);
        btnReport = findViewById(R.id.btnReport);
        back_press = findViewById(R.id.back_press);
        spinnerPhone = findViewById(R.id.spinnerPhone);



        //Spinner
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


        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportAnIssue.super.onBackPressed();
            }
        });

        llBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                    Intent intent = new Intent(ReportAnIssue.this, SearchBusinessFragment.class);
                    intent.putExtra("from","ReportIssue");
                    startActivityForResult(intent,1);
                }else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }

            }
        });

        llissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                    Intent intent = new Intent(ReportAnIssue.this, IssueListActivity.class);
                    startActivityForResult(intent,2);
                }else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }


            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().trim().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter a name.");
                    etName.requestFocus();
                    return;

                }else if(etEmail.getText().toString().trim().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter a email.");
                    etEmail.requestFocus();
                    return;

                }else if(etNumber.getText().toString().trim().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter a number.");
                    etNumber.requestFocus();
                    return;
                }else if(etDiscription.getText().toString().trim().isEmpty()){
                    new Message().showSnack(root_lay, "Please enter description.");
                    etDiscription.requestFocus();
                    return;
                }else if(txtBusiness.getText().toString().trim().equals("Select")){
                    new Message().showSnack(root_lay, "Please select business type.");
                }else if(txtBusiness.getText().toString().trim().isEmpty()){
                    new Message().showSnack(root_lay, "Please select business type.");
                }else if(txtIssue.getText().toString().trim().equals("Select")){
                    new Message().showSnack(root_lay, "Please select issue.");
                }else {
                    if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                        View view = getWindow().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        submitReport();
                    }else {
                        new Message().showSnack(root_lay,Constants.noInternetMessage);
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            strAddressID = data.getStringExtra("addressID");
            String name = data.getStringExtra("name");
            txtBusiness.setText(name);

        }else if (resultCode == 2) {
            String issue = data.getStringExtra("issue");
            issueID = data.getStringExtra("issueID");
            txtIssue.setText(issue);

        }
    }


//===============================SUBMIT REPORT API===================================//

    public void submitReport() {
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId,"");
        try {
//            final String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.reporterUserId,struserId);
            hashMap.put(Constants.reporterName, etName.getText().toString().trim());
            hashMap.put(Constants.reporterEmailId, etEmail.getText().toString().trim());
            hashMap.put(Constants.reporterContactNumber,spinnerValue + etNumber.getText().toString().trim());
            hashMap.put(Constants.businessId, strAddressID);
            hashMap.put(Constants.issueId, issueID);
            hashMap.put(Constants.description, etDiscription.getText().toString().trim());
            UtilityClass.showLoading(Loaderr, ReportAnIssue.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).submitReport(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loaderr, ReportAnIssue.this);
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
                                new Message().showSnack(root_lay, "Email Id and password didn't matched.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                new Message().showSnack(root_lay, "This account does not exist.");


                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "Contact number already registered.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            }

                            else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);


                                AlertDialog.Builder alertBox = new AlertDialog.Builder(ReportAnIssue.this);
                                alertBox.setMessage("Your report submitted successfully.").setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        });
                                AlertDialog alert1 = alertBox.create();
                                alert1.setCancelable(false);
                                alert1.setTitle("Simplr Post");
                                alert1.show();

                            }
//                            loader.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    new UtilityClass().alertBox(ReportAnIssue.this, t.getLocalizedMessage(), Constants.someissuetitle);
                    UtilityClass.hideLoading(Loaderr, ReportAnIssue.this);
                }
            });
        } catch (Exception e) {

        }
    }
}
