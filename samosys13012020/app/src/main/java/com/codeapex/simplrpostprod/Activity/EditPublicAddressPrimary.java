package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.Category;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPublicAddressPrimary extends AppCompatActivity {


    ImageView imgBack;
    CardView crdSlide1Next, crdSlide2Next, crdSlide3Next, crdSlide2Previous, crdSlide3Previous;
    LinearLayout llytSlide1, llytSlide2, llytSlide3;
    EditText edtShortName, edtShortDescription;
    TextView txtvCategoryButton;
    ProgressBar Loader;

    String publicAddressData;
    Category categoryObject;
    ConstraintLayout clytRootLayer;
    JSONObject jsonObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_public_address_primary);

        imgBack = findViewById(R.id.back_press);
        clytRootLayer = findViewById(R.id.rootLayer);
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide3Next = findViewById(R.id.crdSlide3Next);
        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        crdSlide3Previous = findViewById(R.id.crdSlide3Previous);
        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        llytSlide3 = findViewById(R.id.slide3);
        edtShortName = findViewById(R.id.edtShortName);
        txtvCategoryButton = findViewById(R.id.txtvCategoryButton);
        edtShortDescription = findViewById(R.id.edtShortDescription);
        Loader = findViewById(R.id.Loader);


        if (getIntent().hasExtra("publicAddressData")) {
            publicAddressData = getIntent().getStringExtra("publicAddressData");
            try {
                jsonObject = new JSONObject(publicAddressData);

                edtShortName.setText(jsonObject.getString("shortName"));
                edtShortDescription.setText(jsonObject.getString("description"));
                txtvCategoryButton.setText(jsonObject.getString("categoryName"));
                categoryObject = new Category(jsonObject.getString("categoryId"),jsonObject.getString("categoryName"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtvCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPublicAddressPrimary.this,CategoryActivity.class);
                startActivityForResult(intent,104);
            }
        });


        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressPrimary.this);
                if (edtShortName.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please enter business name.");
                }
                else {
                    llytSlide1.setVisibility(View.GONE);
                    llytSlide2.setVisibility(View.VISIBLE);
                }

            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressPrimary.this);
                if (txtvCategoryButton.getText().equals("Select Category")) {
                    new Message().showSnack(clytRootLayer, "Please select a category.");
                }
                else {
                    llytSlide2.setVisibility(View.GONE);
                    llytSlide3.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide3Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressPrimary.this);
                if (edtShortDescription.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please enter description about your business.");
                }
                else {
                    try {
                        editPublicAddressPrimary();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressPrimary.this);
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });

        crdSlide3Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressPrimary.this);
                llytSlide3.setVisibility(View.GONE);
                llytSlide2.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 104 && resultCode == Activity.RESULT_OK) {
            categoryObject = (Category) data.getSerializableExtra("category");
            txtvCategoryButton.setText(categoryObject.getCategoryName());
        }
    }

    //API related methods

    private void editPublicAddressPrimary() throws JSONException {
        if (UtilityClass.isNetworkConnected(EditPublicAddressPrimary.this)) {
            PublicAddressRequest request = new PublicAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();



            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,""));
            request.setAddressId(jsonObject.getString("addressId"));
            request.setShortName(edtShortName.getText().toString().trim());
            request.setDescription(edtShortDescription.getText().toString().trim());
            request.setCategoryId(categoryObject.getCategoryId());


            if (request.getDescription().length() > 500) {
                new Message().showSnack(clytRootLayer, "Description can have max 500 characters.");
                return;
            }


            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPublicPrimary(request);
            UtilityClass.showLoading(Loader,EditPublicAddressPrimary.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressPrimary.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {
                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPublicAddressPrimary.this);
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
                            UtilityClass.hideLoading(Loader,EditPublicAddressPrimary.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressPrimary.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }


    }
}
