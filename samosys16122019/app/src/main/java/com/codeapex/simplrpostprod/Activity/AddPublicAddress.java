package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.codeapex.simplrpostprod.Adapter.AdapterBusinessImage;
import com.codeapex.simplrpostprod.Adapter.AdapterServiceImage;
import com.codeapex.simplrpostprod.Adapter.AdaterHours;
import com.codeapex.simplrpostprod.Adapter.GoogleAutoCompletePlaceAdapter;
import com.codeapex.simplrpostprod.Adapter.ImagePicker;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.Category;
import com.codeapex.simplrpostprod.ModelClass.ContactNumber;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.ModelClass.PublicImageRequest;
import com.codeapex.simplrpostprod.ModelClass.SocialMedia;
import com.codeapex.simplrpostprod.ModelClass.WorkingHour;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.FileUtil;
import com.codeapex.simplrpostprod.UtilityClass.SaveUserDetail;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPublicAddress extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //UI Components
    Spinner spinnerPhone;
    LinearLayout llytSlide1, llytSlide2, llytSlide3, llytSlide4, llytSlide5, llytSlide6, llytSlide7, llytSlide8, llytSlide9, llytImagePickerButton;
    CardView crdSlide1Next, crdSlide2Next, crdSlide3Next, crdSlide4Next, crdSlide5Next, crdSlide6Next, crdSlide7Next, crdSlide8Next, crdSlide9Next;
    CardView crdSlide2Previous, crdSlide3Previous, crdSlide4Previous, crdSlide5Previous, crdSlide6Previous, crdSlide7Previous, crdSlide8Previous, crdSlide9Previous;
    ImageView imgBack, imgAddressLogo, imgLocationCrossButton, imgLocationPic;
    EditText edtBusinessName, edtShortDescription, edtAddress, edtLandmark, edtEmailId, edtPhoneNumber, edtWebsiteURL, edtFacebook, edtTwitter, edtLinkedin, edtInstagram, edtServiceDescription;
    TextView txtvCategoryButton, txtvCurrentLocation;
    AutoCompleteTextView atxtvAddress;
    RecyclerView rcvImages, rcvServices, rcvTiming;
    SupportMapFragment mapView;
    Switch swtDelivery;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;
    RelativeLayout rlytSelectButton;


    String spinnerValue;


    //Global Variables
    ArrayList<String> serviceBusinessImageList = new ArrayList<String>();
    ArrayList<String> serviceImageList = new ArrayList<String>();
    String imageBase64 = "", strLocationPicBase64 = "", name, emailId, contactNumber, countryCode = "", phoneNumber = "";
    Category categoryObject;
    GoogleAutoCompletePlaceAdapter autoCompleteAdapter;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mMap;
    PlaceDetectionClient mPlaceDetectionClient;
    ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_public_address);


        //Map scroll
        mMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        scrollView = findViewById(R.id.scrollMap);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


        imgBack = findViewById(R.id.back_press);
        clytRootLayer = findViewById(R.id.rootLayer);
        edtBusinessName = findViewById(R.id.edtBusinessName);
        txtvCategoryButton = findViewById(R.id.txtvCategoryButton);
        edtShortDescription = findViewById(R.id.edtShortDescription);
        edtAddress = findViewById(R.id.edtAddress);
        edtLandmark = findViewById(R.id.edtLandmark);
        atxtvAddress = findViewById(R.id.address_txt);
        txtvCurrentLocation = findViewById(R.id.txt_current_location);
        imgAddressLogo = findViewById(R.id.img_pub_logo);
        imgLocationPic = findViewById(R.id.imgLocationPic);
        llytImagePickerButton = findViewById(R.id.llytImagePickerButton);
        rcvImages = findViewById(R.id.rv_images);
        rcvServices = findViewById(R.id.rv_service_images);
        edtEmailId = findViewById(R.id.txt_pub_email);
        edtPhoneNumber = findViewById(R.id.txtvPhoneNumber);
        edtWebsiteURL = findViewById(R.id.txtvWebsiteURL);
        edtFacebook = findViewById(R.id.txtvFacebook);
        edtTwitter = findViewById(R.id.txtvTwitter);
        edtLinkedin = findViewById(R.id.txtvLinkedIn);
        edtInstagram = findViewById(R.id.txtvInstagram);
        rcvTiming = findViewById(R.id.rcvTiming);
        swtDelivery = findViewById(R.id.sw_delivery);
        edtServiceDescription = findViewById(R.id.edtServiceDescription);
        Loader = findViewById(R.id.Loader);
        spinnerPhone = findViewById(R.id.spinnerPhone);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        imgLocationCrossButton = findViewById(R.id.locationCross);
        rlytSelectButton = findViewById(R.id.images);


        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        llytSlide3 = findViewById(R.id.slide3);
        llytSlide4 = findViewById(R.id.slide4);
        llytSlide5 = findViewById(R.id.slide5);
        llytSlide6 = findViewById(R.id.slide6);
        llytSlide7 = findViewById(R.id.slide7);
        llytSlide8 = findViewById(R.id.slide8);
        llytSlide9 = findViewById(R.id.slide9);

        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide3Next = findViewById(R.id.crdSlide3Next);
        crdSlide4Next = findViewById(R.id.crdSlide4Next);
        crdSlide5Next = findViewById(R.id.crdSlide5Next);
        crdSlide6Next = findViewById(R.id.crdSlide6Next);
        crdSlide7Next = findViewById(R.id.crdSlide7Next);
        crdSlide8Next = findViewById(R.id.crdSlide8Next);
        crdSlide9Next = findViewById(R.id.crdSlide9Next);

        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        crdSlide3Previous = findViewById(R.id.crdSlide3Previous);
        crdSlide4Previous = findViewById(R.id.crdSlide4Previous);
        crdSlide5Previous = findViewById(R.id.crdSlide5Previous);
        crdSlide6Previous = findViewById(R.id.crdSlide6Previous);
        crdSlide7Previous = findViewById(R.id.crdSlide7Previous);
        crdSlide8Previous = findViewById(R.id.crdSlide8Previous);
        crdSlide9Previous = findViewById(R.id.crdSlide9Previous);



        //edtPhoneNumber.setText(phoneNumber);

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


        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();
//        .setCountry("IN")
        autoCompleteAdapter = new GoogleAutoCompletePlaceAdapter(this,filter);

        atxtvAddress.setAdapter(autoCompleteAdapter);
        atxtvAddress.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        txtvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                atxtvAddress.clearFocus();
                atxtvAddress.setText("");
                checkGPSAndPermission();
            }
        });
        checkGPSAndPermission();
        mapView.getMapAsync(AddPublicAddress.this);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtvCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPublicAddress.this,CategoryActivity.class);
                startActivityForResult(intent,104);
            }
        });

        imgLocationCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atxtvAddress.setText("");
            }
        });

        imgAddressLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                if (UtilityClass.isStroagePermissionGranted(AddPublicAddress.this, AddPublicAddress.this, 888)) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,44444);
                }

            }
        });

        imgLocationPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                if (UtilityClass.isStroagePermissionGranted(AddPublicAddress.this, AddPublicAddress.this, 888)) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,55555);
                }

            }
        });

        rlytSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                if (UtilityClass.isStroagePermissionGranted(AddPublicAddress.this, AddPublicAddress.this, 888)) {
                    getBusinessImage();
                }
            }
        });

        rcvImages.setLayoutManager(new GridLayoutManager(this,3));
        rcvImages.setAdapter(new AdapterBusinessImage(serviceBusinessImageList,this));
        rcvServices.setLayoutManager(new GridLayoutManager(this,3));
        rcvServices.setAdapter(new AdapterServiceImage(serviceImageList, this, new ImagePicker() {
            @Override
            public void pickImage() {
                if (UtilityClass.isStroagePermissionGranted(AddPublicAddress.this,AddPublicAddress.this,999)) {
                    getDocumnet();
                }
            }
        }));

        ArrayList<WorkingHour> myListData = new ArrayList<WorkingHour>();
        myListData.add(new WorkingHour("Sunday","1"));
        myListData.add(new WorkingHour("Monday","2"));
        myListData.add(new WorkingHour("Tuesday","3"));
        myListData.add(new WorkingHour("Wednesday","4"));
        myListData.add(new WorkingHour("Thursday","5"));
        myListData.add(new WorkingHour("Friday","6"));
        myListData.add(new WorkingHour("Saturday","7"));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvTiming.setLayoutManager(linearLayoutManager);
        rcvTiming.setHasFixedSize(true);
        AdaterHours custom_adaterFriends = new AdaterHours(getApplicationContext(),myListData);
        rcvTiming.setAdapter(custom_adaterFriends);




        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtBusinessName.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please enter business name.");
                }
                else {
                    UtilityClass.hideKeyboard(AddPublicAddress.this);
                    llytSlide1.setVisibility(View.GONE);
                    llytSlide2.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtvCategoryButton.getText().equals("Select Category")) {
                    new Message().showSnack(clytRootLayer, "Please select a category.");
                }
                else {
                    UtilityClass.hideKeyboard(AddPublicAddress.this);
                    llytSlide2.setVisibility(View.GONE);
                    llytSlide3.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide3Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtShortDescription.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please provide a description about yourself.");
                }
                else {
                    UtilityClass.hideKeyboard(AddPublicAddress.this);
                    llytSlide3.setVisibility(View.GONE);
                    llytSlide4.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide4Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UtilityClass.hideKeyboard(AddPublicAddress.this);
                    llytSlide4.setVisibility(View.GONE);
                    llytSlide5.setVisibility(View.VISIBLE);
            }
        });

        crdSlide5Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latLng == null) {
                    new Message().showSnack(clytRootLayer, "Select a valid address.");
                }
                else {
                    UtilityClass.hideKeyboard(AddPublicAddress.this);
                    llytSlide5.setVisibility(View.GONE);
                    llytSlide6.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide6Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide6.setVisibility(View.GONE);
                llytSlide7.setVisibility(View.VISIBLE);
            }
        });

        crdSlide7Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide7.setVisibility(View.GONE);
                llytSlide8.setVisibility(View.VISIBLE);
            }
        });

        crdSlide8Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                if (edtEmailId.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer,"Please enter an email id.");
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailId.getText().toString().trim()).matches()) {
                    new Message().showSnack(clytRootLayer, "Entered email id is not in correct format.");
                    return;
                } else if (edtPhoneNumber.getText().toString().trim().length() == 0) {
                    new Message().showSnack(clytRootLayer, "Please enter a contact number.");
                    return;

                }
                else if (edtPhoneNumber.getText().toString().trim().length() != 9) {
                    new Message().showSnack(clytRootLayer, "Contact number should of 9 digits.");
                    return;
                }
                else {
                    llytSlide8.setVisibility(View.GONE);
                    llytSlide9.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide9Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                //Code to put API of add address
                Boolean flag = false;
                for (int i = 0; i < ((AdaterHours)rcvTiming.getAdapter()).getMyListData().size(); i++) {
                    if (((AdaterHours)rcvTiming.getAdapter()).getMyListData().get(i).isOpen().equals("1")) {
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    new Message().showSnack(clytRootLayer,"Please provide information about your working hours.");
                }
                else {
                    addPublicAddress();
                }

            }
        });


        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });

        crdSlide3Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide3.setVisibility(View.GONE);
                llytSlide2.setVisibility(View.VISIBLE);
            }
        });

        crdSlide4Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide4.setVisibility(View.GONE);
                llytSlide3.setVisibility(View.VISIBLE);
            }
        });

        crdSlide5Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide5.setVisibility(View.GONE);
                llytSlide4.setVisibility(View.VISIBLE);
            }
        });

        crdSlide6Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide6.setVisibility(View.GONE);
                llytSlide5.setVisibility(View.VISIBLE);
            }
        });

        crdSlide7Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide7.setVisibility(View.GONE);
                llytSlide6.setVisibility(View.VISIBLE);
            }
        });

        crdSlide8Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide8.setVisibility(View.GONE);
                llytSlide7.setVisibility(View.VISIBLE);
            }
        });

        crdSlide9Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPublicAddress.this);
                llytSlide9.setVisibility(View.GONE);
                llytSlide8.setVisibility(View.VISIBLE);
            }
        });
        get_profile();
    }

    //Activity result method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 104 && resultCode == Activity.RESULT_OK) {
            categoryObject = (Category) data.getSerializableExtra("category");
            txtvCategoryButton.setText(categoryObject.getCategoryName());
        }
        else if (requestCode == 44444 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imgAddressLogo.setImageBitmap(bmp);
            imageBase64 = new UtilityClass().imageToBase64(imgAddressLogo);

        }
        else if (requestCode == 55555 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imgLocationPic.setImageBitmap(bmp);
            strLocationPicBase64 = new UtilityClass().imageToBase64(imgLocationPic);

        }
        else if (requestCode == 103 && resultCode == Activity.RESULT_OK) {
            String path = data.getData().toString();
            serviceBusinessImageList.add(path);
            rcvImages.getAdapter().notifyDataSetChanged();
        }
        else if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            String path = data.getData().toString();
            serviceImageList.add(path);
            rcvServices.getAdapter().notifyDataSetChanged();
        }
    }

    //..........................................Api...............................//
    public void get_profile() {
        try {
            if (UtilityClass.isNetworkConnected(AddPublicAddress.this)) {
                SaveUserDetail model = UtilityClass.getLoginData(AddPublicAddress.this);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, getSharedPreferences("Sesssion",Context.MODE_PRIVATE).getString(Constants.userId,""));

                UtilityClass.showLoading(Loader,AddPublicAddress.this);
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).getProfile(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        UtilityClass.hideLoading(Loader,AddPublicAddress.this);

                        if (response.isSuccessful()) {
                            try {

                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                String result_code = jsonObject.getString(Constants.RESULT_CODE);

                                if (result_code.equals(Constants.ZERO)) {
                                    new Message().showSnack(clytRootLayer, "This account has been deactivated from this platform.");

                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                    new Message().showSnack(clytRootLayer, "This account has been deleted from this platform.");
                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                    new Message().showSnack(clytRootLayer, "Something went wrong. Please try after some time.");

                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                                    new Message().showSnack(clytRootLayer, "No data found.");

                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                    new Message().showSnack(clytRootLayer, "This account has been blocked.");


                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                    new Message().showSnack(clytRootLayer, "All fields not sent.");

                                }
                                else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                    new Message().showSnack(clytRootLayer, " Please check the request method.");
                                }
                                else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                    JSONObject jsonObject1 = new JSONObject(result_data);

                                    edtEmailId.setText(jsonObject1.getString("emailId"));



                                    contactNumber = jsonObject1.getString("contactNumber");

                                    try{for(int i = 0;i<contactNumber.length();i++)
                                    {
                                        if(i<4)
                                            countryCode = countryCode + contactNumber.charAt(i);
                                        else
                                            phoneNumber = phoneNumber + contactNumber.charAt(i);
                                    }}
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    edtPhoneNumber.setText(phoneNumber);
                                    if (countryCode != null) {
                                        int spinnerPosition = ((ArrayAdapter<CharSequence>)(spinnerPhone.getAdapter())).getPosition(countryCode);
                                        spinnerPhone.setSelection(spinnerPosition);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                        UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                    }
                });


            } else {
                new Message().showSnack(clytRootLayer,Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }


    //Method related to location


    public void checkGPSAndPermission() {

        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPublicAddress.this);
            builder.setMessage(
                    "Your GPS seems to be disabled, please enable it from settings?")
                    .setCancelable(false).setPositiveButton("Settings",
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
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

        }
        else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        200);
            }
            else {
                UtilityClass.showLoading(Loader,AddPublicAddress.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            if (likelyPlaces.getCount() > 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(likelyPlaces.get(0).getPlace().getLatLng(),17f));

                                latLng = likelyPlaces.get(0).getPlace().getLatLng();
                            }
                            else {
                                new Message().showSnack(clytRootLayer, "Location can't be retrieved.");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng = mMap.getCameraPosition().target;
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoogleAutoCompletePlaceAdapter.PlaceAutoComplete pac = autoCompleteAdapter.getItem(position);
        if (pac != null) {
            String placeId = pac.getPlaceId();
            UtilityClass.hideKeyboard(AddPublicAddress.this);
            autoCompleteAdapter.getLatLong(placeId, new PlaceListner() {
                @Override
                public void latLong(LatLng latLng) {
                    AddPublicAddress.this.latLng = latLng;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
                }
            });
        }
    }

    //, GoogleApiClient.ConnectionCallbacks methods

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //, GoogleApiClient.OnConnectionFailedListener method

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Other methods

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void getBusinessImage() {
        if (serviceBusinessImageList.size() > 5) {
            new Message().showSnack(clytRootLayer, "You can only select 6 images.");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,103);
    }

    private void getDocumnet() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,102);

    }



    //Methods related to API consumption

    private ArrayList<ContactNumber> getPublicMobileNumber() {
        ArrayList<ContactNumber> arrayListner = new ArrayList<ContactNumber>();
        arrayListner.add(new ContactNumber(spinnerValue + edtPhoneNumber.getText().toString().trim()));
        return arrayListner;
    }


    private void addPublicAddress() {
        PublicAddressRequest request = new PublicAddressRequest();
        UtilityClass typefaceUtil = new UtilityClass();



        ArrayList<ContactNumber> listContact = getPublicMobileNumber();
        request.setLogoPicture(imageBase64);
        request.setUserId(getSharedPreferences("Sesssion",Context.MODE_PRIVATE).getString(Constants.userId,""));
        request.setShortName(edtBusinessName.getText().toString().trim());
        request.setLatitude(Double.toString(latLng.latitude));
        request.setLongitude(Double.toString(latLng.longitude));
        request.setAddress(edtAddress.getText().toString().trim());
        request.setEmailId(edtEmailId.getText().toString().trim());
        request.setDescription(edtShortDescription.getText().toString().trim());
        request.setCategoryId(categoryObject.getCategoryId());
        request.setContactNumber(listContact);
        request.setDeliveryAvailable((swtDelivery.isChecked())?"1":"0");
        request.setLandmark(edtLandmark.getText().toString().trim());
        request.setServiceDescription(edtServiceDescription.getText().toString().trim());
        request.setLocationPictureURL(strLocationPicBase64);


        SocialMedia social = new SocialMedia();
        social.setFacebook(edtFacebook.getText().toString().trim());
        social.setLinkedin(edtLinkedin.getText().toString().trim());
        social.setTwitter(edtTwitter.getText().toString().trim());
        social.setInstagram(edtInstagram.getText().toString().trim());
        social.setWebsite(edtWebsiteURL.getText().toString().trim());

        request.setSocialMedia(social);
        ArrayList<WorkingHour> arr = ((AdaterHours)rcvTiming.getAdapter()).getMyListData();
        for (int i = 0; i < arr.size(); i++) {

            if (arr.get(i).getOpenTime() == null) {
                arr.get(i).setOpenTime("12 AM");
            }
            if (arr.get(i).getCloseTime() == null) {
                arr.get(i).setCloseTime("12 AM");
            }
        }
        request.setWorkingHours(arr);

        if (request.getDescription().length() > 500) {
            new Message().showSnack(clytRootLayer, "Description can have max 500 characters.");
            return;
        }


        Call<Object> call;
        call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).addAddressPublic(request);
        UtilityClass.showLoading(Loader,AddPublicAddress.this);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        int result_code = jsonObject.getInt(Constants.RESULTCODE);
                        if (result_code == 1) {
                            handleImageUploading(jsonObject.getJSONObject(Constants.RESULTDATA).getString("addressId"));
                            return;
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
                        UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                        new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
            }
        });
        
    }

    private void handleImageUploading(String address_id) throws IOException {
//        val newList = ArrayList<String>()
//        newList.addAll(serviceBusinessImageList)
        if (serviceBusinessImageList.isEmpty()) {
            handleServiceFile(address_id);
        } else {
            String uri = serviceBusinessImageList.get(0);
            serviceBusinessImageList.remove(0);
            uploadBusinessImages(uri, address_id);
        }
    }

    private void uploadBusinessImages(String uri, final String addressID) throws IOException {
        PublicImageRequest request = new PublicImageRequest();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri));
        request.setImage(new UtilityClass().image_ToBase64(bitmap));
        request.setAddressId(addressID);
        Call<Object> call;
        call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).addAddressPublicImages(request);
        UtilityClass.showLoading(Loader,AddPublicAddress.this);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        int result_code = jsonObject.getInt(Constants.RESULTCODE);
                        if (result_code == 1) {
                            handleImageUploading(addressID);
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
                        new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
            }
        });
    }


    private void handleServiceFile(String addressID) throws IOException {
//        val newList = ArrayList<String>()
//        newList.addAll(serviceImageList)
        if (serviceImageList.isEmpty()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(AddPublicAddress.this);
            alertbox.setMessage("Your address added successfully.");
            alertbox.setTitle("Success");
            alertbox.setCancelable(false);
            alertbox.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0,
                                            int arg1) {

                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    });
            alertbox.show();
        } else {
            String uri = serviceImageList.get(0);
            serviceImageList.remove(0);
            addBusinessServicesApi(uri, addressID);
        }
    }

    private void addBusinessServicesApi(String uri, final String addressID) throws IOException {
        HashMap<String,String> hash = new HashMap<String, String>();
        AndroidNetworking.enableLogging();
        hash.put("serviceId","-1");
        hash.put("addressId",addressID);
        File file = FileUtil.from(this, Uri.parse(uri));
        UtilityClass.showLoading(Loader,AddPublicAddress.this);
        ANRequest anrRequest = AndroidNetworking.upload(Constants.BASE_URL +  "index.php/Api/" + Constants.addPublicAddressService).addMultipartFile("attachment",file).addMultipartParameter(hash).setTag("").setPriority(com.androidnetworking.common.Priority.HIGH).build();
        anrRequest.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                try {

                    int resultCode = response.getInt(Constants.RESULTCODE);
                    if (resultCode == 1) {
                        handleServiceFile(addressID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                }
            }

            @Override
            public void onError(ANError anError) {
                UtilityClass.hideLoading(Loader,AddPublicAddress.this);
                new Message().showSnack(clytRootLayer, "Something went wrong ${anError.message}.");
            }
        });


    }
}



