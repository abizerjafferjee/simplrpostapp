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
import com.codeapex.simplrpostprod.ModelClass.PrivateAddressRequest;
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

public class EditPrivateAddressOption extends AppCompatActivity {

    //UI Components
    ImageView imgBack;
    CardView crdPrimaryInformation, crdLocationInformation;
    String privateAddressData;
    ProgressBar Loader;
    ConstraintLayout clytRootLayer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_edit_private_address_options);
        imgBack = findViewById(R.id.back_press);
        crdPrimaryInformation = findViewById(R.id.crdPrimaryInformation);
        crdLocationInformation = findViewById(R.id.crdLocationInformation);
        Loader = findViewById(R.id.Loader);
        if (getIntent().hasExtra("privateAddressData")) {
            privateAddressData = getIntent().getStringExtra("privateAddressData");
        }
        clytRootLayer = findViewById(R.id.root);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        crdPrimaryInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UtilityClass.isNetworkConnected(EditPrivateAddressOption.this)) {
                    Intent intent = new Intent(getApplicationContext(), EditPrivateAddressPrimary.class);
                    Bundle bundle = new Bundle();
                    PrivateAddressRequest request = null;
                    request = new Gson().fromJson(privateAddressData, PrivateAddressRequest.class);
                    bundle.putSerializable("privateAddressData", request);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }



            }
        });

        crdLocationInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(EditPrivateAddressOption.this)) {
                    Intent intent = new Intent(getApplicationContext(), EditPrivateAddressLocation.class);
                    Bundle bundle = new Bundle();
                    PrivateAddressRequest request = null;
                    request = new Gson().fromJson(privateAddressData, PrivateAddressRequest.class);
                    bundle.putSerializable("privateAddressData", request);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1098);
                }
                else {
                    new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
                }


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrivateAddressDetail();
    }

    public void getPrivateAddressDetail() {

        PrivateAddressRequest request = null;
        request = new Gson().fromJson(privateAddressData, PrivateAddressRequest.class);


        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId,getIntent().getStringExtra("userId"));
        hashMap.put(Constants.addressId,request.getAddressId());
        UtilityClass.showLoading(Loader,EditPrivateAddressOption.this);

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPrivateAddressDetail(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                UtilityClass.hideLoading(Loader,EditPrivateAddressOption.this);;

                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                        privateAddressData = jsonObject.getString(Constants.RESULT_DATA);


                    }
                    Loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);



            }
        });


//    }
    }

}
