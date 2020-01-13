package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeapex.simplrpostprod.Adapter.AdaptorAddresses;
import com.codeapex.simplrpostprod.Adapter.AdaterSharedAndSavedAddresses;

import com.codeapex.simplrpostprod.Adapter.AdatperSavedAddresses;
import com.codeapex.simplrpostprod.Adapter.AdatperSharedWithMe;
import com.codeapex.simplrpostprod.Adapter.SearchBussinessAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.PublicAddress;
import com.codeapex.simplrpostprod.ModelClass.savedItems;
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
import static android.content.Context.MODE_PRIVATE;


public class SavedAddressListFragment extends Fragment implements SaveUnsaveAddress, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    RecyclerView recyclervieww;
    CardView cardView;
    RelativeLayout relativeLayout;
    ImageView imageView;
    List<savedItems> publiclist;
    List<ModelResultPrivateAddress> privatelist;
    TextView textone, texttwo;
    String listId, listname, user_Id;
    SaveUnsaveAddress saveInterface;
    ProgressBar loader;
    Call<Object> call;
    LinearLayout root_lay;
    TextView txtTitle;
    boolean checkList = false;
    TextView txtError;
    String strErrorMsg;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_recycler,container,false);

        txtError = view.findViewById(R.id.txtError);
        recyclerView = view.findViewById(R.id.recyclerview);
        loader = view.findViewById(R.id.Loader);
        root_lay = view.findViewById(R.id.root_lay);
        recyclervieww = view.findViewById(R.id.recyclervieww);
        cardView = view.findViewById(R.id.cvFinalImage);
        relativeLayout = view.findViewById(R.id.toolbar_lay);

        textone = view.findViewById(R.id.txtCurrentPost);
        texttwo = view.findViewById(R.id.txtHistoryPost);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);

        swipeRefreshLayout.setOnRefreshListener(this);

        saveInterface = this;

        listId = getActivity().getIntent().getStringExtra("listIddd");
        listname = getActivity().getIntent().getStringExtra("listname");

        SharedPreferences preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        Log.d(TAG, "onCreate: " + listId + user_Id);
        publiclist = new ArrayList<>();
        privatelist = new ArrayList<>();

        textone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearanceone(textone, texttwo);
                checkList = true;
                recyclerView.setVisibility(View.VISIBLE);
                recyclervieww.setVisibility(View.GONE);
                if (publiclist.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);

                }
            }
        });

        texttwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextApearance(texttwo, textone);
                recyclerView.setVisibility(View.GONE);
                recyclervieww.setVisibility(View.VISIBLE);
                if (privatelist.isEmpty()) {
                    recyclervieww.setVisibility(View.GONE);
                    txtError.setText(strErrorMsg + "private location found");
                }
                checkList = false;
            }
        });

        if (UtilityClass.isNetworkConnected(getActivity())) {
            getSharedWithUser();
        }else {
            new Message().showSnack(view.findViewById(R.id.root_lay), Constants.noInternetMessage);
        }
        return view;
    }

    @Override
    public void onRefresh() {
        if (!publiclist.isEmpty()){
            publiclist.clear();
        }
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AdatperSavedAddresses _adaterFriends = new AdatperSavedAddresses(getActivity(), publiclist, saveInterface, listId, true);
        recyclerView.setAdapter(_adaterFriends);
        getSharedWithUser();

        swipeRefreshLayout.setRefreshing(false);
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

        if (UtilityClass.isNetworkConnected(getActivity())) {

            Parser.callApi(getActivity(), "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getSavedAddress(user_Id,"public"), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("shared with user", "response :" + response);

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

                                String addressId = object.optString("addressId");
                                if (addressId.equals("null")){
                                    addressId = "";
                                }
                                String userId = object.optString("userId");
                                String profilePicURL = object.optString("profilePicURL");
                                String userName = object.optString("name");
                                if (userName.equals("null")){
                                    userName = "";
                                }
                                String address_tag = object.optString("address_tag");
                                if (address_tag.equals("null")){
                                    address_tag = "";
                                }
                                String latitude = object.optString("latitude");
                                String longitude = object.optString("longitude");
                                String plus_code = object.optString("plus_code");
                                if (plus_code.equals("null")){
                                    plus_code = object.optString("plusCode");
                                }
                                String unique_link = object.optString("unique_link");
                                if (unique_link.equals("null")){
                                    unique_link = "";
                                }
                                String country = object.optString("country");
                                if (country.equals("null")){
                                    country = "";
                                }
                                String city = object.optString("city");
                                if (city.equals("null")){
                                    city = "";
                                }
                                String street_name = object.optString("street_name");
                                if (street_name.equals("null")){
                                    street_name = "";
                                }
                                String building_name = object.optString("building_name");
                                if (building_name.equals("null")){
                                    building_name = "";
                                }
                                String entrance_name = object.optString("entrance_name");
                                if (entrance_name.equals("null")){
                                    entrance_name = "";
                                }
                                String direction_text = object.optString("direction_text");
                                if (direction_text.equals("null")){
                                    direction_text = "";
                                }
                                String street_image = object.optString("street_image");
                                if (street_image.equals("null")){
                                    street_image = "";
                                }
                                String building_image = object.optString("building_image");
                                if (building_image.equals("null")){
                                    building_image = "";
                                }
                                String entrance_image = object.optString("entrance_image");
                                if (entrance_image.equals("null")){
                                    entrance_image = "";
                                }
                                String qrCode_image = object.optString("qrCodeURL");
                                if (street_name.equals("null")){
                                    street_name = "";
                                }
                                String street_img_type = object.optString("street_img_type");
                                if (street_img_type.equals("null")){
                                    street_img_type = "Street";
                                }
                                String building_img_type = object.optString("building_img_type");
                                if (building_img_type.equals("null")){
                                    building_img_type = "Building";
                                }
                                String entrance_img_type = object.optString("entrance_img_type");
                                if (entrance_img_type.equals("null")){
                                    entrance_img_type = "Entrance";
                                }
                                String id = object.optString("id");
                                publiclist.add(new savedItems(id,addressId, userId, userName, profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image, qrCode_image, street_img_type, building_img_type, entrance_img_type));

                            }

                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);

                        AdatperSavedAddresses _adaterFriends = new AdatperSavedAddresses(getActivity(), publiclist, saveInterface, listId, true);
                        recyclerView.setAdapter(_adaterFriends);

                        if (publiclist.isEmpty()){
                            txtError.setVisibility(View.VISIBLE);
                        }else {
                            txtError.setVisibility(View.GONE);
                        }
                        //getPrivateSavedAddress();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } else {
            new Message().showSnack(view.findViewById(R.id.root_lay), Constants.noInternetMessage);
        }


//    }
    }

    public void getSharedWithBusiness() {

        Parser.callApi(getActivity(), "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getSavedAddress(user_Id,"private"), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("shared with business", "response :" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("resultCode");
                    if (result_code.equals("1")) {
                        JSONArray resultData = jsonObject.optJSONArray("resultData");
                        for (int i = 0; i < resultData.length(); i++) {
                            JSONObject object = resultData.optJSONObject(i);
                            String addressId = object.optString("addressId");
                            String userId = object.optString("userId");
                            String profilePicURL = object.optString("profilePicURL");
                            String userName = object.optString("name");
                            String address_tag = object.optString("address_tag");
                            String latitude = object.optString("latitude");
                            String longitude = object.optString("longitude");
                            String plus_code = object.optString("plus_code");
                            String unique_link = object.optString("unique_link");
                            String country = object.optString("country");
                            String city = object.optString("city");
                            String street_name = object.optString("street_name");
                            String building_name = object.optString("building_name");
                            String entrance_name = object.optString("entrance_name");
                            String direction_text = object.optString("direction_text");
                            String street_image = object.optString("street_image");
                            String building_image = object.optString("building_image");
                            String entrance_image = object.optString("entrance_image");
                            String qrCode_image = object.optString("qrCodeURL");
                            String street_img_type = object.optString("street_img_type");
                            String building_img_type = object.optString("building_img_type");
                            String entrance_img_type = object.optString("entrance_img_type");
                            String id = object.optString("id");

                            publiclist.add(new savedItems(id,addressId, userId, userName, profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image, qrCode_image, street_img_type, building_img_type, entrance_img_type));

                        }

                    } else if (result_code.equals("0")) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "" + jsonObject.optString("data"));
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclervieww.setLayoutManager(linearLayoutManager);
                    recyclervieww.setHasFixedSize(true);

                    AdatperSharedWithMe _adaterFriends = new AdatperSharedWithMe(getActivity(), privatelist, saveInterface, listId, true);
                    recyclervieww.setAdapter(_adaterFriends);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

//====================================SAVE UNSAVED ADDRESS API==============================================//

    @Override
    public void saveUnsaveAddress(HashMap data, Boolean isSave) {

        if (isSave == true) {
            call = RetrofitInstance.getdata().create(Api.class).saveAddressToSavedList(data);
        } else {
            call = RetrofitInstance.getdata().create(Api.class).unsaveAddressToSavedList(data);
        }
        UtilityClass.showLoading(loader, getActivity());


        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, ":saveUnsaveAddress " + response);

                UtilityClass.hideLoading(loader, getActivity());
                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "This account has been deactivated from this platform.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "This account has been deleted from this platform.");
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "Something went wrong. Please try after some time.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "No data found.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "This account has been blocked.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), "All fields not sent.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(view.findViewById(R.id.root_lay), " Please check the request method.");
                    } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
                    }
                    UtilityClass.hideLoading(loader, getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(loader, getActivity());
            }
        });

    }

}



