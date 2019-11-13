package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.codeapex.simplrpostprod.Adapter.GoogleAutoCompletePlaceAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ContactNumber;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddressRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
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
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPrivateAddress extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageView imgBack,imgLocationPic, imgLocationCrossButton;
    CardView crdSlide1Next, crdSlide2Next, crdSlide3Next, crdSlide4Next, crdSlide2Previous, crdSlide3Previous, crdSlide4Previous;
    LinearLayout llytSlide1, llytSlide2, llytSlide3, llytSlide4;
    EditText edtShortName, edtAddress, edtLandmark, edtEmailId, edtPhoneNumber;
    AutoCompleteTextView atxtvAddress;
    TextView txtvCurrentLocation;
    SupportMapFragment mapView;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;
    Spinner spinnerPhone;
    SharedPreferences sharedPreferences;

    String contactNumber, countryCode = "", phoneNumber ="", spinnerValue;


    GoogleAutoCompletePlaceAdapter autoCompleteAdapter;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mMap;
    PlaceDetectionClient mPlaceDetectionClient;
    String imageBase64, strLocationPicBase64 = "";
    UtilityClass typefaceUtil;

    ScrollView scrollView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_private_address);


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
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide3Next = findViewById(R.id.crdSlide3Next);
        crdSlide4Next = findViewById(R.id.crdSlide4Next);
        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        crdSlide3Previous = findViewById(R.id.crdSlide3Previous);
        crdSlide4Previous = findViewById(R.id.crdSlide4Previous);
        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        llytSlide3 = findViewById(R.id.slide3);
        llytSlide4 = findViewById(R.id.slide4);
        edtShortName = findViewById(R.id.edtBusinessName);
        edtAddress = findViewById(R.id.edtAddress);
        edtLandmark = findViewById(R.id.edtLandmark);
        atxtvAddress = findViewById(R.id.address_txt);
        txtvCurrentLocation = findViewById(R.id.txt_current_location);
        imgLocationPic = findViewById(R.id.imgLocationPic);
        edtEmailId = findViewById(R.id.txt_pub_email);
        edtPhoneNumber = findViewById(R.id.txtvPhoneNumber);
        imgLocationCrossButton = findViewById(R.id.locationCross);
        Loader = findViewById(R.id.Loader);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        imgLocationCrossButton = findViewById(R.id.locationCross);
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


        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();
//        .setCountry("IN")
        autoCompleteAdapter = new GoogleAutoCompletePlaceAdapter(this,filter);

        atxtvAddress.setAdapter(autoCompleteAdapter);
        atxtvAddress.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        txtvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                atxtvAddress.clearFocus();
                atxtvAddress.setText("");
                checkGPSAndPermission();
            }
        });
        checkGPSAndPermission();
        mapView.getMapAsync(AddPrivateAddress.this);

        imgLocationCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atxtvAddress.setText("");
            }
        });

        imgLocationPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,55555);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                if (edtShortName.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please enter short name.");
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
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
//                if (edtAddress.getText().toString().trim().equals("")) {
//                    new Message().showSnack(clytRootLayer, "Please enter address.");
//                }
//                else {
                    llytSlide2.setVisibility(View.GONE);
                    llytSlide3.setVisibility(View.VISIBLE);
//                }
            }
        });

        crdSlide3Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                llytSlide3.setVisibility(View.GONE);
                llytSlide4.setVisibility(View.VISIBLE);
            }
        });

        crdSlide4Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                //Code to call the API to save the primary information
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
                    add_privateaddress();
                }
            }
        });

        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });

        crdSlide3Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                llytSlide3.setVisibility(View.GONE);
                llytSlide2.setVisibility(View.VISIBLE);
            }
        });

        crdSlide4Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(AddPrivateAddress.this);
                llytSlide4.setVisibility(View.GONE);
                llytSlide3.setVisibility(View.VISIBLE);
            }
        });
        get_profile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 55555 && resultCode == Activity.RESULT_OK) {
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

    }


    //Method related to API

    private void add_privateaddress() {
        typefaceUtil = new UtilityClass();
        ArrayList<ContactNumber> arrayListner = new ArrayList<ContactNumber>();
        arrayListner.add(new ContactNumber(spinnerValue + edtPhoneNumber.getText().toString().trim()));
        PrivateAddressRequest request = new PrivateAddressRequest();
        request.setAddressPicture(strLocationPicBase64);
        request.setUserId(getSharedPreferences("Sesssion",Context.MODE_PRIVATE).getString(Constants.userId,""));
        request.setShortName(edtShortName.getText().toString().trim());
        request.setLatitude(Double.toString(latLng.latitude));
        request.setLongitude(Double.toString(latLng.longitude));
        request.setAddress(edtAddress.getText().toString().trim());
        request.setLandmark(edtLandmark.getText().toString().trim());
        request.setEmailId(edtEmailId.getText().toString().trim());
        request.setContactNumber(arrayListner);







        Call<Object> call;
        call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).addAddressPrivate(request);
        UtilityClass.showLoading(Loader,AddPrivateAddress.this);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        int result_code = jsonObject.getInt(Constants.RESULTCODE);
                        if (result_code == 1) {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(AddPrivateAddress.this);
                            alertbox.setMessage("Your address added successfully.");
                            alertbox.setTitle("Success");

                            alertbox.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            Intent intent = new Intent();
                                            setResult(Activity.RESULT_OK,intent);
                                            finish();
                                        }
                                    });
                            alertbox.setCancelable(false);
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
                        UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
                        new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
                new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
            }
        });





    }

    //..........................................Api...............................//
    public void get_profile() {
        try {
            if (UtilityClass.isNetworkConnected(AddPrivateAddress.this)) {
                SaveUserDetail model = UtilityClass.getLoginData(AddPrivateAddress.this);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, getSharedPreferences("Sesssion",Context.MODE_PRIVATE).getString(Constants.userId,""));

                UtilityClass.showLoading(Loader,AddPrivateAddress.this);
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).getProfile(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        UtilityClass.hideLoading(Loader,AddPrivateAddress.this);

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

                        UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPrivateAddress.this);
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
                            finish();
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
                UtilityClass.showLoading(Loader,AddPrivateAddress.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UtilityClass.showLoading(Loader,AddPrivateAddress.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,AddPrivateAddress.this);
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
            else {
                finish();
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
            UtilityClass.hideKeyboard(AddPrivateAddress.this);
            autoCompleteAdapter.getLatLong(placeId, new PlaceListner() {
                @Override
                public void latLong(LatLng latLng) {
                    AddPrivateAddress.this.latLng = latLng;
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


}
