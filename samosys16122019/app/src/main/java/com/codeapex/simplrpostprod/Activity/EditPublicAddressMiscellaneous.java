package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdaterHours;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ContactNumber;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.ModelClass.SocialMedia;
import com.codeapex.simplrpostprod.ModelClass.WorkingHour;
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

public class EditPublicAddressMiscellaneous extends AppCompatActivity {


    ImageView imgBack;
    CardView crdSlide1Next, crdSlide2Next, crdSlide2Previous;
    LinearLayout llytSlide1, llytSlide2;
    EditText edtEmailId, edtPhoneNumber, edtWebsiteURL, edtFacebook, edtTwitter, edtLinkedIn, edtInstagram;
    RecyclerView rcvTiming;
    Switch swtDelivery;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;
    Spinner spinnerPhone;

    String countryCode = "", phoneNumber ="", spinnerValue;

    PublicAddressRequest publicAddressData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_public_address_miscellaneous);

        imgBack = findViewById(R.id.back_press);
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        edtEmailId = findViewById(R.id.txt_pub_email);
        edtPhoneNumber = findViewById(R.id.txtvPhoneNumber);
        edtWebsiteURL = findViewById(R.id.txtvWebsiteURL);
        edtFacebook = findViewById(R.id.txtvFacebook);
        edtTwitter = findViewById(R.id.txtvTwitter);
        edtLinkedIn = findViewById(R.id.txtvLinkedIn);
        edtInstagram = findViewById(R.id.txtvInstagram);
        rcvTiming = findViewById(R.id.rcvTiming);
        swtDelivery = findViewById(R.id.sw_delivery);
        clytRootLayer = findViewById(R.id.rootLayer);
        Loader = findViewById(R.id.Loader);
        spinnerPhone = findViewById(R.id.spinnerPhone);





        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(adapter);


        spinnerPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ArrayList<WorkingHour> myListData = new ArrayList<WorkingHour>();
        myListData.add(new WorkingHour("Sunday","1"));
        myListData.add(new WorkingHour("Monday","2"));
        myListData.add(new WorkingHour("Tuesday","3"));
        myListData.add(new WorkingHour("Wednesday","4"));
        myListData.add(new WorkingHour("Thursday","5"));
        myListData.add(new WorkingHour("Friday","6"));
        myListData.add(new WorkingHour("Saturday","7"));




        if (getIntent().hasExtra("publicAddressData")) {
            publicAddressData = (PublicAddressRequest) getIntent().getSerializableExtra("publicAddressData");
            edtEmailId.setText(publicAddressData.getEmailId());


            for(int i = 0;i<publicAddressData.getContactNumber().get(0).getPhoneNumber().length();i++)
            {
                if(i<4)
                    countryCode = countryCode + publicAddressData.getContactNumber().get(0).getPhoneNumber().charAt(i);
                else
                    phoneNumber = phoneNumber + publicAddressData.getContactNumber().get(0).getPhoneNumber().charAt(i);
            }
            if (countryCode != null) {
                int spinnerPosition = adapter.getPosition(countryCode);
                spinnerPhone.setSelection(spinnerPosition);
            }

            edtPhoneNumber.setText(phoneNumber);
            edtWebsiteURL.setText(publicAddressData.getSocialMedia().getWebsite());
            edtFacebook.setText(publicAddressData.getSocialMedia().getFacebook());
            edtTwitter.setText(publicAddressData.getSocialMedia().getTwitter());
            edtLinkedIn.setText(publicAddressData.getSocialMedia().getLinkedin());
            edtInstagram.setText(publicAddressData.getSocialMedia().getInstagram());
            swtDelivery.setChecked((publicAddressData.getDeliveryAvailable().equals("1"))?true:false);
            if (publicAddressData.getWorkingHours()!= null) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                rcvTiming.setLayoutManager(linearLayoutManager);
                rcvTiming.setHasFixedSize(true);
                AdaterHours custom_adaterFriends = new AdaterHours(getApplicationContext(),publicAddressData.getWorkingHours());
                rcvTiming.setAdapter(custom_adaterFriends);
            }
            else {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                rcvTiming.setLayoutManager(linearLayoutManager);
                rcvTiming.setHasFixedSize(true);
                AdaterHours custom_adaterFriends = new AdaterHours(getApplicationContext(),myListData);
                rcvTiming.setAdapter(custom_adaterFriends);
            }
        }
        else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            rcvTiming.setLayoutManager(linearLayoutManager);
            rcvTiming.setHasFixedSize(true);
            AdaterHours custom_adaterFriends = new AdaterHours(getApplicationContext(),myListData);
            rcvTiming.setAdapter(custom_adaterFriends);
        }






        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llytSlide1.setVisibility(View.GONE);
                llytSlide2.setVisibility(View.VISIBLE);
            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to call the API to save the primary information
                try {
                    editPublicAddressMiscellenous();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });
    }

    //API related methods

    private void editPublicAddressMiscellenous() throws JSONException {
        if (UtilityClass.isNetworkConnected(EditPublicAddressMiscellaneous.this)) {
            PublicAddressRequest request = new PublicAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();

            ArrayList<ContactNumber> arrayListner = new ArrayList<ContactNumber>();
            arrayListner.add(new ContactNumber(spinnerValue + edtPhoneNumber.getText().toString().trim()));

            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,""));
            request.setAddressId(publicAddressData.getAddressId());
            request.setEmailId(edtEmailId.getText().toString().trim());
            request.setDeliveryAvailable((swtDelivery.isChecked())?"1":"0");
            request.setContactNumber(arrayListner);
            SocialMedia social = new SocialMedia();
            social.setFacebook(edtFacebook.getText().toString().trim());
            social.setLinkedin(edtLinkedIn.getText().toString().trim());
            social.setTwitter(edtTwitter.getText().toString().trim());
            social.setInstagram(edtInstagram.getText().toString().trim());
            social.setWebsite(edtWebsiteURL.getText().toString().trim());

            request.setSocialMedia(social);
            request.setWorkingHours(((AdaterHours)rcvTiming.getAdapter()).getMyListData());


            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPublicMiscellanous(request);
            UtilityClass.showLoading(Loader,EditPublicAddressMiscellaneous.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressMiscellaneous.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPublicAddressMiscellaneous.this);
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
                            UtilityClass.hideLoading(Loader,EditPublicAddressMiscellaneous.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressMiscellaneous.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }


    }
}
