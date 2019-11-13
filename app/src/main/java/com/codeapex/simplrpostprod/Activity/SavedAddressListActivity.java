package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codeapex.simplrpostprod.Adapter.AdaterSavedAddrressList;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SavedAddressListInterface;
import com.codeapex.simplrpostprod.ModelClass.SavedlistModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class SavedAddressListActivity extends AppCompatActivity implements SavedAddressListInterface {

    RecyclerView recyclerGetsavedList;
    ImageView imgBack,imgAddLocation;
    RelativeLayout relativeLayout;
    String user_Id;
    List<SavedlistModel> models;
    boolean isSavingProcess ;
    ProgressBar loader;
    SavedAddressListInterface listInterface;
    ConstraintLayout root_lay;

            @Override
            public void onResume(){
                super.onResume();
                getSavedList();
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_savedlist);
                imgAddLocation = findViewById(R.id.image);
                imgBack = findViewById(R.id.back_press);
                loader = findViewById(R.id.Loader);
                root_lay = findViewById(R.id.root_lay);
                listInterface = this;
                recyclerGetsavedList = findViewById(R.id.recyclerview);

                SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                user_Id = preferences.getString(Constants.userId, "");
                Log.d(TAG, "onCreateView: " + user_Id);

           //     getSavedList();


                if (getIntent().getExtras() != null) {
                    isSavingProcess = getIntent().getExtras().getBoolean("isSavingProcess");
                }

                imgAddLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddSavedListActivity.class);
                        intent.putExtra("isSavingProcess",isSavingProcess);
                        startActivityForResult(intent,101);
                    }
                });





                imgBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                finish();
                    }


                });

            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (isSavingProcess) {
                String lst = data.getStringExtra("list");
                Intent intent = new Intent();
                intent.putExtra("code", data.getStringExtra("list"));
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }

    //.............................................Api.........................................//
    public void getSavedList() {
                if (UtilityClass.isNetworkConnected(SavedAddressListActivity.this)) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    models = new ArrayList<>();
                    hashMap.put(Constants.userId, user_Id);
                    Log.d(TAG, "getPrivateAddresses: " + user_Id);
                    UtilityClass.showLoading(loader, SavedAddressListActivity.this);

                    Call<Object> call = RetrofitInstance.getdata().create(Api.class).getSavedList(hashMap);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            Log.d(TAG, "nbewwww: " + response);
                            UtilityClass.hideLoading(loader, SavedAddressListActivity.this);

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

                                    JSONArray jsonArray = new JSONArray(result_data);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                        String listId = jsonObject1.getString("listId");

                                        String listName = jsonObject1.getString("listName");
                                        String isDefault = jsonObject1.getString("isDefault");
                                        Log.d(TAG, "onResponsesavee: "+listId+listName);

//
                                        SavedlistModel resultPrivateAddress = new SavedlistModel(listId, listName, isDefault);
                                        models.add(resultPrivateAddress);
                                    }
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerGetsavedList.setLayoutManager(linearLayoutManager);
                                    recyclerGetsavedList.setHasFixedSize(true);

                                    AdaterSavedAddrressList custom_adaterFriends = new AdaterSavedAddrressList(getApplicationContext(), models,isSavingProcess,listInterface);
                                    recyclerGetsavedList.setAdapter(custom_adaterFriends);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t);


                        }
                    });
                }
                else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }



//    }
    }

    @Override
    public void listCallBackMethod(String listID) {
        Intent intent = new Intent();
        intent.putExtra("code", listID);
        setResult(RESULT_OK, intent);
        finish();
    }


}
