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
import com.codeapex.simplrpostprod.Adapter.AdatperSharedWithMe;
import com.codeapex.simplrpostprod.Adapter.AdatperSharedWithOther;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.UserData;
import com.codeapex.simplrpostprod.ModelClass.UserModelReciepent;
import com.codeapex.simplrpostprod.ModelClass.publicModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
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
    TextView textone, texttwo,txtError;
    boolean checkList = false;
    String strAddressID, strIsPublic;
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
    List<UserData> publiclist=new ArrayList<>();

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
        txtError = findViewById(R.id.txtError);

        Loaderr = findViewById(R.id.Loaderr);
        root_lay = findViewById(R.id.root_lay);
        recyclerView = findViewById(R.id.recyclerview);
        recyclervieww = findViewById(R.id.recyclervieww);
        cardView = findViewById(R.id.cvFinalImage);
        relativeLayout = findViewById(R.id.toolbar_lay);
        //relativeLayout = findViewById(R.id.ok);
        textone = findViewById(R.id.txtCurrentPost);
        texttwo = findViewById(R.id.txtHistoryPost);

        cardView.setVisibility(View.GONE);

        textone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearanceone(textone, texttwo);
                checkList = true;
                recyclerView.setVisibility(View.GONE);
                recyclervieww.setVisibility(View.VISIBLE);
                if (publicModels.size() == 0) {
                    txtError.setText("No data found.");
                    txtError.setVisibility(View.VISIBLE);
                } else {

                    txtError.setText("No data found.");
                    txtError.setVisibility(View.GONE);
                }
            }
        });

        texttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearance(texttwo, textone);

                recyclerView.setVisibility(View.VISIBLE);
                recyclervieww.setVisibility(View.GONE);
                if (userModelReciepents.size() == 0) {
                    txtError.setText("No user found.");
                    txtError.setVisibility(View.VISIBLE);
                } else {

                    txtError.setText("No data found.");
                    txtError.setVisibility(View.GONE);
                }

                checkList = false;
            }
        });

        managerPublic = new LinearLayoutManager(this);

        recyclervieww.setLayoutManager(managerPublic);
        recyclervieww.setAdapter(adapter);

        managerUser = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(managerUser);
        recyclerView.setAdapter(adapteruser);

        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }


        });

        //getPublicSavedAddress();
        getSharedWithBusiness();

    }

    @Override
    public void onBackPressed() {

        finish();

    }

    public void changeTextApearance(TextView txtChange, TextView txtChange2nd) {
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.white);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }

    public void changeTextApearanceone(TextView txtChange, TextView txtChange2nd) {
        txtChange.setBackgroundResource(R.color.colorPrimaryDark);
        txtChange.setTextColor(Color.WHITE);
        txtChange2nd.setBackgroundResource(R.drawable.
                whitetwo);
        txtChange2nd.setTextColor(Color.parseColor("#1bac71"));
    }

    public void getSharedWithUser() {

        if (UtilityClass.isNetworkConnected(SharedListActivity.this)) {

            Parser.callApi(SharedListActivity.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getPublicSharedSenderList(preferences.getString(Constants.userId,""),strAddressID,"public"), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("shared with user", "response :" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result_code = jsonObject.getString("resultCode");
                        if (result_code.equals("1")) {
                            JSONArray resultData = jsonObject.optJSONArray("resultData");
                            for (int i = 0; i < resultData.length(); i++) {
                                JSONObject object = resultData.optJSONObject(i);
                                String userId = object.optString("userId");
                                String profilePicURL = object.optString("profilePicURL");
                                String name = object.optString("name");
                                String emailId = object.optString("emailId");
                                String contactNumber = object.optString("contactNumber");
                                String status = object.optString("status");
                                String id = object.optString("recordId");

                                //publiclist.add(new UserData(id,"private",userId, profilePicURL, name, emailId, contactNumber, status));

                            }

                        } else if (result_code.equals("0")) {
                            //new Message().showSnack(root_lay, "" + jsonObject.optString("data"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }


//    }
    }

    public void getSharedWithBusiness() {

        Parser.callApi(SharedListActivity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getPrivateSharedSenderList(preferences.getString(Constants.userId,""),strAddressID,"private"), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("shared with business", "response :" + response);

                if (!publiclist.isEmpty()){
                    publiclist.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("resultCode");
                    if (result_code.equals("1")) {
                        JSONArray resultData = jsonObject.optJSONArray("resultData");
                        for (int i = 0; i < resultData.length(); i++) {
                            JSONObject object = resultData.optJSONObject(i);

                            String userId = object.optString("userId");
                            String profilePicURL = object.optString("profilePicURL");
                            String name = object.optString("name");
                            String emailId = object.optString("emailId");
                            String contactNumber = object.optString("contactNumber");
                            String status = object.optString("status");
                            String id = object.optString("recordId");

                            publiclist.add(new UserData(id,"private",userId, profilePicURL, name, emailId, contactNumber, status));

                        }

                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SharedListActivity.this);
                    recyclervieww.setLayoutManager(linearLayoutManager);
                    recyclervieww.setHasFixedSize(true);

                    AdatperSharedWithOther _adaterFriends = new AdatperSharedWithOther(SharedListActivity.this, publiclist);
                    recyclervieww.setAdapter(_adaterFriends);

                    if (publiclist.isEmpty()){
                        txtError.setText("No data found.");
                        txtError.setVisibility(View.VISIBLE);
                    }else {
                        txtError.setText("No data found.");
                        txtError.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
