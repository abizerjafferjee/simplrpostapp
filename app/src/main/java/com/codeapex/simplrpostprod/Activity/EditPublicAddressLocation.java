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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdapterBusinessImage;
import com.codeapex.simplrpostprod.Adapter.GoogleAutoCompletePlaceAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.ModelClass.PublicImageRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPublicAddressLocation extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    ImageView imgBack, imgLocationCrossButton, imgAddressLogo, imgLocationPic;
    CardView crdSlide1Next, crdSlide2Next, crdSlide3Next, crdSlide2Previous, crdSlide3Previous;
    LinearLayout llytSlide1, llytSlide2, llytSlide3;
    EditText edtAddress, edtLandmark;
    AutoCompleteTextView atxtvAddress;
    TextView txtvCurrentLocation;
    LinearLayout llytImagePickerButton;
    RelativeLayout rlytSelectButton;
    RecyclerView rcvImages;
    ProgressBar Loader;
    SupportMapFragment mapView;


    GoogleAutoCompletePlaceAdapter autoCompleteAdapter;
    String imageBase64,strLocationPicBase64,latitude,longitude;
    PublicAddressRequest publicAddressData;
    ConstraintLayout clytRootLayer;
    JSONObject jsonObject;
    ArrayList<String> serviceBusinessImageList = new ArrayList<String>();
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mMap;
    PlaceDetectionClient mPlaceDetectionClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_public_address_location);

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
        edtAddress = findViewById(R.id.edtAddress);
        edtLandmark = findViewById(R.id.edtLandmark);
        atxtvAddress = findViewById(R.id.address_txt);
        imgLocationCrossButton = findViewById(R.id.locationCross);
        txtvCurrentLocation = findViewById(R.id.txt_current_location);
        imgAddressLogo = findViewById(R.id.img_pub_logo);
        imgLocationPic = findViewById(R.id.imgLocationPic);
        llytImagePickerButton = findViewById(R.id.llytImagePickerButton);
        rlytSelectButton = findViewById(R.id.rlytSelectButton);
        rcvImages = findViewById(R.id.rv_images);
        Loader = findViewById(R.id.Loader);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);








        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();
//        .setCountry("IN")
        autoCompleteAdapter = new GoogleAutoCompletePlaceAdapter(this,filter);

        atxtvAddress.setAdapter(autoCompleteAdapter);
        atxtvAddress.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        txtvCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                atxtvAddress.clearFocus();
                atxtvAddress.setText("");
                checkGPSAndPermission();
            }
        });
        mapView.getMapAsync(EditPublicAddressLocation.this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,44444);
            }
        });

        imgLocationPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,55555);
            }
        });

        rlytSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                if (UtilityClass.isStroagePermissionGranted(EditPublicAddressLocation.this, EditPublicAddressLocation.this, 888)) {
                    getBusinessImage();
                }
            }
        });

        rcvImages.setLayoutManager(new GridLayoutManager(this,3));
        rcvImages.setAdapter(new AdapterBusinessImage(serviceBusinessImageList,this));

        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
//                if (edtAddress.getText().toString().trim().equals("")) {
//                    new Message().showSnack(clytRootLayer, "Please enter an address.");
//                }
//                else {
                    llytSlide1.setVisibility(View.GONE);
                    llytSlide2.setVisibility(View.VISIBLE);
//                }
            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                llytSlide2.setVisibility(View.GONE);
                llytSlide3.setVisibility(View.VISIBLE);
            }
        });

        crdSlide3Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
                //Code to call the API to save the primary information
                try {
                    editPublicAddressLocation();
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

        crdSlide3Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llytSlide3.setVisibility(View.GONE);
                llytSlide2.setVisibility(View.VISIBLE);
            }
        });


        if (getIntent().hasExtra("publicAddressData")) {
            publicAddressData = (PublicAddressRequest) getIntent().getSerializableExtra("publicAddressData");
            edtAddress.setText(publicAddressData.getAddress());
            edtLandmark.setText(publicAddressData.getLandmark());
            latitude = publicAddressData.getLatitude();
            longitude = publicAddressData.getLongitude();
            if (publicAddressData.getImages() != null) {
                for (int i = 0; i<publicAddressData.getImages().size(); i++) {
                    serviceBusinessImageList.add(Constants.IMG_URL + publicAddressData.getImages().get(i).getImageURL());
                }
                rcvImages.getAdapter().notifyDataSetChanged();
            }
            latLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));



//            Glide.with(this).load(Constants.IMG_URL + publicAddressData.getPictureURL()).error(Glide.with(this).load(R.drawable.placeholder))
//                    .into(imgAddressLogo);


            Picasso.with(this)
                    .load(Constants.IMG_URL.concat(publicAddressData.getPictureURL()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.placeholder)
                    .into(imgAddressLogo, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });


//            Glide.with(this).load(Constants.IMG_URL + publicAddressData.getLocationPictureURL()).error(Glide.with(this).load(R.drawable.placeholder))
//                    .into(imgLocationPic);

            Picasso.with(this)
                    .load(Constants.IMG_URL.concat(publicAddressData.getLocationPictureURL()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.placeholder)
                    .into(imgLocationPic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 44444 && resultCode == Activity.RESULT_OK) {
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
            final InputStream imageStream;
            try {
                imageStream = this.getContentResolver().openInputStream(selectedImage);
                final Bitmap image = BitmapFactory.decodeStream(imageStream);
                imageBase64 = encodeImage(image);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
            final InputStream imageStream;
            try {
                imageStream = this.getContentResolver().openInputStream(selectedImage);
                final Bitmap image = BitmapFactory.decodeStream(imageStream);
                strLocationPicBase64 = encodeImage(image);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 103 && resultCode == Activity.RESULT_OK) {
            String path = data.getData().toString();
            serviceBusinessImageList.add(path);
            rcvImages.getAdapter().notifyDataSetChanged();
        }
    }

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


    public void checkGPSAndPermission() {

        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditPublicAddressLocation.this);
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
                UtilityClass.showLoading(Loader,EditPublicAddressLocation.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
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
                UtilityClass.showLoading(Loader,EditPublicAddressLocation.this);
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoogleAutoCompletePlaceAdapter.PlaceAutoComplete pac = autoCompleteAdapter.getItem(position);
        if (pac != null) {
            String placeId = pac.getPlaceId();
            UtilityClass.hideKeyboard(EditPublicAddressLocation.this);
            autoCompleteAdapter.getLatLong(placeId, new PlaceListner() {
                @Override
                public void latLong(LatLng latLng) {
                    EditPublicAddressLocation.this.latLng = latLng;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f));
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng = mMap.getCameraPosition().target;
            }
        });
    }


    //API related methods

    private void editPublicAddressLocation() throws JSONException {

        if (UtilityClass.isNetworkConnected(EditPublicAddressLocation.this)) {
            PublicAddressRequest request = new PublicAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();



            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,""));
            request.setAddressId(publicAddressData.getAddressId());
            request.setAddress(edtAddress.getText().toString().trim());
            request.setLandmark(edtLandmark.getText().toString().trim());
            request.setLatitude(Double.toString(latLng.latitude));
            request.setLongitude(Double.toString(latLng.longitude));
            request.setLocationPictureURL(typefaceUtil.imageToBase64(imgLocationPic));
            request.setLogoPicture(typefaceUtil.imageToBase64(imgAddressLogo));



            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPublicLocation(request);
            UtilityClass.showLoading(Loader,EditPublicAddressLocation.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {


                                handleImageUploading(publicAddressData.getAddressId());





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
                            UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }



    }


    private void handleImageUploading(String address_id) throws IOException {
//        val newList = ArrayList<String>()
//        newList.addAll(serviceBusinessImageList)
        if (serviceBusinessImageList.isEmpty()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPublicAddressLocation.this);
            alertbox.setMessage("Your address updated successfully.");
            alertbox.setTitle("Success");

            alertbox.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0,
                                            int arg1) {

                            finish();
                        }
                    });
            alertbox.show();
        } else {
            uploadBusinessImages(serviceBusinessImageList.get(0), address_id);
            serviceBusinessImageList.remove(0);
        }
    }

    private void uploadBusinessImages(String uri, final String addressID) throws IOException {
        PublicImageRequest request = new PublicImageRequest();
        if (uri.substring(0,4).equals("http")) {
            String[] strArray1 = uri.split("/");
            String id = strArray1[strArray1.length - 1];
            id = id.split("\\.")[0];
            request.setImageId(id);
            request.setImage("");
        }
        else {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri));
            request.setImage(new UtilityClass().image_ToBase64(bitmap));
            request.setImageId("-1");
        }


        request.setAddressId(addressID);
        Call<Object> call;
        call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).addAddressPublicImages(request);
        UtilityClass.showLoading(Loader,EditPublicAddressLocation.this);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
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
                UtilityClass.hideLoading(Loader,EditPublicAddressLocation.this);
                new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
            }
        });
    }
}
