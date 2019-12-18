package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import static android.content.ContentValues.TAG;
import static com.codeapex.simplrpostprod.RetrofitApi.Constants.listName;

public class AddSavedListActivity extends AppCompatActivity {
    ImageView imgBackButton;
    EditText edtAddListName;
    TextView txtToolbar;
    ConstraintLayout root_lay;
    ProgressBar loader;
    Button btnSubmit;
    String strUserId,strListId,strListname,strTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlist);
        loader = findViewById(R.id.Loader);
        imgBackButton = findViewById(R.id.back_press);
        txtToolbar = findViewById(R.id.toolbar_text);
        root_lay = findViewById(R.id.root_lay);
        btnSubmit = findViewById(R.id.submit_button_id);
        edtAddListName = findViewById(R.id.addlist_name);


        //................................Shared perfer/intent.....................//

        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        strUserId = preferences.getString(Constants.userId, "");


        if (getIntent().getExtras() != null) {
            if (!getIntent().hasExtra("isSavingProcess")) {
                strListId = getIntent().getExtras().getString("listId");
                strListname = getIntent().getExtras().getString("listname");
                strTitle = getIntent().getExtras().getString("toolbar_heading");
                txtToolbar.setText(strTitle);
                edtAddListName.setText(strListname);
            }
        }



        imgBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });




        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(AddSavedListActivity.this)) {
                    if(txtToolbar.getText().toString().trim().equals("Add List")){
                        Login();
                    }
                    else{
                        addEditList();
                    }
                }
                else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }


            }
        });
    }









    //====================================Functions===========================================//
    public void Login() {
        try {
            if (edtAddListName.getText().toString().trim().isEmpty()) {
                new Message().showSnack(root_lay, "Please add list name.");
                edtAddListName.requestFocus();
                return;
            }else {
                addSavedList();
            }
        } catch (Exception e) {
        }
    }

    //================================Api===========================================//

    public void addSavedList() {
        if (UtilityClass.isNetworkConnected(AddSavedListActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, strUserId);
            hashMap.put(listName,edtAddListName.getText().toString().trim());
            UtilityClass.showLoading(loader, AddSavedListActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).addSavedList(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("re_Response", new Gson().toJson(response.body()));
                    UtilityClass.hideLoading(loader, AddSavedListActivity.this);
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
                            final String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(AddSavedListActivity.this);
                            builder.setMessage("List has been added sucessfully.");

                            builder.setCancelable(false);
                            builder
                                    .setPositiveButton(
                                            "OK",
                                            new DialogInterface
                                                    .OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    Intent intent = new Intent();
                                                    if (getIntent().hasExtra("isSavingProcess")) {

                                                        intent.putExtra("list",result_data);
                                                        setResult(Activity.RESULT_OK,intent);
                                                    }
                                                    finish();
                                                }
                                            });


                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t);
                    UtilityClass.hideLoading(loader, AddSavedListActivity.this);
                }
            });
        }
        else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }

    }

    //================================Api===========================================//
    public void addEditList() {

        if (UtilityClass.isNetworkConnected(AddSavedListActivity.this)) {
            try {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, strUserId);
                hashMap.put(Constants.listId, strListId);
                hashMap.put(listName,edtAddListName.getText().toString().trim());
                UtilityClass.showLoading(loader, AddSavedListActivity.this);
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).editSavedList(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        try {
                            UtilityClass.hideLoading(loader, AddSavedListActivity.this);
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
                                AlertDialog.Builder builder
                                        = new AlertDialog
                                        .Builder(AddSavedListActivity.this);
                                builder.setMessage("List name has been updated successfully.");

                                builder.setCancelable(false);
                                builder
                                        .setPositiveButton(
                                                "OK",
                                                new DialogInterface
                                                        .OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        finish();
                                                    }
                                                });


                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t);
                        UtilityClass.hideLoading(loader, AddSavedListActivity.this);
                    }
                });
            } catch (Exception e) {

            }
        }
        else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }


    }

}