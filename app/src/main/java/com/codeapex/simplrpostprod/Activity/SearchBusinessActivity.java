package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codeapex.simplrpostprod.Adapter.SearchBussinessAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SearchScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.BussinessModel;
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

public class SearchBusinessActivity extends AppCompatActivity implements LocationListener, SearchScreenInterface {
    RecyclerView recyclerView;
    EditText editTextSearch;
    ArrayList<String> names;
    List<BussinessModel> lsit;
    SearchBussinessAdapter adapter;
    ConstraintLayout root_lay;
    String addressId, shortName;
    LocationManager locationManager;
    String provider;
    LinearLayout category;
    SeekBar seekBar;
    String text;
    int distance = 5;
    int filterCategoryId = -1;
    String filterCategoryName;
    ProgressBar Loader;
    Dialog filterDialog;
    ImageView imageView,back;
    TextView txtvFilterCategoryName, txtvCancelButton, txtvApplyButton,tView,clear;
    String currentLatitude, currentLongitude, user_Id,userId;
    SearchScreenInterface searchInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_bussiness);
        imageView = findViewById(R.id.fillter);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        root_lay = findViewById(R.id.root_lay);
        Loader = findViewById(R.id.Loader);
        searchInterface = this;
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        if(editTextSearch.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }



        SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
  //      distance = getIntent().getExtras().getInt("text");
       // categoryId = getIntent().getExtras().getInt("categoryId");
        lsit = new ArrayList<>();
        addTextListener();
        filterDialog = new Dialog(SearchBusinessActivity.this);
        filterDialog.setContentView(R.layout.frag_dialog_explore);
        filterDialog.setCancelable(false);
        txtvFilterCategoryName = filterDialog.findViewById(R.id.filterCategoryName);
        txtvCancelButton = filterDialog.findViewById(R.id.filterCancel);
        txtvApplyButton = filterDialog.findViewById(R.id.filterApply);
        clear = filterDialog.findViewById(R.id.clear);

        back = findViewById(R.id.back_press);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }


        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to show filter pop up
                filterDialog.show();

                editTextSearch.clearFocus();
                //Code for category button on filter pop up
                category = filterDialog.findViewById(R.id.category);
                category.setOnClickListener(new View.OnClickListener() {
                    //            @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(filterDialog.getContext(), CategoryActivity.class);
                        intent.putExtra("isFromExplore", true);
                        startActivityForResult(intent, 2);
                    }
                });


                //Code for the seek bar on filter pop up
                seekBar = filterDialog.findViewById(R.id.ProgressBar);
                tView = filterDialog.findViewById(R.id.seekbar_text);
                seekBar.setMax(50);
                // seekBar.setMin(5);
                tView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int pval = 2;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        pval = progress;
                        distance = progress;
                        tView.setText(pval + "/" + seekBar.getMax());

                        text = tView.getText().toString();
                        String textt = tView.getText().toString();
                        Log.d(TAG, "onProgressChanged: " + text);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {


                    }
                });


                //Code for cancel button on filter pop up
                txtvCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filterDialog.dismiss();
                    }
                });


                //Code for apply button on filter pop up
                txtvApplyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editTextSearch.getText().length()>1 || filterCategoryId != -1) {
                            searchBusiness();
                        }
                        else {
                            lsit.clear();
                            SearchBussinessAdapter adapter = new SearchBussinessAdapter(getApplicationContext(), lsit,searchInterface,getIntent().getStringExtra("from"));
                            recyclerView.setAdapter(adapter);
                            new Message().showSnack(root_lay,"Please enter something to search...");
                        }
                        filterDialog.dismiss();

                    }
                });


                //Code for clear button on filter pop up
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        distance = -5;
                        filterCategoryId = -1;
                        seekBar.setProgress(5);
                        txtvFilterCategoryName.setText("Select");
                      //  filterDialog.dismiss();
//                        if (editTextSearch.getText().length()>=3) {
//                            searchBusiness();
//                        }

                    }
                });
            }
        });





        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        // Getting LocationManager object
        statusCheck();

        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);


        if (provider != null && !provider.equals("")) {
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings",
                        "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
            // Get the location from the given provider
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 500, 0, SearchBusinessActivity.this);

            if (location != null)
                onLocationChanged(location);
            else
                location = locationManager.getLastKnownLocation(provider);
            if (location != null)
                onLocationChanged(location);
            else

                Toast.makeText(getBaseContext(), "Location can't be retrieved",
                        Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void selectedSearchedAddress(BussinessModel objBusiness) {
        if (getIntent().getStringExtra("from").equals("shareProfile")) {
            if (objBusiness.getUser() == false){
                //Public Address
                //Code to share public address
                shareAddress(getIntent().getStringExtra("shareAddressId"),getIntent().getStringExtra("shareAddressIsPublic"),objBusiness.getAddressId(),false);


            }else {
                //User
                //Code to share private address
                shareAddress(getIntent().getStringExtra("shareAddressId"),getIntent().getStringExtra("shareAddressIsPublic"),objBusiness.getUserId(),true);
            }
        }
        else if(getIntent().getStringExtra("from").equals("ReportIssue")) {

                Intent intent = new Intent();
                intent.putExtra("addressID",objBusiness.getAddressId());
                intent.putExtra("name",objBusiness.getShortName());
                setResult(1,intent);
                finish();
            }
        else {
                if (objBusiness.getUser() == false){
                    Intent intent = new Intent(SearchBusinessActivity.this, PublicLocationDetailNewActivity.class);
                    intent.putExtra("addressId", objBusiness.getAddressId());
                    intent.putExtra("isOwn",false);
                    intent.putExtra("isFromShared",true);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SearchBusinessActivity.this,AddressesActivity.class);
                    intent.putExtra("formwhere","shareAdd");
                    startActivityForResult(intent,10);
                }
        }
    }

    public void onLocationChanged(Location location) {
        currentLatitude = String.valueOf(location.getLatitude());
        currentLongitude = String.valueOf(location.getLongitude());

        Log.d(TAG, "onLocationChanged: "+currentLatitude+currentLongitude);

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            filterCategoryId = Integer.parseInt(data.getStringExtra("categoryId"));
            filterCategoryName = data.getStringExtra("categoryName");
            txtvFilterCategoryName.setText(filterCategoryName);
        }else if(requestCode == 10){
            try {

                String  strAddressID = data.getStringExtra("addressId");
                boolean  isPrivate = data.getBooleanExtra("isPrivate",false);
                Log.e("backIntent",strAddressID+"    "+isPrivate);
                String strPublicorPrivate;
                if(isPrivate==true){
                    strPublicorPrivate = "0";
                }else {
                    strPublicorPrivate = "1";
                }

                if(strAddressID.isEmpty()){

                }else {
                    shareAddress(strAddressID,strPublicorPrivate, userId,true);
                }
            }catch (Exception e){

            }

        }
    }




    public void searchBusiness() {
        if (UtilityClass.isNetworkConnected(SearchBusinessActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.latitude, currentLatitude);
            hashMap.put(Constants.userId, user_Id);
            hashMap.put(Constants.longitude, currentLongitude);
            hashMap.put(Constants.categoryId, String.valueOf(filterCategoryId));
            hashMap.put(Constants.distance, String.valueOf(distance));
            hashMap.put(Constants.searchText, editTextSearch.getText().toString());

            UtilityClass.showLoading(Loader, SearchBusinessActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).searchBusiness(hashMap);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader, SearchBusinessActivity.this);
                    Log.d(TAG, "onResponse: " + response);
                    Log.e("ddddd_Response", new Gson().toJson(response.body()));
                    try {
                        lsit.clear();
                        SearchBussinessAdapter adapter = new SearchBussinessAdapter(getApplicationContext(), lsit,searchInterface,getIntent().getStringExtra("from"));
                        recyclerView.setAdapter(adapter);
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                        new Message().showSnack(root_lay, "No data found.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                            new Message().showSnack(root_lay, "List does not exist.");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_EIGHT)) {
                            new Message().showSnack(root_lay, "Address does not exist.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            JSONObject jsonObject1 = new JSONObject(result_data);

                            if (getIntent().hasExtra("from")) {
                                if(getIntent().getStringExtra("from").equals("ReportIssue")){
                                    if (jsonObject1.has("businessData")) {
                                        String businessData = jsonObject1.getString("businessData");
                                        JSONArray jsonArrayy = new JSONArray(businessData);
                                        for (int i = 0; i < jsonArrayy.length(); i++) {
                                            JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                            addressId = jsonObject2.getString("addressId");
                                            shortName = jsonObject2.getString("shortName");
                                            BussinessModel contactNumber1 = new BussinessModel("", "", "", "", false, addressId, shortName,jsonObject2.getString("categoryName"),jsonObject2.getString("plusCode"));
                                            Log.d(TAG, "onResponseecard: " + addressId + shortName);
                                            lsit.add(contactNumber1);


                                        }
                                    }

                                }
                                else {

                                    if (jsonObject1.has("userData")) {
                                        String userData = jsonObject1.getString("userData");
                                        JSONArray jsonArray = new JSONArray(userData);

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                            userId = jsonObject2.getString("userId");
                                            String profilePicURL = jsonObject2.getString("profilePicURL");
                                            String userName = jsonObject2.getString("userName");
                                            String name = jsonObject2.getString("name");
                                            Log.d(TAG, "onResponseebussiness: " + userId + profilePicURL + userName + name);
                                            BussinessModel contactNumber1 = new BussinessModel(userId, profilePicURL, userName, name, true, "", "","","");
                                            lsit.add(contactNumber1);


                                        }
                                    }
                                    if (jsonObject1.has("businessData")) {
                                        String businessData = jsonObject1.getString("businessData");
                                        JSONArray jsonArrayy = new JSONArray(businessData);
                                        for (int i = 0; i < jsonArrayy.length(); i++) {
                                            JSONObject jsonObject2 = jsonArrayy.getJSONObject(i);
                                            addressId = jsonObject2.getString("addressId");
                                            shortName = jsonObject2.getString("shortName");
                                            BussinessModel contactNumber1 = new BussinessModel("", jsonObject2.getString("logoURL"), "", "", false, addressId, shortName,jsonObject2.getString("categoryName"),jsonObject2.getString("plusCode"));
                                            Log.d(TAG, "onResponseecard: " + addressId + shortName);
                                            lsit.add(contactNumber1);


                                        }
                                    }


                                }
                            }


                            LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(linearLayoutManagerr);
                            recyclerView.setHasFixedSize(true);
                            adapter = new SearchBussinessAdapter(getApplicationContext(), lsit,searchInterface,getIntent().getStringExtra("from"));
                            recyclerView.setAdapter(adapter);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader, SearchBusinessActivity.this);
                    Log.d(TAG, "onFailure: " + t);


                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }


    }

    public void addTextListener() {



        editTextSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    lsit.clear();
                    SearchBussinessAdapter adapter = new SearchBussinessAdapter(getApplicationContext(), lsit,searchInterface,getIntent().getStringExtra("from"));
                    recyclerView.setAdapter(adapter);
                }
                else if (s.length()>1 || filterCategoryId != -1) {
                    searchBusiness();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                // adapter.getFilter().filter(query.toString());
            }


        });


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* getMenuInflater().inflate(R.menu.activity_main, menu); */
        return true;
    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    public void shareAddress(String addressID,String isPublic, String receiverId, Boolean isWithUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id); //logged in user id
        hashMap.put(Constants.receiverId, receiverId); //searched user or public address
        hashMap.put(Constants.addressId, addressID); //address to share
        hashMap.put(Constants.isAddressPublic,isPublic );
        Log.d(TAG, "saveAddressToSavedList: " + user_Id +"  "+ userId+"  "+addressID+"  "+isPublic);

        UtilityClass.showLoading(Loader, SearchBusinessActivity.this);
        Call<Object> call;
        if (isWithUser)
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithUser(hashMap);
        else
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithBusiness(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader, SearchBusinessActivity.this);
                Log.e("share_Response", new Gson().toJson(response.body()));

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


                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                        new Message().showSnack(root_lay, "List does not exist.");


                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_EIGHT)) {
                        new Message().showSnack(root_lay, "Address does not exist.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(root_lay, "This account has been blocked.");


                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(root_lay, "All fields not sent.");

                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(root_lay, " Please check the request method.");
                    }
                    else if (result_code.equals("-11.0")) {
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(SearchBusinessActivity.this);
                        alertbox.setMessage("This address is already shared.");
                        alertbox.setTitle("");
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
                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                        fill.setImageResource(R.drawable.hearticon);
//                        new UtilityClass().simpleAlertBox(SearchBusinessActivity.this,"Your address shared successfully.");
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(SearchBusinessActivity.this);
                        alertbox.setMessage("Your address shared successfully.");
                        alertbox.setTitle("");
                        alertbox.setCancelable(false);
                        alertbox.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        finish();

                                    }
                                });
                        alertbox.show();

//                        Toast.makeText(SearchBusinessActivity.this, "Your address shared successfully.", Toast.LENGTH_SHORT).show();




                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.showLoading(Loader, SearchBusinessActivity.this);

            }
        });


    }

}





