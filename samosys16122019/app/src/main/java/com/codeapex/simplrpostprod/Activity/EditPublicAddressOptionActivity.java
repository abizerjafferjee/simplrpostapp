package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
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

public class EditPublicAddressOptionActivity extends AppCompatActivity {


    //UI Components
    ImageView imgBack;
    CardView crdPrimaryInformation, crdLocationInformation, crdServices, crdMiscellaneousInformation;
    ProgressBar Loader;
    ConstraintLayout clytRootLayer;


    String publicAddressData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_edit_address_options);
        imgBack = findViewById(R.id.back_press);
        crdPrimaryInformation = findViewById(R.id.crdPrimaryInformation);
        crdLocationInformation = findViewById(R.id.crdLocationInformation);
        crdServices = findViewById(R.id.crdServices);
        crdMiscellaneousInformation = findViewById(R.id.crdMiscellaneousInformation);
        Loader = findViewById(R.id.Loader);
        clytRootLayer = findViewById(R.id.root);

        if (getIntent().hasExtra("publicAddressData")) {
            publicAddressData = getIntent().getStringExtra("publicAddressData");
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        crdPrimaryInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(EditPublicAddressOptionActivity.this)) {
                    Intent intent = new Intent(getApplicationContext(),EditPublicAddressPrimary.class);
                    intent.putExtra("publicAddressData",publicAddressData);
                    startActivity(intent);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }

            }
        });

        crdLocationInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(EditPublicAddressOptionActivity.this)) {
                    Intent intent = new Intent(getApplicationContext(), EditPublicAddressLocation.class);
                    Bundle bundle = new Bundle();
                    PublicAddressRequest request = null;
                    request = new Gson().fromJson(publicAddressData, PublicAddressRequest.class);
                    //                request.setAddressId(address_id);
                    bundle.putSerializable("publicAddressData", request);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }

            }
        });

        crdServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(EditPublicAddressOptionActivity.this)) {
                    Intent intent = new Intent(getApplicationContext(), EditPublicAddressServices.class);
                    Bundle bundle = new Bundle();
                    PublicAddressRequest request = null;
                    request = new Gson().fromJson(publicAddressData, PublicAddressRequest.class);
                    //                request.setAddressId(address_id);
                    bundle.putSerializable("publicAddressData", request);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }

            }
        });

        crdMiscellaneousInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(EditPublicAddressOptionActivity.this)) {
                    Intent intent = new Intent(getApplicationContext(), EditPublicAddressMiscellaneous.class);
                    Bundle bundle = new Bundle();
                    PublicAddressRequest request = null;
                    request = new Gson().fromJson(publicAddressData, PublicAddressRequest.class);
                    //                request.setAddressId(address_id);
                    bundle.putSerializable("publicAddressData", request);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }



//                Intent intent = new Intent(getApplicationContext(),EditPublicAddressMiscellaneous.class);
//                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPublicAddressDetail();
    }

    public void getPublicAddressDetail() {
        PublicAddressRequest request = null;
        request = new Gson().fromJson(publicAddressData, PublicAddressRequest.class);
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId, getIntent().getStringExtra("userId"));
        hashMap.put(Constants.addressId, request.getAddressId());
        UtilityClass.showLoading(Loader,EditPublicAddressOptionActivity.this);

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPublicAddressDetail(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,EditPublicAddressOptionActivity.this);
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {


                        Log.d(TAG, "tarnum: " + response);
                        publicAddressData = jsonObject.getString(Constants.RESULT_DATA);







                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                UtilityClass.hideLoading(Loader,EditPublicAddressOptionActivity.this);

            }
        });




    }



}
