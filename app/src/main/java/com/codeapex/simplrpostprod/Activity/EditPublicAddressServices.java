package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdapterServiceImage;
import com.codeapex.simplrpostprod.Adapter.ImagePicker;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPublicAddressServices extends AppCompatActivity {


    ImageView imgBack;
    EditText edtServiceDescription;
    RecyclerView rcvServices;
    CardView crdSlide1Next;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;


    PublicAddressRequest publicAddressData;
    ArrayList<String> serviceImageList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_public_address_services);

        imgBack = findViewById(R.id.back_press);
        edtServiceDescription = findViewById(R.id.edtServiceDescription);
        rcvServices = findViewById(R.id.rv_service_images);
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        clytRootLayer = findViewById(R.id.rootLayer);
        Loader = findViewById(R.id.Loader);



        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressServices.this);
//                if (edtServiceDescription.getText().toString().trim().equals("")) {
//                    new Message().showSnack(clytRootLayer, "Please enter service description.");
//                }
//                else {
                    //Code to call API
                    try {
                        editPublicAddressServices();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                }
            }
        });

        rcvServices.setLayoutManager(new GridLayoutManager(this,3));
        rcvServices.setAdapter(new AdapterServiceImage(serviceImageList, this, new ImagePicker() {
            @Override
            public void pickImage() {
                if (UtilityClass.isStroagePermissionGranted(EditPublicAddressServices.this,EditPublicAddressServices.this,999)) {
                    getDocumnet();
                }
            }
        }));


        if (getIntent().hasExtra("publicAddressData")) {
            publicAddressData = (PublicAddressRequest) getIntent().getSerializableExtra("publicAddressData");
            edtServiceDescription.setText(publicAddressData.getServiceDescription());
            if (publicAddressData.getServices() != null) {
                for (int i = 0; i<publicAddressData.getImages().size(); i++) {
                    serviceImageList.add(Constants.IMG_URL + publicAddressData.getImages().get(i).getImageURL());
                }
                rcvServices.getAdapter().notifyDataSetChanged();
            }
        }


    }

    private void getDocumnet() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,102);

    }

    //API related methods

    private void editPublicAddressServices() throws JSONException {
        if (UtilityClass.isNetworkConnected(EditPublicAddressServices.this)) {
            PublicAddressRequest request = new PublicAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();



            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,""));
            request.setAddressId(publicAddressData.getAddressId());
            request.setServiceDescription(edtServiceDescription.getText().toString().trim());



            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPublicServices(request);
            UtilityClass.showLoading(Loader,EditPublicAddressServices.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressServices.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPublicAddressServices.this);
                                alertbox.setMessage("Your address updated successfully.");
                                alertbox.setTitle("Success");
                                alertbox.setCancelable(false);
                                alertbox.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {

                                                finish();
                                            }
                                        });
                                alertbox.show();

                            }
                            else if (result_code == 0) {
                                new Message().showSnack(clytRootLayer,"This account has been deactivated.");
                            }
                            else if (result_code == -1) {
                                new Message().showSnack(clytRootLayer,"This account has been deleted.");
                            }
                            else if (result_code == -2) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }
                            else if (result_code == -3) {
                                new Message().showSnack(clytRootLayer,"No data found.");
                            }
                            else if (result_code == -5) {
                                new Message().showSnack(clytRootLayer,"This account has been blocked.");
                            }
                            else if (result_code == -6) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }
                        }
                        catch (Exception e) {
                            UtilityClass.hideLoading(Loader,EditPublicAddressServices.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressServices.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }


    }
}
