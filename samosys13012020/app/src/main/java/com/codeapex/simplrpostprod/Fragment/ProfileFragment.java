package com.codeapex.simplrpostprod.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.codeapex.simplrpostprod.Activity.EditProfileActivity;
import com.codeapex.simplrpostprod.Adapter.AdaptorAddresses;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.ResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.SaveUserDetail;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements ProfileScreenInterface, OnRefreshListener {
    RecyclerView recyclerView;
    ProgressBar loader;
    ImageView circleImageView;
    LinearLayout root_lay;
    List<ResultPrivateAddress> models;
    List<ModelResultPrivateAddress> private_address_model = new ArrayList<>();
    SharedPreferences preferences;
    String userId, name, emailId, contactNumber, dpimg, isEmailIdVerified, isContactNumberVerified;
    TextView text,txtError, txt_contactNumber, txt_msg_profileComplete;
    String question, answer;
    ProfileScreenInterface intProfile;
    ConstraintLayout consEmptyScreen;
    SwipeRefreshLayout swipeRefreshLayout;

    String imgURL = "";
    ProgressBar progressBarImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile, container, false);
        consEmptyScreen = view.findViewById(R.id.consEmptyScreen);
        recyclerView = view.findViewById(R.id.recyecler);
        loader = view.findViewById(R.id.Loader);
        root_lay = view.findViewById(R.id.root_lay);
        text = view.findViewById(R.id.text);
        txt_contactNumber = view.findViewById(R.id.txt_contactNumber);
        progressBarImage = view.findViewById(R.id.progressBarImage);
        txtError = view.findViewById(R.id.txtError);

        txt_msg_profileComplete = view.findViewById(R.id.txt_msg_profileComplete);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);

        swipeRefreshLayout.setOnRefreshListener(this);

        preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        userId = preferences.getString(Constants.userId, "");

        models = new ArrayList<>();

        txt_msg_profileComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("userId", userId);
                intent.putExtra("emailId", emailId);
                intent.putExtra("contactNumber", contactNumber);
                intent.putExtra("isEmailIdVerified", isEmailIdVerified);
                intent.putExtra("isContactNumberVerified", isContactNumberVerified);
                intent.putExtra("dpimg", "");
                intent.putExtra("question", question);
                intent.putExtra("answer", answer);
                startActivityForResult(intent, 1024);
            }
        });

        intProfile = this;


        return view;
    }

    @Override
    public void onRefresh() {
        if (private_address_model!=null) {
            private_address_model.clear();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(getActivity(), private_address_model, "xyz", intProfile);
        recyclerView.setAdapter(custom_adaterFriends);
        get_profile();

        //Toast.makeText(getContext(),"hello",Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        get_profile();
        UtilityClass.hideLoading(loader, getActivity());
    }

    //..........................................Api...............................//
    public void get_profile() {
        try {
            if (UtilityClass.isNetworkConnected(getContext())) {
                SaveUserDetail model = UtilityClass.getLoginData(getContext());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, userId);

                //UtilityClass.showLoading(loader, getActivity());
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).getProfile(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (loader != null)
                            //UtilityClass.hideLoading(loader, getActivity());

                        if (response.isSuccessful()) {
                            try {

                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                //Log.e("PROFILE DATA:", "" + jsonObject);
                                String result_code = jsonObject.getString(Constants.RESULT_CODE);

                                if (result_code.equals(Constants.ZERO)) {
                                    //new Message().showSnack(root_lay, "No profile data available");

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
                                } else if (result_code.equals("1.0") || result_code.equals("1")) {
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    userId = jsonObject1.getString("userId");
                                    if (jsonObject1.has("name")) {
                                        name = jsonObject1.getString("name");
                                    }
                                    if (jsonObject1.has("emailId")) {
                                        emailId = jsonObject1.getString("emailId");
                                    }
                                    contactNumber = jsonObject1.getString("contactNumber");
                                    isEmailIdVerified = jsonObject1.getString("isEmailIdVerified");
                                    isContactNumberVerified = jsonObject1.getString("isContactNumberVerified");
                                    question = jsonObject1.getString("security_question");
                                    answer = jsonObject1.getString("security_answer");


                                    //Log.e("question", "drfgfgdfg : " + question);
                                    /*if (question.equals("")) {
                                        txt_msg_profileComplete.setVisibility(View.VISIBLE);
                                    } else {
                                        txt_msg_profileComplete.setVisibility(View.GONE);
                                    }*/

                                    if (getActivity() != null) {
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(Constants.userId, userId);
                                        editor.putString(Constants.contactNumber, contactNumber);

                                    /*if (jsonObject1.has("profilePicURL")) {
                                        editor.putString(Constants.profilePicURL, imgURL);
                                        if (imgURL != null && !imgURL.isEmpty()) {
                                            progressBarImage.setVisibility(View.VISIBLE);
                                            dpimg = Constants.IMG_URL + jsonObject1.getString("profilePicURL");

                                            Picasso.with(getContext())
                                                    .load(dpimg)
                                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                    .placeholder(R.drawable.profileplchlder)
                                                    .into(imgUserImage, new com.squareup.picasso.Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressBarImage.setVisibility(View.GONE);
                                                            imgUserImage.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                                                                    intent.putExtra("image", Constants.IMG_URL + imgURL);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            progressBarImage.setVisibility(View.GONE);
                                                        }
                                                    });
                                        } else {
                                            imgUserImage.setImageResource(R.drawable.profileplchlder);

                                        }
                                    } else {
                                        editor.putString(Constants.profilePicURL, "");
                                        progressBarImage.setVisibility(View.GONE);
                                        imgUserImage.setImageResource(R.drawable.profileplchlder);
                                    }*/

                                    editor.commit();
                                }

                                }

                                //getPrivateAddresses();
                                get_private_address();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                        UtilityClass.hideLoading(loader, getActivity());
                    }
                });


            } else {
                new Message().showSnack(view.findViewById(R.id.root_lay), Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }

    public void get_private_address() {
        try {
            if (UtilityClass.isNetworkConnected(getContext())) {
                Parser.callApi(getActivity(), "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getPrivateAddress(preferences.getString(Constants.userId, "")), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("Private", "Address response::" + response);

                        if (private_address_model!=null){
                            private_address_model.clear();
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
                                    String userName = object.optString("userName");
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

                                    private_address_model.add(new ModelResultPrivateAddress(addressId,userId,userName,profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image,qrCode_image,street_img_type,building_img_type,entrance_img_type));
                                }


                            } else if (result_code.equals("0")) {
                                new Message().showSnack(view.findViewById(R.id.root_lay), "" + jsonObject.optString("data"));
                            }

                            get_public_address();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new Message().showSnack(view.findViewById(R.id.root_lay), Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }

    public void get_public_address() {
        try {
            if (UtilityClass.isNetworkConnected(getContext())) {
                Parser.callApi(getActivity(), "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getPublicAddresses(preferences.getString(Constants.userId, "")), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("Public", "Address response::" + response);

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
                                    String userName = object.optString("userName");
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

                                    private_address_model.add(new ModelResultPrivateAddress(addressId,userId,userName,profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image,qrCode_image,street_img_type,building_img_type,entrance_img_type));
                                }



                            } else if (result_code.equals("0")) {
                                new Message().showSnack(view.findViewById(R.id.root_lay), "" + jsonObject.optString("data"));
                            }

                            if (private_address_model.isEmpty()){
                                txtError.setVisibility(View.VISIBLE);
                            }else {
                                txtError.setVisibility(View.GONE);
                            }
                            if (getActivity()!=null) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(getActivity(), private_address_model, "xyz", intProfile);
                                recyclerView.setAdapter(custom_adaterFriends);

                                if (private_address_model.size() == 0) {
                                    consEmptyScreen.setVisibility(View.VISIBLE);
                                } else {
                                    consEmptyScreen.setVisibility(View.GONE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new Message().showSnack(view.findViewById(R.id.root_lay), Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1098 && resultCode == RESULT_OK) {
            //getPrivateAddresses();
        } else if (requestCode == 1024 && resultCode == RESULT_OK) {
            //get_profile();
        }
    }

    public void openPrivateAddress(Intent objIntent) {
        UtilityClass.showLoading(loader, getActivity());
        startActivityForResult(objIntent, 1098);
    }

    public void openPublicAddress(Intent objIntent) {
        UtilityClass.showLoading(loader, getActivity());
        startActivityForResult(objIntent, 1098);
    }

}