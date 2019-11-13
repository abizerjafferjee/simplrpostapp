package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdaterShardlistPrivate;
import com.codeapex.simplrpostprod.Adapter.AdaterSharedPubliccList;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.UserModelReciepent;
import com.codeapex.simplrpostprod.ModelClass.publicModel;
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
public class SharedListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView recyclervieww;
    CardView cardView;
    RelativeLayout relativeLayout;
    ImageView imageView;
    TextView textone, texttwo;
    ScrollView scrollViewone,scrollViewtwo;
    boolean checkList = false;
    String strAddressID,strIsPublic;
    SharedPreferences preferences;
    ConstraintLayout root_lay;
    private List<publicModel> publicModels = new ArrayList<>();
    private List<UserModelReciepent> userModelReciepents = new ArrayList<>();
    LinearLayoutManager managerPublic;
    LinearLayoutManager managerUser;
    AdaterSharedPubliccList adapter;
    AdaterShardlistPrivate adapteruser;
    AlertDialog.Builder alertBox;
    ProgressBar Loaderr;

    ConstraintLayout consEmptyScreen;
    ImageView imdError;
    TextView txtError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_list);
        if (getIntent().hasExtra("addressID") && getIntent().hasExtra("isPublic")) {
            strAddressID = getIntent().getStringExtra("addressID");
            strIsPublic = getIntent().getStringExtra("isPublic");
        }

        preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        alertBox = new AlertDialog.Builder(SharedListActivity.this);
        alertBox.setCancelable(false);
        /*Toast.makeText(this, ""+strAddressID+"   "+strIsPublic, Toast.LENGTH_SHORT).show();*/
        consEmptyScreen = findViewById(R.id.consEmptyScreen);
        imdError = findViewById(R.id.imdError);
        txtError = findViewById(R.id.txtError);

        Loaderr = findViewById(R.id.Loaderr);
        root_lay =findViewById(R.id.root_lay);
        recyclerView =findViewById(R.id.recyclerview);
        recyclervieww = findViewById(R.id.recyclervieww);
        scrollViewone =findViewById(R.id.publicdefualt);
        scrollViewtwo = findViewById(R.id.privatedeault);
        cardView = findViewById(R.id.cvFinalImage);
        relativeLayout = findViewById(R.id.toolbar_lay);
        //relativeLayout = findViewById(R.id.ok);
        textone = findViewById(R.id.txtCurrentPost);
        texttwo = findViewById(R.id.txtHistoryPost);

        textone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearanceone(textone,texttwo);
                checkList = true;
                recyclerView.setVisibility(View.GONE);
                recyclervieww.setVisibility(View.VISIBLE);
                if(publicModels.size() == 0){
                    consEmptyScreen.setVisibility(View.VISIBLE);
                    imdError.setImageResource(R.drawable.publicdefault);
                    txtError.setText("No public location found.");
                }else {
                    consEmptyScreen.setVisibility(View.GONE);
                }

            }
        });

        texttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearance(texttwo,textone);

                recyclerView.setVisibility(View.VISIBLE);
                recyclervieww.setVisibility(View.GONE);
                if(userModelReciepents.size() == 0){
                    consEmptyScreen.setVisibility(View.VISIBLE);
                    imdError.setImageResource(R.drawable.privatedefault);
                    txtError.setText("No user found.");
                }else {
                    consEmptyScreen.setVisibility(View.GONE);
                }

                checkList = false;
            }
        });

        managerPublic = new LinearLayoutManager(this);
        adapter = new AdaterSharedPubliccList(this, publicModels, new AdaterSharedPubliccList.OnItemClickListener() {
            @Override
            public void onItemClick(final com.codeapex.simplrpostprod.ModelClass.publicModel item, int position) {

                if(item.getType().equals("clicked")){
                    unshareAddressWithBusiness(item.getAddressId(),position);
                }
            }
        });
        recyclervieww.setLayoutManager(managerPublic);
        recyclervieww.setAdapter(adapter);

        managerUser = new LinearLayoutManager(this);
        adapteruser = new AdaterShardlistPrivate(this, userModelReciepents, new AdaterShardlistPrivate.OnItemClickListener() {
            @Override
            public void onItemClick(final UserModelReciepent item, int position) {
                if(item.getType().equals("clicked")){
                    unshareAddressWithUser(item.getUserId(),position);

                }
            }
        });
        recyclerView.setLayoutManager(managerUser);
        recyclerView.setAdapter(adapteruser);

        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }


        });

        getPublicAddresses();

    }
    @Override
    public void onBackPressed() {

        finish();

    }


    public void changeTextApearance(TextView txtChange,TextView txtChange2nd){
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.white);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }


    public void changeTextApearanceone(TextView txtChange,TextView txtChange2nd){
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.
                whitetwo);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }




    public void getPublicAddresses() {
        if (UtilityClass.isNetworkConnected(SharedListActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
            hashMap.put(Constants.addressId, strAddressID);
            hashMap.put(Constants.isPublic, strIsPublic);
            UtilityClass.showLoading(Loaderr,SharedListActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getReceipientList(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    Log.e("re_Response", new Gson().toJson(response.body()));
                    UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
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


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                            new Message().showSnack(root_lay, "List does not exist.");

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
                            Log.d(TAG, "tanunnnnnnnnnnn: " + response);

                            JSONObject jsonObject1 = new JSONObject(result_data);

                            if(jsonObject1.has("publicAddresses")){
                                String publicAddresses = jsonObject1.getString("publicAddresses");
                                JSONArray jsonArray = new JSONArray(publicAddresses);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String addressId = jsonObject2.getString("addressId");
                                    String pictureURL = jsonObject2.getString("pictureURL");
                                    String shortName = jsonObject2.getString("shortName");
                                    String plusCode = jsonObject2.getString("plusCode");
                                    String categoryName = jsonObject2.getString("categoryName");
                                    String addressReferenceId = jsonObject2.getString("addressReferenceId");
                                    String description = jsonObject2.getString("description");

                                    publicModel model = new publicModel();
                                    model.setAddressId(addressId);
                                    model.setAddressReferenceId(addressReferenceId);
                                    model.setCategoryName(categoryName);
                                    model.setDescription(description);
                                    model.setPictureURL(Constants.IMG_URL+pictureURL);
                                    model.setPlusCode(plusCode);
                                    model.setShortName(shortName);
                                    publicModels.add(model);

                                }
                                adapter.notifyDataSetChanged();
                            }

                            if(jsonObject1.has("user")){
                                String privateAddresses = jsonObject1.getString("user");
                                JSONArray jsonArrayy = new JSONArray(privateAddresses);

                                for (int i = 0; i < jsonArrayy.length(); i++) {
                                    JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                    String userID = jsonObject2.getString("userId");
                                    String pictureURL = jsonObject2.getString("profilePicURL");
                                    String username = jsonObject2.getString("userName");
                                    String name = jsonObject2.getString("name");
                                    UserModelReciepent model = new UserModelReciepent();
                                    model.setName(name);
                                    model.setProfilePicURL(Constants.IMG_URL+pictureURL);
                                    model.setUserName(username);
                                    model.setUserId(userID);
                                    userModelReciepents.add(model);

                                }
                                adapteruser.notifyDataSetChanged();
                            }
                        }

                        if(publicModels.size() == 0){
                            consEmptyScreen.setVisibility(View.VISIBLE);
                            imdError.setImageResource(R.drawable.publicdefault);
                            txtError.setText("No public location found.");
                        }else {
                            consEmptyScreen.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }

    }



    public void unshareAddressWithBusiness(String receiverID, final int posi){
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
            String struserId = preferences.getString(Constants.userId,"");

            if (UtilityClass.isNetworkConnected(SharedListActivity.this)) {
                try {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Constants.userId,struserId);
                    hashMap.put(Constants.receiverId, receiverID);
                    hashMap.put(Constants.addressId, strAddressID);
                    hashMap.put(Constants.isAddressPublic,strIsPublic);

                    UtilityClass.showLoading(Loaderr,SharedListActivity.this);


                    Call<Object> call = RetrofitInstance.getdata().create(Api.class).unshareAddressWithBusiness(hashMap);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
                            Log.e("unreB_Response", new Gson().toJson(response.body()));
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
                                        new Message().showSnack(root_lay, "No data found");

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
                                        consEmptyScreen.setVisibility(View.GONE);
                                        if(publicModels.size() == 1){
                                            adapter.removeItem(posi);
                                            consEmptyScreen.setVisibility(View.VISIBLE);
                                            imdError.setImageResource(R.drawable.publicdefault);
                                            txtError.setText("No public location found.");
                                        }else {
                                            adapter.removeItem(posi);
                                            consEmptyScreen.setVisibility(View.GONE);
                                        }

                                    }

//                                loader.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            new UtilityClass().alertBox(SharedListActivity.this, t.getLocalizedMessage(), t.getLocalizedMessage());
                            UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
                        }
                    });
                } catch (Exception e) {

                }
            }
            else {
                new Message().showSnack(root_lay,Constants.noInternetMessage);
            }



        }




        public void unshareAddressWithUser(String receiverID, final int posi){
        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
            String struserId = preferences.getString(Constants.userId,"");
            if (UtilityClass.isNetworkConnected(SharedListActivity.this)) {
                try {

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Constants.userId,struserId);
                    hashMap.put(Constants.receiverId, receiverID);
                    hashMap.put(Constants.addressId, strAddressID);
                    hashMap.put(Constants.isAddressPublic,strIsPublic);

                    UtilityClass.showLoading(Loaderr,SharedListActivity.this);

                    Call<Object> call = RetrofitInstance.getdata().create(Api.class).unshareAddressWithUser(hashMap);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
                            Log.e("unreU_Response", new Gson().toJson(response.body()));
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
                                        new Message().showSnack(root_lay, "No data found");


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
                                        if(userModelReciepents.size() == 1){
                                            adapteruser.removeItem(posi);
                                            consEmptyScreen.setVisibility(View.VISIBLE);
                                            imdError.setImageResource(R.drawable.privatedefault);
                                        }else {
                                            adapteruser.removeItem(posi);
                                            consEmptyScreen.setVisibility(View.GONE);
                                        }
                                    }
//                                loader.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            new UtilityClass().alertBox(SharedListActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);
                            UtilityClass.hideLoading(Loaderr,SharedListActivity.this);
                        }
                    });
                } catch (Exception e) {

                }
            }
            else {
                new Message().showSnack(root_lay,Constants.noInternetMessage);
            }

        }



}
