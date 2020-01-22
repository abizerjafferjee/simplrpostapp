package com.codeapex.simplrpostprod.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
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

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class SearchBusinessFragment extends Fragment implements SearchScreenInterface {
    RecyclerView recyclerView;
    EditText editTextSearch;
    List<BussinessModel> lsit;
    SearchBussinessAdapter adapter;
    LinearLayout root_lay;
    String addressId,provider,text,user_Id,userId;
    ProgressBar Loader;
    ImageButton btn_search,btn_scanner;
    TextView emptyText;
    SearchScreenInterface searchInterface;
    List<ModelResultPrivateAddress> private_address_model = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private int REQUEST_CODE_ASK_PERMISSIONS = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_serach_bussiness,container,false);
        sharedPreferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerView);
        root_lay = view.findViewById(R.id.root_lay);
        Loader = view.findViewById(R.id.Loader);
        btn_search = view.findViewById(R.id.btn_search);
        searchInterface = this;
        editTextSearch = view.findViewById(R.id.editTextSearch);
        emptyText = view.findViewById(R.id.emptyText);
        btn_scanner = view.findViewById(R.id.btn_scanner);

        if(editTextSearch.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        btn_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions(PERMISSIONS)) {
                    Intent intent = new Intent(getActivity(), QRScanActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                    }
                }
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        lsit = new ArrayList<>();
        addTextListener();

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    if (UtilityClass.isNetworkConnected(getActivity())) {
                        searchBusiness(editTextSearch.getText().toString().trim());
                        //getPublicAddress(editTextSearch.getText().toString().trim());
                    }else {
                        new Message().showSnack(root_lay, Constants.noInternetMessage);
                    }
                    hideKeyboard(getActivity());
                    return true;
                } else {
                    return false;
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(getActivity())) {
                    //searchBusiness(editTextSearch.getText().toString().trim());
                    //getPublicAddress(editTextSearch.getText().toString().trim());
                }else {
                    new Message().showSnack(root_lay, Constants.noInternetMessage);
                }
                hideKeyboard(getActivity());
            }
        });
        return view;
    }

    public boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                Toast.makeText(getActivity(), "Some Permission denied", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    private String[] PERMISSIONS = {Manifest.permission.CAMERA};

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                editTextSearch.setText(data.getStringExtra("code"));
                editTextSearch.setSelection(editTextSearch.length());
                searchBusiness(data.getStringExtra("code"));
            }
        }
    }

    public void selectedSearchedAddress(BussinessModel objBusiness) {
        if (getActivity().getIntent().getStringExtra("from").equals("shareProfile")) {
            if (objBusiness.getUser() == false){
                //Public Address
                //Code to share public address
                //shareAddress(getActivity().getIntent().getStringExtra("shareAddressId"),getActivity().getIntent().getStringExtra("shareAddressIsPublic"),objBusiness.getAddressId(),false);


            }else {
                //User
                //Code to share private address
                //shareAddress(getActivity().getIntent().getStringExtra("shareAddressId"),getActivity().getIntent().getStringExtra("shareAddressIsPublic"),objBusiness.getUserId(),true);
            }
        }
        else if(getActivity().getIntent().getStringExtra("from").equals("ReportIssue")) {

                Intent intent = new Intent();
                intent.putExtra("addressID",objBusiness.getAddressId());
                intent.putExtra("name",objBusiness.getShortName());
            getActivity().setResult(1,intent);
            getActivity().finish();
            }
        else {
                if (objBusiness.getUser() == false){
                    Intent intent = new Intent(getActivity(), PublicLocationDetailNewActivity.class);
                    intent.putExtra("addressId", objBusiness.getAddressId());
                    intent.putExtra("isOwn",false);
                    intent.putExtra("isFromShared",true);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(),AddressesActivity.class);
                    intent.putExtra("formwhere","shareAdd");
                    startActivityForResult(intent,10);
                }
        }
    }

    public void searchBusiness(String search) {
        emptyText.setVisibility(View.VISIBLE);
        if (UtilityClass.isNetworkConnected(getActivity())) {

            Parser.callApi(getActivity(), "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).searchPrivateAddresses(search), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("Search private", "Address response::" + response);

                    if (private_address_model != null) {
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


                                private_address_model.add(new ModelResultPrivateAddress(addressId, userId, userName, profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image, qrCode_image, street_img_type, building_img_type, entrance_img_type));

                            }
                            emptyText.setVisibility(View.GONE);
                        }

                        getPublicAddress(search);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }

    }

    public void getPublicAddress(String search){
        emptyText.setVisibility(View.VISIBLE);
        Parser.callApi(getActivity(), "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).searchPublicAddresses(search), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Search Public", "Address response::" + response);

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


                            private_address_model.add(new ModelResultPrivateAddress(addressId, userId, userName, profilePicURL, address_tag, latitude, longitude, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_image, building_image, entrance_image, qrCode_image, street_img_type, building_img_type, entrance_img_type));

                        }
                    }

                    if (private_address_model.isEmpty()){
                        emptyText.setText("No address found.");
                        emptyText.setVisibility(View.VISIBLE);

                    }else {
                        emptyText.setVisibility(View.GONE);
                        emptyText.setText("Search address with unique name, mobile number and plus code Or you can search address by scanning Qr code.");
                    }
                        LinearLayoutManager linearLayoutManagerr = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManagerr);
                        recyclerView.setHasFixedSize(true);
                        SearchBussinessAdapter adapter = new SearchBussinessAdapter(getActivity(), private_address_model,searchInterface,getActivity().getIntent().getStringExtra("from"));
                        recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void addTextListener() {

        editTextSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    private_address_model.clear();
                    btn_search.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("Search address with unique name, mobile number and plus code Or you can search address by scanning Qr code.");
                    SearchBussinessAdapter adapter = new SearchBussinessAdapter(getActivity(), private_address_model,searchInterface,getActivity().getIntent().getStringExtra("from"));
                    recyclerView.setAdapter(adapter);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                // adapter.getFilter().filter(query.toString());
                if (query.length() >2) {
                    btn_search.setVisibility(View.GONE);
                    searchBusiness(query.toString().trim());
                }
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        getActivity().finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void shareAddress(String addressID,String isPublic, String receiverId, Boolean isWithUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id); //logged in user id
        hashMap.put(Constants.receiverId, receiverId); //searched user or public address
        hashMap.put(Constants.addressId, addressID); //address to share
        hashMap.put(Constants.isAddressPublic,isPublic );
        Log.d(TAG, "saveAddressToSavedList: " + user_Id +"  "+ userId+"  "+addressID+"  "+isPublic);

        //UtilityClass.showLoading(Loader, getActivity());
        Call<Object> call;
        if (isWithUser)
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithUser(hashMap);
        else
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithBusiness(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //UtilityClass.hideLoading(Loader, getActivity());
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
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
                        alertbox.setMessage("This address is already shared.");
                        alertbox.setTitle("");
                        alertbox.setCancelable(false);
                        alertbox.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        getActivity().finish();

                                    }
                                });
                        alertbox.show();
                    }
                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                        fill.setImageResource(R.drawable.hearticon);
//                        new UtilityClass().simpleAlertBox(SearchBusinessFragment.this,"Your address shared successfully.");
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
                        alertbox.setMessage("Your address shared successfully.");
                        alertbox.setTitle("");
                        alertbox.setCancelable(false);
                        alertbox.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        getActivity().finish();

                                    }
                                });
                        alertbox.show();

//                        Toast.makeText(SearchBusinessFragment.this, "Your address shared successfully.", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                //UtilityClass.showLoading(Loader, getActivity());

            }
        });


    }

}





