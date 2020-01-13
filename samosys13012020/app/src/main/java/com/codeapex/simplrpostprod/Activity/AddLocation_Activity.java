/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.androidnetworking.utils.Utils;
import com.codeapex.simplrpostprod.Adapter.AutoCompleteAdapter;
import com.codeapex.simplrpostprod.LocalDatabase.SimplrPostDBOpenHelper;
import com.codeapex.simplrpostprod.ModelClass.PlacePredictions;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.VolleyJSONRequest;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.codeapex.simplrpostprod.opencodelocation.code.CodeView;
import com.codeapex.simplrpostprod.opencodelocation.main.MainPresenter;
import com.codeapex.simplrpostprod.opencodelocation.map.MyMapView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Currency;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.MyApplication;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

import android.location.LocationListener;
import android.location.Location;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;


/**
 * The home {@link android.app.Activity}. All features are implemented in this Activity. The
 * features with a UI are code, direction, map, and search. Additionally, the app also obtains the
 * current location of the user.
 * <p/>
 * The UI features all live in their own package, with a Contract interface defining the methods on
 * their view(s) and their action listeners. The action listener interface is implemented via a
 * feature Presenter, and the view interface via a {@link android.view.View}.
 * <br />
 * Note that some features have a source and target view, the source view being the UI that the
 * user interacts with for that feature, and the target view being a view that needs to update its
 * data based on an action in that feature (eg Search result).
 * <p/>
 * The AddLocation_Activity also has a presenter, the {@link MainPresenter}, which implements the user
 * location feature. As some features need to know the user location and the app consists of one
 * Activity only, the {@link MainPresenter} is made accessible to the other features via a static
 * reference.
 */
public class AddLocation_Activity extends FragmentActivity implements Response.Listener<String>, Response.ErrorListener, LocationListener {

    private LinearLayout bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout layout_main;
    private ImageButton btn_save, btn_share, btn_infoPublicPrivate, btn_back, btn_directionInfo;
    private TextView txt_headerPublic, txt_headerPrivate;
    private Switch btn_switchPublicPrivate;
    private ImageView img_street, img_building, img_entrance, btn_img_streetClose, btn_img_buildingClose, btn_img_entranceClose;
    private ImageButton btn_search, btn_dropDown, btn_plusCodeInfo, btn_uniqueLinkInfo, btnCurrentLocation;
    private EditText edt_uniqueLink, edt_country, edt_city, edt_streetName, edt_buildingName, edt_entranceName, edt_directionTxt;
    private View view_space;
    private ScrollView bottom_scroll;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private int RESULT_LOAD_STREET_IMAGE = 11111;
    private int RESULT_LOAD_BUILDING_IMAGE = 22222;
    private int RESULT_LOAD_ENTRANCE_IMAGE = 33333;
    private String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};
    private File street_imageFile = null, building_imageFile = null, entrance_imageFile = null;
    private String address_id, user_id, from, plus_code, public_private_tag = "public", qr_code_img, street_img, building_img, entrance_img, address_unique_link, country, city, street, building, entrance, direction_txt;
    private double latitude, longitude;
    private DisplayMetrics metrics;
    private EditText edt_search;
    private SharedPreferences sharedPreferences;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9.-]+$";
    private Pattern pattern;
    private Boolean isCamera = false;
    private String street_img_type = "Street", building_img_type = "Building", entrance_img_type = "Floor";
    private ProgressBar progressBar;
    int lastZoom = -1;
    private String preFilledText;
    private Handler handler;
    private VolleyJSONRequest request;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions;
    private ListView mAutoCompleteList;
    private AutoCompleteAdapter mAutoCompleteAdapter;

    LatLng CURRENT_LATLONG = null;
    LatLng SEARCHED_LATLONG = null;
    Marker marker;
    protected LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    String provider;
    private boolean isSearched = false;
    private boolean isCameraIdealActive = false;
    private boolean isFromEdit = false;
    private SimplrPostDBOpenHelper dbHandler;

    private static final String TAG = AddLocation_Activity.class.getSimpleName();

    private static final String MAP_CAMERA_POSITION_LATITUDE = "map_camera_position_latitude";

    private static final String MAP_CAMERA_POSITION_LONGITUDE = "map_camera_position_longitude";

    private static final String MAP_CAMERA_POSITION_ZOOM = "map_camera_position_zoom";

    private static final String URI_QUERY_SEPARATOR = "q=";

    private static final String URI_ZOOM_SEPARATOR = "&";

    /**
     * As all features are implemented in this activity, a static {@link MainPresenter} allows all
     * features to access its data without passing a reference to it to each feature presenter.
     */
    private static MainPresenter mMainPresenter;

    // We need to store this because we need to call this at different point in the lifecycle
    private MapView mMapView;

    MultipartBody.Part street_image;
    MultipartBody.Part building_image;
    MultipartBody.Part entrance_image;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);

        dbHandler = new SimplrPostDBOpenHelper(this, null);

        if (hasPermissions(PERMISSIONS)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        //find views
        sharedPreferences = this.getSharedPreferences("Sesssion", MODE_PRIVATE);
        findIds();
        pattern = Pattern.compile(USERNAME_PATTERN);
        getIntentFromActivity();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        MyMapView myMapView = findViewById(R.id.myMapView);
        mMapView = myMapView.getMapView();
        mMainPresenter =
                new MainPresenter(
                        this,
                        findViewById(R.id.codeView),
                        myMapView);

        mMapView.onCreate(savedInstanceState);

        if (getIntent() != null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            handleGeoIntent(getIntent());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        mMainPresenter.loadCurrentLocation();
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();

        mMainPresenter.stopListeningForLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        CameraPosition currentMapCameraPosition = mMainPresenter.getMapCameraPosition();
        Log.i(TAG, "Saving state");
        if (currentMapCameraPosition != null) {
            savedInstanceState.putDouble(
                    MAP_CAMERA_POSITION_LATITUDE, currentMapCameraPosition.target.latitude);
            savedInstanceState.putDouble(
                    MAP_CAMERA_POSITION_LONGITUDE, currentMapCameraPosition.target.longitude);
            savedInstanceState.putFloat(MAP_CAMERA_POSITION_ZOOM, currentMapCameraPosition.zoom);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "Restoring state");
        if (savedInstanceState != null) {
            double mapCameraPositionLatitude =
                    savedInstanceState.getDouble(MAP_CAMERA_POSITION_LATITUDE);
            double mapCameraPositionLongitude =
                    savedInstanceState.getDouble(MAP_CAMERA_POSITION_LONGITUDE);
            float mapCameraPositionZoom = savedInstanceState.getFloat(MAP_CAMERA_POSITION_ZOOM);
            mMainPresenter.setMapCameraPosition(
                    mapCameraPositionLatitude, mapCameraPositionLongitude, mapCameraPositionZoom);
        }
    }

    /**
     * Handles intent URIs, extracts the query part and sends it to the search function.
     * <p/>
     * URIs may be of the form:
     * <ul>
     * <li>{@code geo:37.802,-122.41962}
     * <li>{@code geo:37.802,-122.41962?q=7C66CM4X%2BC34&z=20}
     * <li>{@code geo:0,0?q=WF59%2BX67%20Praia}
     * </ul>
     * <p/>
     * Only the query string is used. Coordinates and zoom level are ignored. If the query string
     * is not recognised by the search function (say, it's a street address), it will fail.
     */
    private void handleGeoIntent(Intent intent) {
        Uri uri = intent != null ? intent.getData() : null;
        if (uri == null) {
            return;
        }
        String schemeSpecificPart = uri.getEncodedSchemeSpecificPart();
        if (schemeSpecificPart == null || schemeSpecificPart.isEmpty()) {
            return;
        }
        // Get everything after q=
        int queryIndex = schemeSpecificPart.indexOf(URI_QUERY_SEPARATOR);
        if (queryIndex == -1) {
            return;
        }
        String searchQuery = schemeSpecificPart.substring(queryIndex + 2);
        if (searchQuery.contains(URI_ZOOM_SEPARATOR)) {
            searchQuery = searchQuery.substring(0, searchQuery.indexOf(URI_ZOOM_SEPARATOR));
        }
        final String searchString = Uri.decode(searchQuery);
        Log.i(TAG, "Search string is " + searchString);

    }

    public static MainPresenter getMainPresenter() {
        return mMainPresenter;
    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.e("location:", "AJAY" + location.getLatitude());
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 2000, this);

        if (location != null) {}*/

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            {
                                // Logic to handle location object
                                CURRENT_LATLONG = new LatLng(location.getLatitude(), location.getLongitude());

                                if (!from.equals("edit")) {

                                    getAddressNameByLocation(CURRENT_LATLONG, "edit");

                                } else {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            MyMapView.mMap.moveCamera(CameraUpdateFactory.zoomTo(19.0f));
                                            MyMapView.mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));


                                        }
                                    }, 1500);

                                }
                            }
                        }
                    }
                });
    }

    private void getIntentFromActivity() {

        user_id = this.getIntent().getStringExtra("user_id");
        from = this.getIntent().getStringExtra("from");
        address_id = this.getIntent().getStringExtra("address_id");
        if (this.getIntent().getStringExtra("latitude") != null && !this.getIntent().getStringExtra("latitude").equals("")) {
            latitude = Double.parseDouble(this.getIntent().getStringExtra("latitude"));
        }
        if (this.getIntent().getStringExtra("longitude") != null && !this.getIntent().getStringExtra("longitude").equals("")) {
            longitude = Double.parseDouble(this.getIntent().getStringExtra("longitude"));
        }
        address_id = this.getIntent().getStringExtra("address_id");
        plus_code = this.getIntent().getStringExtra("plus_code");
        public_private_tag = this.getIntent().getStringExtra("public_private_tag");
        if (public_private_tag == null || public_private_tag.equals("")) {
            public_private_tag = "public";
        }
        qr_code_img = this.getIntent().getStringExtra("qr_code_img");
        street_img = this.getIntent().getStringExtra("street_img");
        building_img = this.getIntent().getStringExtra("building_img");
        entrance_img = this.getIntent().getStringExtra("entrance_img");
        address_unique_link = this.getIntent().getStringExtra("address_unique_link");
        country = this.getIntent().getStringExtra("country");
        city = this.getIntent().getStringExtra("city");
        street = this.getIntent().getStringExtra("street");
        building = this.getIntent().getStringExtra("building");
        entrance = this.getIntent().getStringExtra("entrance");
        street_img_type = this.getIntent().getStringExtra("street_img_type");
        building_img_type = this.getIntent().getStringExtra("building_img_type");
        entrance_img_type = this.getIntent().getStringExtra("entrance_img_type");

        if (from.equals("edit")) {
            edt_uniqueLink.setEnabled(false);
        } else {
            isFromEdit = true;
            edt_uniqueLink.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
        }
        if (from.equals("share")) {
            btn_save.setVisibility(View.GONE);
            btn_share.setVisibility(View.VISIBLE);
        } else {
            isFromEdit = true;
            btn_save.setVisibility(View.VISIBLE);
            btn_share.setVisibility(View.GONE);
        }

        if (!UtilityClass.isNetworkConnected(this)) {
            progressBar.setVisibility(View.GONE);
        }
        direction_txt = this.getIntent().getStringExtra("direction_txt");

        if (!public_private_tag.isEmpty()) {
            if (public_private_tag.equals("public")) {
                btn_switchPublicPrivate.setChecked(false);
                txt_headerPublic.setTextColor(getResources().getColor(R.color.colorWhite));
                txt_headerPrivate.setTextColor(getResources().getColor(R.color.colorgray_));
            } else {
                btn_switchPublicPrivate.setChecked(true);
                txt_headerPublic.setTextColor(getResources().getColor(R.color.colorgray_));
                txt_headerPrivate.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }

        if (!street_img.equals("null") || street_img != null) {
            if (!street_img.equals("") && !street_img.contains("address_default_image.png")) {
                Picasso.with(AddLocation_Activity.this).load(Constants.IMG_URL + street_img.replace("uploads/", "")).into(img_street);
                btn_img_streetClose.setVisibility(View.VISIBLE);
                img_street.setEnabled(false);
            } else {
                street_imageFile = null;
            }
        }
        if (!building_img.equals("null") || building_img != null) {
            if (!building_img.equals("") && !building_img.contains("address_default_image.png")) {
                Picasso.with(AddLocation_Activity.this).load(Constants.IMG_URL + building_img.replace("uploads/", "")).into(img_building);
                btn_img_buildingClose.setVisibility(View.VISIBLE);
                img_building.setEnabled(false);
            } else {
                building_imageFile = null;
            }
        }
        if (!entrance_img.equals("null") || entrance_img != null) {
            if (!entrance_img.equals("") && !entrance_img.contains("address_default_image.png")) {
                Picasso.with(AddLocation_Activity.this).load(Constants.IMG_URL + entrance_img.replace("uploads/", "")).into(img_entrance);
                btn_img_entranceClose.setVisibility(View.VISIBLE);
                img_entrance.setEnabled(false);
            } else {
                entrance_imageFile = null;
            }
        }
        if (plus_code != null & !plus_code.isEmpty()) {
            if (CodeView.mCodeTV != null)
                CodeView.mCodeTV.setText(plus_code);
        }

        String url = address_unique_link;
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String[] segments = uri.getPath().split("/");
        String idStr = segments[segments.length - 1];
        String prefix = idStr.replace("address-", "");

        edt_uniqueLink.setText(prefix);
        edt_country.setText(country);
        edt_city.setText(city);
        edt_streetName.setText(street);
        edt_buildingName.setText(building);
        edt_entranceName.setText(entrance);
        edt_directionTxt.setText(direction_txt);
    }

    private void findIds() {
        //find views ids
        bottom_sheet = findViewById(R.id.bottom_sheet);
        layout_main = findViewById(R.id.layout_main);
        btn_infoPublicPrivate = findViewById(R.id.btn_infoPublicPrivate);
        btn_back = findViewById(R.id.btn_back);
        btn_save = findViewById(R.id.btn_save);
        txt_headerPublic = findViewById(R.id.txt_headerPublic);
        txt_headerPrivate = findViewById(R.id.txt_headerPrivate);
        btn_switchPublicPrivate = findViewById(R.id.btn_switchPublicPrivate);
        btn_dropDown = findViewById(R.id.btn_dropDown);
        btn_plusCodeInfo = findViewById(R.id.btn_plusCodeInfo);
        btn_uniqueLinkInfo = findViewById(R.id.btn_uniqueLinkInfo);
        img_street = findViewById(R.id.img_street);
        img_building = findViewById(R.id.img_building);
        img_entrance = findViewById(R.id.img_entrance);
        btn_img_streetClose = findViewById(R.id.btn_img_streetClose);
        btn_img_buildingClose = findViewById(R.id.btn_img_buildingClose);
        btn_img_entranceClose = findViewById(R.id.btn_img_entranceClose);
        edt_uniqueLink = findViewById(R.id.edt_uniqueLink);
        edt_country = findViewById(R.id.edt_country);
        edt_city = findViewById(R.id.edt_city);
        edt_streetName = findViewById(R.id.edt_streetName);
        edt_buildingName = findViewById(R.id.edt_buildingName);
        edt_entranceName = findViewById(R.id.edt_entranceName);
        edt_directionTxt = findViewById(R.id.edt_directionTxt);
        view_space = findViewById(R.id.view_space);
        btnCurrentLocation = findViewById(R.id.btn_currentLocation);
        progressBar = findViewById(R.id.progressBar);
        edt_search = findViewById(R.id.edt_search);
        btn_search = findViewById(R.id.btn_search);
        mAutoCompleteList = findViewById(R.id.searchResultLV);
        btn_directionInfo = findViewById(R.id.btn_directionInfo);
        bottom_scroll = findViewById(R.id.bottom_scroll);
        btn_share = findViewById(R.id.btn_share_);

        edt_country.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edt_city.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edt_streetName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edt_buildingName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edt_entranceName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        edt_directionTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if (getIntent().hasExtra("Search Text")) {
            preFilledText = getIntent().getStringExtra("Search Text");
        }

        //Add a text change listener to implement autocomplete functionality
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars

                if (isSearched == false) {
                    if (edt_search.getText().length() > 3) {

                        btn_search.setVisibility(View.VISIBLE);
                        Runnable run = new Runnable() {

                            @Override
                            public void run() {

                                // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                                MyApplication.volleyQueueInstance.cancelRequestInQueue(GETPLACESHIT);

                                //build Get url of Place Autocomplete and hit the url to fetch result.
                                request = new VolleyJSONRequest(Request.Method.GET, getPlaceAutoCompleteUrl_(edt_search.getText().toString()), null, null, AddLocation_Activity.this, AddLocation_Activity.this);

                                //Give a tag to your request so that you can use this tag to cancle request later.
                                request.setTag(GETPLACESHIT);

                                MyApplication.volleyQueueInstance.addToRequestQueue(request);

                            }

                        };

                        // only canceling the network calls will not help, you need to remove all callbacks as well
                        // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        } else {
                            handler = new Handler();
                        }
                        handler.postDelayed(run, 1000);

                    } else {
                        btn_search.setVisibility(View.GONE);
                    }
                }
            }

        });
        edt_search.setText(preFilledText);
        edt_search.setSelection(edt_search.getText().length());
        edt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearched = false;
                isCameraIdealActive = false;
            }
        });

        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                isSearched = true;
                isFromEdit = false;
                isCameraIdealActive = false;
                edt_search.setText("");
                edt_search.setText(predictions.getPlaces().get(position).getPlaceDesc());
                edt_search.setSelection(edt_search.getText().length());
                hideKeyboard(AddLocation_Activity.this);
                if (predictions != null && mAutoCompleteAdapter != null) {
                    getClientAddressFromAddress(predictions.getPlaces().get(position).getPlaceDesc());
                    predictions = null;
                    mAutoCompleteAdapter.clear();
                    mAutoCompleteAdapter.notifyDataSetChanged();
                }


            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClientAddressFromAddress(edt_search.getText().toString());
                isSearched = true;
                isFromEdit = false;
                isCameraIdealActive = false;
                hideKeyboard(AddLocation_Activity.this);
                if (predictions != null && mAutoCompleteAdapter != null) {
                    predictions = null;
                    mAutoCompleteAdapter.clear();
                    mAutoCompleteAdapter.notifyDataSetChanged();
                }
                btn_search.setVisibility(View.GONE);
            }
        });

        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (CURRENT_LATLONG != null) {
                    btnCurrentLocation.setEnabled(false);

                    getLocation();
                    Location targetLocation = new Location("");//provider name is unnecessary
                    targetLocation.setLatitude(CURRENT_LATLONG.latitude);//your coords of course
                    targetLocation.setLongitude(CURRENT_LATLONG.longitude);
                    edt_country.setText("");
                    edt_city.setText("");
                    edt_streetName.setText("");
                    edt_buildingName.setText("");
                    edt_entranceName.setText("");
                    getAddressNameByLocation(CURRENT_LATLONG, "current");
                    isCameraIdealActive = false;
                }
            }
        });


        //set up bottom sheet
        bottomSheetSetup();
        //setBottom_sheetHeight();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_switchPublicPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    txt_headerPrivate.setTextColor(Color.WHITE);
                    txt_headerPublic.setTextColor(getResources().getColor(R.color.colorgray_));
                    public_private_tag = "private";
                } else {

                    txt_headerPublic.setTextColor(Color.WHITE);
                    txt_headerPrivate.setTextColor(getResources().getColor(R.color.colorgray_));
                    public_private_tag = "public";
                }

            }
        });

        btn_infoPublicPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_publicPrivateTipAlert();
            }
        });

        btn_directionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_directionTipAlert();
            }
        });

        btn_plusCodeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_PlusCodeTipAlert();
            }
        });

        btn_uniqueLinkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_uniqueLinkTipAlert();
            }
        });

        btn_dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag().toString().equals("down")) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btn_dropDown.setImageResource(R.drawable.card_arrow_down);
                    btn_dropDown.setTag("up");
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btn_dropDown.setImageResource(R.drawable.card_arrow_up);
                    btn_dropDown.setTag("down");
                }
            }
        });

        img_street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_imageTipAlert("street");
            }
        });

        img_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_imageTipAlert("building");
            }
        });

        img_entrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_imageTipAlert("entrance");
            }
        });

        btn_img_streetClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                street_image = null;
                street_imageFile = null;
                street_img = "";
                img_street.setImageResource(R.drawable.image_placeholder);
                btn_img_streetClose.setVisibility(View.GONE);
                img_street.setEnabled(true);
            }
        });

        btn_img_buildingClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultipartBody.Part street_image;

                building_image = null;
                building_imageFile = null;
                building_img = "";
                img_building.setImageResource(R.drawable.image_placeholder);
                btn_img_buildingClose.setVisibility(View.GONE);
                img_building.setEnabled(true);
            }
        });

        btn_img_entranceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrance_image = null;
                entrance_imageFile = null;
                entrance_img = "";
                img_entrance.setImageResource(R.drawable.image_placeholder);
                btn_img_entranceClose.setVisibility(View.GONE);
                img_entrance.setEnabled(true);
            }
        });

        //btn_save.setEnabled(false);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        /*btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edt_uniqueLink.getText().toString().equals("")) {
                    edt_uniqueLink.setText(edt_uniqueLink.getText().toString().trim().replace(" ", "-").toLowerCase());
                }
                if (edt_uniqueLink.getText().toString().equals("")) {
                    new Message().showSnack(layout_main, "Please enter unique link name.");
                } else if (!edt_uniqueLink.getText().toString().contains("http") && !pattern.matcher(edt_uniqueLink.getText().toString()).matches()) {
                    open_uniqueLinkTipAlert();
                } else {

                    if (street_imageFile == null) {
                        open_SaveErrorAlert("street image");
                    } else if (building_imageFile == null) {
                        open_SaveErrorAlert("building image");
                    } else if (entrance_imageFile == null) {
                        open_SaveErrorAlert("floor image");
                    } else if (edt_country.getText().toString().equals("") ||
                            edt_city.getText().toString().equals("") ||
                            edt_search.getText().toString().equals("") ||
                            edt_buildingName.getText().toString().equals("") ||
                            edt_entranceName.getText().toString().equals("")) {
                        open_SaveErrorAlert("all");
                    } else if (edt_directionTxt.getText().toString().equals("")) {
                        open_SaveErrorAlert("direction");
                    } else {
                        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), sharedPreferences.getString(Constants.userId, ""));
                        RequestBody address_tag = RequestBody.create(MediaType.parse("multipart/form-data"), public_private_tag);
                        RequestBody latitude_ = null;
                        RequestBody longitude_ = null;
                        if (SEARCHED_LATLONG != null) {
                            latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.latitude));
                            longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.longitude));
                        } else {
                            latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.latitude));
                            longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.longitude));
                        }
                        RequestBody plus_code = RequestBody.create(MediaType.parse("multipart/form-data"), CodeView.mCodeTV.getText().toString());
                        RequestBody unique_link = RequestBody.create(MediaType.parse("multipart/form-data"), edt_uniqueLink.getText().toString().trim());
                        RequestBody country = RequestBody.create(MediaType.parse("multipart/form-data"), edt_country.getText().toString().trim());
                        RequestBody city = RequestBody.create(MediaType.parse("multipart/form-data"), edt_city.getText().toString().trim());
                        RequestBody street_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_streetName.getText().toString().trim());
                        RequestBody building_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_buildingName.getText().toString().trim());
                        RequestBody entrance_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_entranceName.getText().toString().trim());
                        RequestBody street_img_type_ = null;
                        if (street_img_type != null && !street_img_type.equals("")) {
                            street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), street_img_type);
                        } else {
                            street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Street");
                        }
                        RequestBody building_img_type_ = null;
                        if (building_img_type != null && !building_img_type.equals("")) {
                            building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), building_img_type);
                        } else {
                            building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Building");
                        }
                        RequestBody entrance_img_type_ = null;
                        if (entrance_img_type != null && !entrance_img_type.equals("")) {
                            entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_img_type);
                        } else {
                            entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Floor");
                        }
                        RequestBody direction_text = RequestBody.create(MediaType.parse("multipart/form-data"), edt_directionTxt.getText().toString().trim());

                        MultipartBody.Part street_image;
                        if (street_imageFile != null) {
                            RequestBody requestFile_street_image = RequestBody.create(MediaType.parse("multipart/form-data"), street_imageFile);
                            street_image = MultipartBody.Part.createFormData("street_image", street_imageFile.getName(), requestFile_street_image);
                        } else {
                            street_image = null;
                        }
                        MultipartBody.Part building_image;
                        if (building_imageFile != null) {
                            RequestBody requestFile_building_image = RequestBody.create(MediaType.parse("multipart/form-data"), building_imageFile);
                            building_image = MultipartBody.Part.createFormData("building_image", building_imageFile.getName(), requestFile_building_image);
                        } else {
                            building_image = null;
                        }

                        MultipartBody.Part entrance_image = null;
                        if (entrance_imageFile != null) {
                            RequestBody requestFile_entrance_image = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_imageFile);
                            entrance_image = MultipartBody.Part.createFormData("entrance_image", entrance_imageFile.getName(), requestFile_entrance_image);
                        } else {
                            entrance_image = null;
                        }

                        if (public_private_tag.equals("private")) {
                            Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).savePrivateAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_img_type_, building_img_type_, entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e("share Private::", "Location response::" + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");

                                        if (result_code.equals("1")) {
                                            new Message().showSnackGreen(layout_main, "Address shared successfully.");

                                        } else if (result_code.equals("0")) {
                                            new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).savePublicAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_img_type_, building_img_type_, entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e("share Public ", "Location response::" + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");

                                        if (result_code.equals("1")) {
                                            new Message().showSnackGreen(layout_main, "Address shared successfully.");

                                        } else if (result_code.equals("0")) {
                                            new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }

                }

            }
        });*/

        edt_uniqueLink.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    if (!edt_uniqueLink.getText().toString().equals("")) {
                        edt_uniqueLink.setText(edt_uniqueLink.getText().toString().trim().replace(" ", "-"));
                    }
                }
            }
        });
        edt_uniqueLink.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_country.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_country.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_city.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_streetName.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_streetName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_buildingName.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_buildingName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_entranceName.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
        edt_entranceName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    edt_directionTxt.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(AddLocation_Activity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    view_space.setVisibility(View.VISIBLE);
                } else {
                    view_space.setVisibility(View.GONE);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                MyMapView.mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                            Log.e("camera", "The user gestured on the map.");
                            isCameraIdealActive = true;
                        }
                    }
                });

                MyMapView.mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (isCameraIdealActive) {
                            Log.e("CAMERA LOCATION:", "" + MyMapView.mMap.getCameraPosition().target);
                            getAddressNameByLocation(MyMapView.mMap.getCameraPosition().target, "target");
                            isCameraIdealActive = false;
                        }
                    }
                });
            }
        }, 1500);
    }

    /*
     * Create a get url to fetch results from google place autocomplete api.
     * Append the input received from autocomplete edittext
     * Append your current location
     * Append radius you want to search results within
     * Choose a language you want to fetch data in
     * Append your google API Browser key
     */
    public String getPlaceAutoCompleteUrl_(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + "AIzaSyBVXQw3qGgpUCqkxIqtcks2VZafV3Xz39g");

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        Log.d("PLACES RESULT:::", response);
        btn_search.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        //hideKeyboard(AddLocation_Activity.this);
        predictions = gson.fromJson(response, PlacePredictions.class);

        if (mAutoCompleteAdapter == null) {
            mAutoCompleteAdapter = new AutoCompleteAdapter(this, predictions.getPlaces(), AddLocation_Activity.this);
            mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
        } else {
            mAutoCompleteAdapter.clear();
            mAutoCompleteAdapter.addAll(predictions.getPlaces());
            mAutoCompleteAdapter.notifyDataSetChanged();
            mAutoCompleteList.invalidate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // CURRENT_LATLONG = new LatLng(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    public void getAddressNameByLocation(LatLng latLng, String from) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            Log.e("CURRENT_LATLONG1:::", "CURRENT_LATLONG1" + address);
            Log.e("from:::", "from" + from);

            if (UtilityClass.isNetworkConnected(AddLocation_Activity.this)) {

                if (from.equals("current")) {
                    getClientAddressFromAddress_current(address, latLng);
                } else {
                    getClientAddressFromAddress2(address, latLng);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getClientAddressFromAddress(String address) {
        progressBar.setVisibility(View.VISIBLE);
        if (isFromEdit == false) {
            edt_streetName.setText("");
            edt_buildingName.setText("");
            edt_entranceName.setText("");
            edt_city.setText("");
            edt_country.setText("");
        }
        btnCurrentLocation.setEnabled(true);

        Parser.callApi(AddLocation_Activity.this, "Please wait...", false, ApiClient.getClientAddressFromPlaceId().create(ApiInterface.class).getAddressFromAddress(address, "AIzaSyBVXQw3qGgpUCqkxIqtcks2VZafV3Xz39g"), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Address", "From placeId response::" + response);
                progressBar.setVisibility(View.INVISIBLE);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String addre = null;
                Address addr1 = null;
                try {

                    if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                        Log.d("status", jsonObject.getString("status"));
                        addr1 = new Address(Locale.getDefault());
                        JSONObject object = null;

                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int j = 0; j < results.length(); j++) {
                            object = results.getJSONObject(j);

                        }

                        JSONArray address_components = object.getJSONArray("address_components");

                        for (int j = 0; j < address_components.length(); j++) {

                            Log.e("TYPES", "" + (JSONArray) ((JSONObject) address_components.get(j)).get("types"));
                            //country
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String countr = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (countr.compareTo("country") == 0) {
                                    String countr1 = ((JSONObject) address_components.get(j)).getString("long_name");

                                    addr1.setCountryName(countr1);
                                }
                            }

                            //city/town
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String sublocality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (sublocality.compareTo("political") == 0 || sublocality.compareTo("sublocality") == 0 || sublocality.compareTo("sublocality_level_1") == 0) {
                                    String sublocality1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    Log.e("SUBLOCALITY", "" + addr1.getSubLocality());

                                    addr1.setSubLocality(sublocality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String locality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (locality.compareTo("locality") == 0) {
                                    String locality1 = ((JSONObject) address_components.get(0)).getString("long_name");
                                    Log.e("getLocality", "" + addr1.getLocality());

                                    addr1.setLocality(locality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subadminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);

                                if (subadminArea.compareTo("administrative_area_level_2") == 0) {
                                    String subadminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubAdminArea(subadminArea1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String adminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (adminArea.compareTo("administrative_area_level_1") == 0) {
                                    String adminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setAdminArea(adminArea1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String postalcode = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (postalcode.compareTo("postal_code") == 0) {
                                    String postalcode1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPostalCode(postalcode1);
                                }
                            }
                            //street
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String neighborhood = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (neighborhood.compareTo("neighborhood") == 0) {
                                    String neighborhood1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(neighborhood1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String street_number = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (street_number.compareTo("street_number") == 0) {
                                    String street_number_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(street_number_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String route = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (route.compareTo("route") == 0) {
                                    String route_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setThoroughfare(route_);
                                }
                            }

                            //building
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subpremise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (subpremise.compareTo("subpremise") == 0) {
                                    String subpremise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(subpremise_);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String premise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (premise.compareTo("premise") == 0) {
                                    String premise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(premise_);
                                }
                            }

                            //floor/entrance
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String room = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (room.compareTo("room") == 0) {
                                    String room_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(room_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String floor = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (floor.compareTo("floor") == 0) {
                                    String floor_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(floor_);
                                }
                            }
                        }

                        JSONObject geometry = object.optJSONObject("geometry");
                        JSONObject location = geometry.optJSONObject("location");
                        String lat = location.optString("lat");
                        String lng = location.optString("lng");
                        //String formatted_address = jsonObject.getJSONObject("result").optString("formatted_address");
                        Log.e("isCameraIdealActive:::", "isCameraIdealActive" + isCameraIdealActive);

                        if (!lat.equals("") && !lng.equals("")) {
                            SEARCHED_LATLONG = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            btn_save.setEnabled(true);
                            if (isCameraIdealActive == false) {
                                Log.e("camera move", "start" + isCameraIdealActive);

                                MyMapView.mMap.moveCamera(CameraUpdateFactory.zoomTo(19.0f));
                                MyMapView.mMap.moveCamera(CameraUpdateFactory.newLatLng(SEARCHED_LATLONG));

                            }
                        }

                        if (isFromEdit == false) {
                            //city/town
                            if (addr1.getLocality() != null && !addr1.getLocality().equals("")) {
                                edt_city.setText(addr1.getSubLocality());

                            }
                            if (addr1.getSubAdminArea() != null && !addr1.getSubAdminArea().equals("")) {
                                if (!edt_city.getText().toString().equals("")) {
                                    edt_city.setText(edt_city.getText().toString() + ", " + addr1.getSubAdminArea());
                                } else {
                                    edt_city.setText(addr1.getSubAdminArea());
                                }


                            }
                            if (addr1.getAdminArea() != null && !addr1.getAdminArea().equals("")) {
                                if (!edt_city.getText().toString().equals("")) {
                                    edt_city.setText(edt_city.getText().toString() + ", " + addr1.getAdminArea());
                                } else {
                                    edt_city.setText(addr1.getAdminArea());
                                }
                            }
                            //country
                            edt_country.setText(edt_city.getText().toString() + ", " + addr1.getCountryName());

                            //street
                            if (addr1.getSubThoroughfare() != null && !addr1.getSubThoroughfare().equals("")) {
                                edt_streetName.setText(addr1.getSubThoroughfare());
                            }
                            if (addr1.getThoroughfare() != null && !addr1.getThoroughfare().equals("")) {
                                if (addr1.getSubThoroughfare() != null) {
                                    edt_streetName.setText(edt_streetName.getText().toString() + ", " + addr1.getThoroughfare());
                                } else {
                                    edt_streetName.setText(addr1.getThoroughfare());
                                }

                            }

                            //building
                            if (addr1.getPremises() != null && !addr1.getPremises().equals("")) {
                                edt_buildingName.setText(addr1.getPremises());
                            }

                            //floor/entrance
                            if (addr1.getFeatureName() != null && !addr1.getFeatureName().equals("")) {
                                edt_entranceName.setText(addr1.getFeatureName());
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You may check your internet connection or latitude and longitude values", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d("JSONException", e.toString());
                }
            }
        });
    }

    public void getClientAddressFromAddress2(String address, LatLng LATLNG) {
        progressBar.setVisibility(View.VISIBLE);
        if (isFromEdit == false) {
            edt_streetName.setText("");
            edt_buildingName.setText("");
            edt_entranceName.setText("");
            edt_city.setText("");
            edt_country.setText("");
        }
        btnCurrentLocation.setEnabled(true);

        Parser.callApi(AddLocation_Activity.this, "Please wait...", false, ApiClient.getClientAddressFromPlaceId().create(ApiInterface.class).getAddressFromAddress(address, "AIzaSyBVXQw3qGgpUCqkxIqtcks2VZafV3Xz39g"), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Address", "From placeId response::" + response);
                progressBar.setVisibility(View.INVISIBLE);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String addre = null;
                Address addr1 = null;
                try {

                    if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                        Log.d("status", jsonObject.getString("status"));
                        addr1 = new Address(Locale.getDefault());
                        JSONObject object = null;

                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int j = 0; j < results.length(); j++) {
                            object = results.getJSONObject(j);

                        }

                        JSONArray address_components = object.getJSONArray("address_components");

                        for (int j = 0; j < address_components.length(); j++) {

                            Log.e("TYPES", "" + (JSONArray) ((JSONObject) address_components.get(j)).get("types"));
                            //country
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String countr = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (countr.compareTo("country") == 0) {
                                    String countr1 = ((JSONObject) address_components.get(j)).getString("long_name");

                                    addr1.setCountryName(countr1);
                                }
                            }

                            //city/town
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String sublocality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (sublocality.compareTo("political") == 0 || sublocality.compareTo("sublocality") == 0 || sublocality.compareTo("sublocality_level_1") == 0) {
                                    String sublocality1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    Log.e("SUBLOCALITY", "" + addr1.getSubLocality());

                                    addr1.setSubLocality(sublocality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String locality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (locality.compareTo("locality") == 0) {
                                    String locality1 = ((JSONObject) address_components.get(0)).getString("long_name");
                                    Log.e("getLocality", "" + addr1.getLocality());

                                    addr1.setLocality(locality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subadminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);

                                if (subadminArea.compareTo("administrative_area_level_2") == 0) {
                                    String subadminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubAdminArea(subadminArea1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String adminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (adminArea.compareTo("administrative_area_level_1") == 0) {
                                    String adminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setAdminArea(adminArea1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String postalcode = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (postalcode.compareTo("postal_code") == 0) {
                                    String postalcode1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPostalCode(postalcode1);
                                }
                            }
                            //street
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String neighborhood = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (neighborhood.compareTo("neighborhood") == 0) {
                                    String neighborhood1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(neighborhood1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String street_number = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (street_number.compareTo("street_number") == 0) {
                                    String street_number_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(street_number_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String route = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (route.compareTo("route") == 0) {
                                    String route_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setThoroughfare(route_);
                                }
                            }

                            //building
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subpremise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (subpremise.compareTo("subpremise") == 0) {
                                    String subpremise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(subpremise_);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String premise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (premise.compareTo("premise") == 0) {
                                    String premise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(premise_);
                                }
                            }

                            //floor/entrance
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String room = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (room.compareTo("room") == 0) {
                                    String room_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(room_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String floor = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (floor.compareTo("floor") == 0) {
                                    String floor_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(floor_);
                                }
                            }
                        }

                        JSONObject geometry = object.optJSONObject("geometry");
                        JSONObject location = geometry.optJSONObject("location");
                        String lat = location.optString("lat");
                        String lng = location.optString("lng");
                        //Log.e("isCameraIdealActive:::current", "isCameraIdealActive" + isCameraIdealActive);

                        if (!lat.equals("") && !lng.equals("")) {
                            SEARCHED_LATLONG = LATLNG;
                            btn_save.setEnabled(true);
                            if (isCameraIdealActive == false) {
                                Log.e("camera move", "before" + isCameraIdealActive + "\n" + SEARCHED_LATLONG);

                                MyMapView.mMap.moveCamera(CameraUpdateFactory.zoomTo(19.0f));
                                MyMapView.mMap.moveCamera(CameraUpdateFactory.newLatLng(SEARCHED_LATLONG));
                                Log.e("camera move", "after" + isCameraIdealActive);

                            }
                        }


                        //city/town
                        if (addr1.getLocality() != null && !addr1.getLocality().equals("")) {
                            edt_city.setText(addr1.getSubLocality());

                        }
                        if (addr1.getSubAdminArea() != null && !addr1.getSubAdminArea().equals("")) {
                            if (!edt_city.getText().toString().equals("")) {
                                edt_city.setText(edt_city.getText().toString() + ", " + addr1.getSubAdminArea());
                            } else {
                                edt_city.setText(addr1.getSubAdminArea());
                            }

                        }
                        if (addr1.getAdminArea() != null && !addr1.getAdminArea().equals("")) {
                            if (!edt_city.getText().toString().equals("")) {
                                edt_city.setText(edt_city.getText().toString() + ", " + addr1.getAdminArea());
                            } else {
                                edt_city.setText(addr1.getAdminArea());
                            }
                        }

                        //country
                        edt_country.setText(edt_city.getText().toString() + ", " + addr1.getCountryName());

                        //street
                        if (addr1.getSubThoroughfare() != null && !addr1.getSubThoroughfare().equals("")) {
                            edt_streetName.setText(addr1.getSubThoroughfare());
                        }
                        if (addr1.getThoroughfare() != null && !addr1.getThoroughfare().equals("")) {
                            if (addr1.getSubThoroughfare() != null) {
                                edt_streetName.setText(edt_streetName.getText().toString() + ", " + addr1.getThoroughfare());
                            } else {
                                edt_streetName.setText(addr1.getThoroughfare());
                            }
                        }

                        //building
                        if (addr1.getPremises() != null && !addr1.getPremises().equals("")) {
                            edt_buildingName.setText(addr1.getPremises());
                        }

                        //floor/entrance
                        if (addr1.getFeatureName() != null && !addr1.getFeatureName().equals("")) {
                            edt_entranceName.setText(addr1.getFeatureName());
                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You may check your internet connection or latitude and longitude values", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d("JSONException", e.toString());
                }
            }
        });
    }

    public void getClientAddressFromAddress_current(String address, LatLng LATLNG) {
        progressBar.setVisibility(View.VISIBLE);
        if (isFromEdit == false) {
            edt_streetName.setText("");
            edt_buildingName.setText("");
            edt_entranceName.setText("");
            edt_city.setText("");
            edt_country.setText("");
        }
        btnCurrentLocation.setEnabled(true);

        Parser.callApi(AddLocation_Activity.this, "Please wait...", false, ApiClient.getClientAddressFromPlaceId().create(ApiInterface.class).getAddressFromAddress(address, "AIzaSyBVXQw3qGgpUCqkxIqtcks2VZafV3Xz39g"), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Address", "From placeId response::" + response);
                progressBar.setVisibility(View.INVISIBLE);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String addre = null;
                Address addr1 = null;
                try {

                    if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                        Log.d("status", jsonObject.getString("status"));
                        addr1 = new Address(Locale.getDefault());
                        JSONObject object = null;

                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int j = 0; j < results.length(); j++) {
                            object = results.getJSONObject(j);

                        }

                        JSONArray address_components = object.getJSONArray("address_components");

                        for (int j = 0; j < address_components.length(); j++) {

                            Log.e("TYPES", "" + (JSONArray) ((JSONObject) address_components.get(j)).get("types"));
                            //country
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String countr = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (countr.compareTo("country") == 0) {
                                    String countr1 = ((JSONObject) address_components.get(j)).getString("long_name");

                                    addr1.setCountryName(countr1);
                                }
                            }

                            //city/town
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String sublocality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (sublocality.compareTo("political") == 0 || sublocality.compareTo("sublocality") == 0 || sublocality.compareTo("sublocality_level_1") == 0) {
                                    String sublocality1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    Log.e("SUBLOCALITY", "" + addr1.getSubLocality());

                                    addr1.setSubLocality(sublocality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String locality = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (locality.compareTo("locality") == 0) {
                                    String locality1 = ((JSONObject) address_components.get(0)).getString("long_name");
                                    Log.e("getLocality", "" + addr1.getLocality());

                                    addr1.setLocality(locality1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subadminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);

                                if (subadminArea.compareTo("administrative_area_level_2") == 0) {
                                    String subadminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubAdminArea(subadminArea1);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String adminArea = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (adminArea.compareTo("administrative_area_level_1") == 0) {
                                    String adminArea1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setAdminArea(adminArea1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String postalcode = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (postalcode.compareTo("postal_code") == 0) {
                                    String postalcode1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPostalCode(postalcode1);
                                }
                            }
                            //street
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String neighborhood = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (neighborhood.compareTo("neighborhood") == 0) {
                                    String neighborhood1 = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(neighborhood1);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String street_number = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (street_number.compareTo("street_number") == 0) {
                                    String street_number_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setSubThoroughfare(street_number_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String route = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (route.compareTo("route") == 0) {
                                    String route_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setThoroughfare(route_);
                                }
                            }

                            //building
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String subpremise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (subpremise.compareTo("subpremise") == 0) {
                                    String subpremise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(subpremise_);
                                }
                            }

                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String premise = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (premise.compareTo("premise") == 0) {
                                    String premise_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setPremises(premise_);
                                }
                            }

                            //floor/entrance
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String room = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (room.compareTo("room") == 0) {
                                    String room_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(room_);
                                }
                            }
                            if (((JSONArray) ((JSONObject) address_components.get(j)).get("types")).length() > 0) {
                                String floor = ((JSONArray) ((JSONObject) address_components.get(j)).get("types")).getString(0);
                                if (floor.compareTo("floor") == 0) {
                                    String floor_ = ((JSONObject) address_components.get(j)).getString("long_name");
                                    addr1.setFeatureName(floor_);
                                }
                            }
                        }

                        JSONObject geometry = object.optJSONObject("geometry");
                        JSONObject location = geometry.optJSONObject("location");
                        String lat = location.optString("lat");
                        String lng = location.optString("lng");

                        if (!lat.equals("") && !lng.equals("")) {
                            latitude = Double.parseDouble(lat);
                            longitude = Double.parseDouble(lng);
                            //SEARCHED_LATLONG = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                            SEARCHED_LATLONG = LATLNG;
                            Log.e("SEARCHED_LATLONG_CURR", "" + SEARCHED_LATLONG);

                            btn_save.setEnabled(true);
                            if (isCameraIdealActive == false) {

                                MyMapView.mMap.moveCamera(CameraUpdateFactory.zoomTo(19.0f));
                                MyMapView.mMap.moveCamera(CameraUpdateFactory.newLatLng(SEARCHED_LATLONG));
                                isCameraIdealActive = false;
                            }
                        }


                        //city/town
                        if (addr1.getLocality() != null && !addr1.getLocality().equals("")) {
                            edt_city.setText(addr1.getSubLocality());

                        }
                        if (addr1.getSubAdminArea() != null && !addr1.getSubAdminArea().equals("")) {
                            if (!edt_city.getText().toString().equals("")) {
                                edt_city.setText(edt_city.getText().toString() + ", " + addr1.getSubAdminArea());
                            } else {
                                edt_city.setText(addr1.getSubAdminArea());
                            }

                        }
                        if (addr1.getAdminArea() != null && !addr1.getAdminArea().equals("")) {
                            if (!edt_city.getText().toString().equals("")) {
                                edt_city.setText(edt_city.getText().toString() + ", " + addr1.getAdminArea());
                            } else {
                                edt_city.setText(addr1.getAdminArea());
                            }
                        }

                        //country
                        edt_country.setText(edt_city.getText().toString() + ", " + addr1.getCountryName());

                        //street
                        if (addr1.getSubThoroughfare() != null && !addr1.getSubThoroughfare().equals("")) {
                            edt_streetName.setText(addr1.getSubThoroughfare());
                        }
                        if (addr1.getThoroughfare() != null && !addr1.getThoroughfare().equals("")) {
                            if (addr1.getSubThoroughfare() != null) {
                                edt_streetName.setText(edt_streetName.getText().toString() + ", " + addr1.getThoroughfare());
                            } else {
                                edt_streetName.setText(addr1.getThoroughfare());
                            }
                        }

                        //building
                        if (addr1.getPremises() != null && !addr1.getPremises().equals("")) {
                            edt_buildingName.setText(addr1.getPremises());
                        }

                        //floor/entrance
                        if (addr1.getFeatureName() != null && !addr1.getFeatureName().equals("")) {
                            edt_entranceName.setText(addr1.getFeatureName());
                        }


                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You may check your internet connection or latitude and longitude values", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),
                            e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d("JSONException", e.toString());
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

    private void saveData() {

        btn_save.setImageResource(R.drawable.save_icon_filled);
        if (!from.equals("") && !from.equals("edit")) {
            if (!edt_uniqueLink.getText().toString().equals("")) {
                edt_uniqueLink.setText(edt_uniqueLink.getText().toString().trim().replace(" ", "-").toLowerCase());
            }
            if (edt_uniqueLink.getText().toString().equals("")) {
                new Message().showSnack(layout_main, "Please enter unique link name.");
            } else if (!edt_uniqueLink.getText().toString().contains("http") && !pattern.matcher(edt_uniqueLink.getText().toString()).matches()) {
                open_uniqueLinkTipAlert();

            } else {

                if (street_imageFile == null) {
                    open_SaveErrorAlert("street image");
                } else if (building_imageFile == null) {
                    open_SaveErrorAlert("building image");
                } else if (entrance_imageFile == null) {
                    open_SaveErrorAlert("floor image");
                } else if (edt_country.getText().toString().equals("") ||
                        edt_streetName.getText().toString().equals("") ||
                        edt_buildingName.getText().toString().equals("") ||
                        edt_entranceName.getText().toString().equals("")) {
                    open_SaveErrorAlert("all");
                } else if (edt_directionTxt.getText().toString().equals("")) {
                    open_SaveErrorAlert("direction");
                } else {
                    RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), sharedPreferences.getString(Constants.userId, ""));
                    RequestBody address_tag = RequestBody.create(MediaType.parse("multipart/form-data"), public_private_tag);
                    RequestBody latitude_ = null;
                    RequestBody longitude_ = null;
                    if (SEARCHED_LATLONG != null) {
                        latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.latitude));
                        longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.longitude));
                    } else {
                        latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.latitude));
                        longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.longitude));
                    }
                    RequestBody plus_code = RequestBody.create(MediaType.parse("multipart/form-data"), CodeView.mCodeTV.getText().toString());
                    RequestBody unique_link = RequestBody.create(MediaType.parse("multipart/form-data"), edt_uniqueLink.getText().toString().trim());
                    RequestBody country = RequestBody.create(MediaType.parse("multipart/form-data"), edt_country.getText().toString().trim());
                    RequestBody city = RequestBody.create(MediaType.parse("multipart/form-data"), edt_city.getText().toString().trim());
                    RequestBody street_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_streetName.getText().toString().trim());
                    RequestBody building_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_buildingName.getText().toString().trim());
                    RequestBody entrance_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_entranceName.getText().toString().trim());
                    RequestBody street_img_type_ = null;
                    if (street_img_type != null && !street_img_type.equals("")) {
                        street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), street_img_type);
                    } else {
                        street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Street");
                    }
                    RequestBody building_img_type_ = null;
                    if (building_img_type != null && !building_img_type.equals("")) {
                        building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), building_img_type);
                    } else {
                        building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Building");
                    }
                    RequestBody entrance_img_type_ = null;
                    if (entrance_img_type != null && !entrance_img_type.equals("")) {
                        entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_img_type);
                    } else {
                        entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Floor");
                    }
                    RequestBody direction_text = RequestBody.create(MediaType.parse("multipart/form-data"), edt_directionTxt.getText().toString().trim());

                    MultipartBody.Part street_image;
                    if (street_imageFile != null) {
                        RequestBody requestFile_street_image = RequestBody.create(MediaType.parse("multipart/form-data"), street_imageFile);
                        street_image = MultipartBody.Part.createFormData("street_image", street_imageFile.getName(), requestFile_street_image);
                    } else {
                        street_image = null;
                    }
                    MultipartBody.Part building_image;
                    if (building_imageFile != null) {
                        RequestBody requestFile_building_image = RequestBody.create(MediaType.parse("multipart/form-data"), building_imageFile);
                        building_image = MultipartBody.Part.createFormData("building_image", building_imageFile.getName(), requestFile_building_image);
                    } else {
                        building_image = null;
                    }

                    MultipartBody.Part entrance_image = null;
                    if (entrance_imageFile != null) {
                        RequestBody requestFile_entrance_image = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_imageFile);
                        entrance_image = MultipartBody.Part.createFormData("entrance_image", entrance_imageFile.getName(), requestFile_entrance_image);
                    } else {
                        entrance_image = null;
                    }

                    Log.e("NETWORK", "" + UtilityClass.isNetworkConnected(AddLocation_Activity.this));
                    if (UtilityClass.isNetworkConnected(AddLocation_Activity.this)) {
                        if (public_private_tag.equals("private")) {
                            Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).savePrivateAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_img_type_, building_img_type_, entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e("Add Private::", "Location response::" + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");

                                        if (result_code.equals("1")) {
                                            startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                            finish();
                                        } else if (result_code.equals("0")) {
                                            new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).savePublicAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_img_type_, building_img_type_, entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e("Add Public ", "Location response::" + response);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");

                                        if (result_code.equals("1")) {
                                            startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                            finish();
                                        } else if (result_code.equals("0")) {
                                            new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        JSONObject main_jsonObject = new JSONObject();
                        JSONArray resultData = new JSONArray();
                        JSONObject object = new JSONObject();

                        try {
                            object.put("userId", sharedPreferences.getString(Constants.userId, ""));
                            object.put("address_tag", public_private_tag);
                            object.put("latitude", "0");
                            object.put("longitude", "0");
                            object.put("plus_code", CodeView.mCodeTV.getText().toString());
                            object.put("unique_link", edt_uniqueLink.getText().toString().trim());
                            object.put("country", edt_country.getText().toString().trim());
                            object.put("city", edt_city.getText().toString().trim());
                            object.put("street_name", edt_streetName.getText().toString().trim());
                            object.put("building_name", edt_buildingName.getText().toString().trim());
                            object.put("entrance_name", edt_entranceName.getText().toString().trim());
                            object.put("direction_text", edt_directionTxt.getText().toString().trim());
                            object.put("street_image", street_imageFile.toString());
                            object.put("building_image", building_imageFile.toString());
                            object.put("entrance_image", entrance_imageFile.toString());
                            object.put("street_img_type", "Street");
                            object.put("building_img_type", "Building");
                            object.put("entrance_img_type", "Entrance");

                            resultData.put(object);
                            main_jsonObject.put("resultData", resultData);

                            Log.e("OFFLINE DATA SAVE", "" + main_jsonObject);
                            dbHandler.addAddress(sharedPreferences.getString(Constants.userId, ""), main_jsonObject.toString());
                            Log.e("OFFLINE DATA SAVE", "" + dbHandler.getAddress(sharedPreferences.getString(Constants.userId, "")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        } else {
            Log.e("EDIT:", "" + from + "/" + building_img + "\n" + building_imageFile);
            if (!edt_uniqueLink.getText().toString().equals("")) {
                edt_uniqueLink.setText(edt_uniqueLink.getText().toString().replace(" ", "-").toLowerCase());
            }
            if (edt_uniqueLink.getText().toString().equals("")) {
                new Message().showSnack(layout_main, "Please enter unique link name.");
            } else if (!edt_uniqueLink.getText().toString().contains("http") && !pattern.matcher(edt_uniqueLink.getText().toString()).matches()) {
                open_uniqueLinkTipAlert();

            } else {

                if (street_img.contains("address_default_image") && street_imageFile == null) {
                    open_SaveErrorAlert("street image");
                } else if (building_img.contains("address_default_image") && building_imageFile == null) {
                    open_SaveErrorAlert("building image");
                } else if (entrance_img.contains("address_default_image") && entrance_imageFile == null) {
                    open_SaveErrorAlert("floor image");
                } else if (edt_country.getText().toString().equals("") ||
                        edt_streetName.getText().toString().equals("") ||
                        edt_buildingName.getText().toString().equals("") ||
                        edt_entranceName.getText().toString().equals("")) {
                    open_SaveErrorAlert("all");
                } else if (edt_directionTxt.getText().toString().equals("")) {
                    open_SaveErrorAlert("direction");
                } else {
                    RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), sharedPreferences.getString(Constants.userId, ""));
                    RequestBody addressId = RequestBody.create(MediaType.parse("multipart/form-data"), address_id);
                    RequestBody address_tag = RequestBody.create(MediaType.parse("multipart/form-data"), public_private_tag);
                    RequestBody latitude_ = null;
                    RequestBody longitude_ = null;
                    if (SEARCHED_LATLONG != null) {
                        latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.latitude));
                        longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.longitude));
                    } else {
                        latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.latitude));
                        longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.longitude));
                    }
                    RequestBody plus_code = RequestBody.create(MediaType.parse("multipart/form-data"), CodeView.mCodeTV.getText().toString());
                    RequestBody unique_link = RequestBody.create(MediaType.parse("multipart/form-data"), edt_uniqueLink.getText().toString().trim());
                    RequestBody country = RequestBody.create(MediaType.parse("multipart/form-data"), edt_country.getText().toString().trim());
                    RequestBody city = RequestBody.create(MediaType.parse("multipart/form-data"), edt_city.getText().toString().trim());
                    RequestBody street_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_streetName.getText().toString().trim());
                    RequestBody building_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_buildingName.getText().toString().trim());
                    RequestBody entrance_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_entranceName.getText().toString().trim());
                    RequestBody street_img_type_ = null;
                    RequestBody street_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                    RequestBody building_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                    RequestBody enterance_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                    if (street_img_type != null && !street_img_type.equals("")) {
                        street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), street_img_type);
                    } else {
                        street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Street");
                    }
                    RequestBody building_img_type_ = null;
                    if (building_img_type != null && !building_img_type.equals("")) {
                        building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), building_img_type);
                    } else {
                        building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Building");
                    }
                    RequestBody entrance_img_type_ = null;
                    if (entrance_img_type != null && !entrance_img_type.equals("")) {
                        entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_img_type);
                    } else {
                        entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Floor");
                    }
                    RequestBody direction_text = RequestBody.create(MediaType.parse("multipart/form-data"), edt_directionTxt.getText().toString().trim());

                    MultipartBody.Part street_image;
                    if (street_imageFile != null) {
                        RequestBody requestFile_street_image = RequestBody.create(MediaType.parse("multipart/form-data"), street_imageFile);
                        street_image = MultipartBody.Part.createFormData("street_image", street_imageFile.getName(), requestFile_street_image);
                    } else {
                        street_image = null;
                    }
                    MultipartBody.Part building_image;
                    if (building_imageFile != null) {
                        RequestBody requestFile_building_image = RequestBody.create(MediaType.parse("multipart/form-data"), building_imageFile);
                        building_image = MultipartBody.Part.createFormData("building_image", building_imageFile.getName(), requestFile_building_image);
                    } else {
                        building_image = null;
                    }

                    MultipartBody.Part entrance_image = null;
                    if (entrance_imageFile != null) {
                        RequestBody requestFile_entrance_image = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_imageFile);
                        entrance_image = MultipartBody.Part.createFormData("entrance_image", entrance_imageFile.getName(), requestFile_entrance_image);
                    } else {
                        entrance_image = null;
                    }

                    if (public_private_tag.equals("private")) {
                        Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).
                                editPublicPrivateAddressWithImage(user_id, addressId, address_tag, address_tag, latitude_, longitude_,
                                        plus_code, unique_link, country, city, street_name, building_name, entrance_name, street_img_remove
                                        , building_img_remove, enterance_img_remove,
                                        direction_text, street_img_type_, building_img_type_,
                                        entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                            @Override
                            public void getResponseFromServer(String response) {
                                Log.e("EDIT Private::", "Location response::" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String result_code = jsonObject.getString("resultCode");

                                    if (result_code.equals("1")) {
                                        startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                        finish();
                                    } else if (result_code.equals("0")) {
                                        new Message().showSnack(layout_main, "" + jsonObject.optString("data"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {

                        Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class)
                                .editPublicPrivateAddressWithImage(user_id, addressId, address_tag, address_tag, latitude_, longitude_,
                                        plus_code, unique_link, country, city, street_name, building_name, entrance_name, street_img_remove
                                        , building_img_remove, enterance_img_remove,
                                        direction_text, street_img_type_, building_img_type_,
                                        entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                            @Override
                            public void getResponseFromServer(String response) {
                                Log.e("EDIT Public ", "Location response::" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String result_code = jsonObject.getString("resultCode");

                                    if (result_code.equals("1")) {
                                        startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                        finish();
                                    } else if (result_code.equals("0")) {
                                        new Message().showSnack(layout_main, "" + jsonObject.optString("data"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void open_directionTipAlert() {
        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tool_tip_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        Button btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);

        title.setText("Text Direction");
        title.setVisibility(View.VISIBLE);
        sub_title.setText(R.string.direction_message);
        btn_add.setVisibility(View.GONE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void open_SaveErrorAlert(String type) {
        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.save_address_error, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView txt_subTitle = view.findViewById(R.id.txt_subTitle);
        Button btn_update = view.findViewById(R.id.btn_update);
        Button btn_skip = view.findViewById(R.id.btn_skip);

        if (type.equals("all")) {
            txt_subTitle.setText("You did not complete the address details. This may impact how easily people can locate this address.");
        } else if (type.equals("direction")) {
            txt_subTitle.setText("You did not complete the address details. This may impact how easily people can locate this address.");
        }
        if (type.equals("street image")) {
            //title.setText("Add a photo of the road");
            txt_subTitle.setText("You did not upload a picture of street. This may impact how easily people can locate this address.");
        }
        if (type.equals("building image")) {
            txt_subTitle.setText("You did not upload a picture of building. This may impact how easily people can locate this address.");
        }
        if (type.equals("floor image")) {
            txt_subTitle.setText("You did not upload a picture of entrance. This may impact how easily people can locate this address.");
        }

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_save.setImageResource(R.drawable.save_icon);
                dialog.dismiss();
            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressToServer(dialog);
            }
        });

    }

    private void addAddressToServer(Dialog dialog) {
        if (!edt_uniqueLink.getText().toString().equals("")) {
            edt_uniqueLink.setText(edt_uniqueLink.getText().toString().replace(" ", "-").toLowerCase());
        }
        if (from != null && !from.equals("edit")) {
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), sharedPreferences.getString(Constants.userId, ""));
            RequestBody address_tag = RequestBody.create(MediaType.parse("multipart/form-data"), public_private_tag);
            RequestBody latitude_ = null;
            RequestBody longitude_ = null;
            if (SEARCHED_LATLONG != null) {
                latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.latitude));
                longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.longitude));
            } else {
                latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.latitude));
                longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.longitude));
            }
            RequestBody plus_code = RequestBody.create(MediaType.parse("multipart/form-data"), CodeView.mCodeTV.getText().toString());
            RequestBody unique_link = RequestBody.create(MediaType.parse("multipart/form-data"), edt_uniqueLink.getText().toString().trim());
            RequestBody country = RequestBody.create(MediaType.parse("multipart/form-data"), edt_country.getText().toString().trim());
            RequestBody city = RequestBody.create(MediaType.parse("multipart/form-data"), edt_city.getText().toString().trim());
            RequestBody street_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_streetName.getText().toString().trim());
            RequestBody building_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_buildingName.getText().toString().trim());
            RequestBody entrance_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_entranceName.getText().toString().trim());
            RequestBody street_img_type_ = null;
            if (street_img_type != null && !street_img_type.equals("")) {
                street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), street_img_type);
            } else {
                street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Street");
            }
            RequestBody building_img_type_ = null;
            if (building_img_type != null && !building_img_type.equals("")) {
                building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), building_img_type);
            } else {
                building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Building");
            }
            RequestBody entrance_img_type_ = null;
            if (entrance_img_type != null && !entrance_img_type.equals("")) {
                entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_img_type);
            } else {
                entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Floor");
            }
            RequestBody direction_text = RequestBody.create(MediaType.parse("multipart/form-data"), edt_directionTxt.getText().toString().trim());


            if (street_imageFile != null) {
                RequestBody requestFile_street_image = RequestBody.create(MediaType.parse("multipart/form-data"), street_imageFile);
                street_image = MultipartBody.Part.createFormData("street_image", street_imageFile.getName(), requestFile_street_image);
            } else {
                street_image = null;
            }

            if (building_imageFile != null) {
                RequestBody requestFile_building_image = RequestBody.create(MediaType.parse("multipart/form-data"), building_imageFile);
                building_image = MultipartBody.Part.createFormData("building_image", building_imageFile.getName(), requestFile_building_image);
            } else {
                building_image = null;
            }

            if (entrance_imageFile != null) {
                RequestBody requestFile_entrance_image = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_imageFile);
                entrance_image = MultipartBody.Part.createFormData("entrance_image", entrance_imageFile.getName(), requestFile_entrance_image);
            } else {
                entrance_image = null;
            }

            if (UtilityClass.isNetworkConnected(AddLocation_Activity.this)) {
                if (public_private_tag.equals("private")) {
                    Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).savePrivateAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link, country, city, street_name, building_name, entrance_name, direction_text, street_img_type_, building_img_type_, entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Add Private::", "Location response::" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result_code = jsonObject.getString("resultCode");

                                if (result_code.equals("1")) {
                                    startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                    finish();
                                } else if (result_code.equals("0")) {
                                    new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class)
                            .savePublicAddress(user_id, address_tag, latitude_, longitude_, plus_code, unique_link,
                                    country, city, street_name, building_name, entrance_name, direction_text,
                                    street_img_type_, building_img_type_, entrance_img_type_,
                                    street_image, building_image, entrance_image), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Add Public ", "Location response::" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result_code = jsonObject.getString("resultCode");

                                if (result_code.equals("1")) {
                                    startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                    finish();
                                } else if (result_code.equals("0")) {
                                    new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                JSONObject main_jsonObject = new JSONObject();
                JSONArray resultData = new JSONArray();
                JSONObject object = new JSONObject();

                try {
                    object.put("userId", sharedPreferences.getString(Constants.userId, ""));
                    object.put("address_tag", public_private_tag);
                    object.put("latitude", "0");
                    object.put("longitude", "0");
                    object.put("plus_code", CodeView.mCodeTV.getText().toString());
                    object.put("unique_link", edt_uniqueLink.getText().toString().trim());
                    object.put("country", edt_country.getText().toString().trim());
                    object.put("city", edt_city.getText().toString().trim());
                    object.put("street_name", edt_streetName.getText().toString().trim());
                    object.put("building_name", edt_buildingName.getText().toString().trim());
                    object.put("entrance_name", edt_entranceName.getText().toString().trim());
                    object.put("direction_text", edt_directionTxt.getText().toString().trim());

                    if (street_imageFile != null) {
                        object.put("street_image", street_imageFile.toString());
                    } else {
                        object.put("street_image", "");
                    }
                    if (building_imageFile != null) {
                        object.put("building_image", building_imageFile.toString());
                    } else {
                        object.put("building_image", "");
                    }
                    if (entrance_imageFile != null) {
                        object.put("entrance_image", entrance_imageFile.toString());
                    } else {
                        object.put("entrance_image", "");
                    }
                    object.put("street_img_type", "Street");
                    object.put("building_img_type", "Building");
                    object.put("entrance_img_type", "Entrance");

                    resultData.put(object);
                    main_jsonObject.put("resultData", resultData);

                    Log.e("OFFLINE DATA SAVE", "" + main_jsonObject);
                    dbHandler.addAddress(sharedPreferences.getString(Constants.userId, ""), main_jsonObject.toString());
                    Log.e("OFFLINE DATA SAVE", "" + dbHandler.getAddress(sharedPreferences.getString(Constants.userId, "")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } else {

            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), sharedPreferences.getString(Constants.userId, ""));
            RequestBody addressId = RequestBody.create(MediaType.parse("multipart/form-data"), address_id);
            RequestBody address_tag = RequestBody.create(MediaType.parse("multipart/form-data"), public_private_tag);
            RequestBody latitude_ = null;
            RequestBody longitude_ = null;
            if (SEARCHED_LATLONG != null) {
                latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.latitude));
                longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SEARCHED_LATLONG.longitude));
            } else {
                latitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.latitude));
                longitude_ = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(CURRENT_LATLONG.longitude));
            }
            RequestBody plus_code = RequestBody.create(MediaType.parse("multipart/form-data"), CodeView.mCodeTV.getText().toString());
            RequestBody unique_link = RequestBody.create(MediaType.parse("multipart/form-data"), edt_uniqueLink.getText().toString().trim());
            RequestBody country = RequestBody.create(MediaType.parse("multipart/form-data"), edt_country.getText().toString().trim());
            RequestBody city = RequestBody.create(MediaType.parse("multipart/form-data"), edt_city.getText().toString().trim());
            RequestBody street_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_streetName.getText().toString().trim());
            RequestBody building_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_buildingName.getText().toString().trim());
            RequestBody entrance_name = RequestBody.create(MediaType.parse("multipart/form-data"), edt_entranceName.getText().toString().trim());
            RequestBody street_img_type_ = null;
            RequestBody street_img_remove = null;
            RequestBody building_img_remove = null;
            RequestBody enterance_img_remove = null;

            if (street_img_type != null && !street_img_type.equals("")) {
                street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), street_img_type);
            } else {
                street_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Street");
            }
            RequestBody building_img_type_ = null;
            if (building_img_type != null && !building_img_type.equals("")) {
                building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), building_img_type);
            } else {
                building_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Building");
            }
            RequestBody entrance_img_type_ = null;
            if (entrance_img_type != null && !entrance_img_type.equals("")) {
                entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_img_type);
            } else {
                entrance_img_type_ = RequestBody.create(MediaType.parse("multipart/form-data"), "Floor");
            }
            RequestBody direction_text = RequestBody.create(MediaType.parse("multipart/form-data"), edt_directionTxt.getText().toString().trim());

            MultipartBody.Part street_image;
            if (street_imageFile != null) {
                RequestBody requestFile_street_image = RequestBody.create(MediaType.parse("multipart/form-data"), street_imageFile);
                street_image = MultipartBody.Part.createFormData("street_image", street_imageFile.getName(), requestFile_street_image);
            } else {
                street_image = null;
                if (!street_img.equals("")) {
                    street_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                } else {
                    street_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "remove");
                }
            }

            MultipartBody.Part building_image;
            if (building_imageFile != null) {
                RequestBody requestFile_building_image = RequestBody.create(MediaType.parse("multipart/form-data"), building_imageFile);
                building_image = MultipartBody.Part.createFormData("building_image", building_imageFile.getName(), requestFile_building_image);
            } else {
                building_image = null;
                if (!building_img.equals("")) {
                    building_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                } else {
                    building_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "remove");
                }
            }

            MultipartBody.Part entrance_image = null;
            if (entrance_imageFile != null) {
                RequestBody requestFile_entrance_image = RequestBody.create(MediaType.parse("multipart/form-data"), entrance_imageFile);
                entrance_image = MultipartBody.Part.createFormData("entrance_image", entrance_imageFile.getName(), requestFile_entrance_image);
            } else {
                entrance_image = null;
                if (!entrance_img.equals("")) {
                    enterance_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                } else {
                    enterance_img_remove = RequestBody.create(MediaType.parse("multipart/form-data"), "remove");
                }
            }

            if (public_private_tag.equals("private")) {
                Parser.callApi(AddLocation_Activity.this, "Please wait...",
                        true, ApiClient.getClient().create(ApiInterface.class).
                                editPublicPrivateAddressWithImage(user_id, addressId, address_tag, address_tag, latitude_, longitude_,
                                        plus_code, unique_link, country, city, street_name, building_name, entrance_name, street_img_remove
                                        , building_img_remove, enterance_img_remove,
                                        direction_text, street_img_type_, building_img_type_,
                                        entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                            @Override
                            public void getResponseFromServer(String response) {
                                Log.e("EDIT Private::", "Location response::" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String result_code = jsonObject.getString("resultCode");

                                    if (result_code.equals("1")) {
                                        startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                        finish();
                                    } else if (result_code.equals("0")) {
                                        new Message().showSnack(layout_main, "" + jsonObject.optString("data"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } else {
                Log.e("khushbu", "street_image::" + street_image);
                Log.e("khushbu", "building_image::" + building_image);
                Log.e("khushbu", "entrance_image::" + entrance_image);

                Log.e("khushbu", "street_img_remove::" + street_img_remove);
                Log.e("khushbu", "building_img_remove::" + building_img_remove);
                Log.e("khushbu", "enterance_img_remove::" + enterance_img_remove);
                Parser.callApi(AddLocation_Activity.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).editPublicPrivateAddressWithImage(user_id, addressId, address_tag, address_tag, latitude_, longitude_,
                        plus_code, unique_link, country, city, street_name, building_name, entrance_name,
                        street_img_remove
                        , building_img_remove,
                        enterance_img_remove,
                        direction_text, street_img_type_, building_img_type_,
                        entrance_img_type_, street_image, building_image, entrance_image), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("EDIT Public ", "Location response::" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result_code = jsonObject.getString("resultCode");

                            if (result_code.equals("1")) {
                                startActivity(new Intent(AddLocation_Activity.this, Home_Activity_new.class));
                                finish();
                            } else if (result_code.equals("0")) {
                                new Message().showSnack(layout_main, "" + jsonObject.optString("data"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        }

        dialog.dismiss();
    }

    private void open_imageTipAlert(final String type) {
        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.image_selection_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        ImageButton btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_camera = view.findViewById(R.id.btn_camera);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);

        if (type.equals("street")) {
            title.setText("Add a photo of the road");
            sub_title.setText("Help people save time with a photo the road near your address.");

        } else if (type.equals("building")) {
            title.setText("Add a photo of the building or establishment");
            sub_title.setText("Pictures of your building can reduce the ETA of a delivery by 20%.");

        } else {
            title.setText("Add a photo of the entrance");
            sub_title.setText("Help people navigate inside your address by adding a picture of your gate or building entrance.");

        }

        title.setVisibility(View.VISIBLE);
        btn_add.setVisibility(View.VISIBLE);
        btn_camera.setVisibility(View.VISIBLE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCamera = false;
                if (hasPermissions(PERMISSIONS)) {
                    if (type.equals("street")) {
                        street_img_type = "Street";
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_STREET_IMAGE);
                        dialog.dismiss();
                    } else if (type.equals("building")) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_BUILDING_IMAGE);
                        building_img_type = "Building";
                        dialog.dismiss();
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_ENTRANCE_IMAGE);
                        entrance_img_type = "Entrance";
                        dialog.dismiss();
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                    }
                }
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCamera = true;
                if (hasPermissions(PERMISSIONS)) {
                    if (type.equals("street")) {
                        street_img_type = "Street";
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, RESULT_LOAD_STREET_IMAGE);//zero can be replaced with any action code
                        dialog.dismiss();
                    } else if (type.equals("building")) {
                        building_img_type = "Building";
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, RESULT_LOAD_BUILDING_IMAGE);//zero can be replaced with any action code
                        dialog.dismiss();
                    } else {
                        entrance_img_type = "Entrance";
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, RESULT_LOAD_ENTRANCE_IMAGE);//zero can be replaced with any action code
                        dialog.dismiss();
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
                    }
                }
            }
        });

        dialog.show();
    }

    private void open_uniqueLinkTipAlert() {
        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tool_tip_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        Button btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);

        title.setText("Unique address name");
        title.setVisibility(View.VISIBLE);
        sub_title.setText(R.string.unique_name_message);
        btn_add.setVisibility(View.GONE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void open_PlusCodeTipAlert() {
        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tool_tip_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        Button btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);
        LinearLayout img_plusDiagram = view.findViewById(R.id.img_plusDiagram);

        title.setText("Plus Code");
        img_plusDiagram.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        sub_title.setText("Your Plus Code is a unique location code that you can use as your address. It’s easy to remember and can be used on Google Maps for directions to your address.");
        btn_add.setVisibility(View.GONE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void bottomSheetSetup() {
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        // set the peek height
        // bottomSheetBehavior.setPeekHeight(230);
        // set hide able or not
        bottomSheetBehavior.setHideable(true);
        // set callback for changes

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    btn_dropDown.setImageResource(R.drawable.card_arrow_down);
                    btn_dropDown.setTag("up");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void open_publicPrivateTipAlert() {

        final Dialog dialog = new Dialog(AddLocation_Activity.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tool_tip_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        Button btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);

        title.setText("Public / Private Addresses");
        title.setVisibility(View.VISIBLE);
        sub_title.setText(R.string.public_private_message);
        btn_add.setVisibility(View.GONE);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                Toast.makeText(AddLocation_Activity.this, "Some Permission denied", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_STREET_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri filePath = data.getData();
            Bitmap bitmap = null;
            try {
                if (isCamera) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                }
                img_street.setImageBitmap(bitmap);
                btn_img_streetClose.setVisibility(View.VISIBLE);
                img_street.setEnabled(false);
                BitmapImageToFile(bitmap, "street");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == RESULT_LOAD_BUILDING_IMAGE && resultCode == RESULT_OK && null != data) {
            Bitmap bitmap = null;
            Uri filePath = data.getData();
            try {
                if (isCamera) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                }
                img_building.setImageBitmap(bitmap);
                BitmapImageToFile(bitmap, "building");
                btn_img_buildingClose.setVisibility(View.VISIBLE);
                img_building.setEnabled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == RESULT_LOAD_ENTRANCE_IMAGE && resultCode == RESULT_OK && null != data) {
            Bitmap bitmap = null;
            Uri filePath = data.getData();
            try {
                if (isCamera) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                }
                img_entrance.setImageBitmap(bitmap);
                BitmapImageToFile(bitmap, "entrance");
                btn_img_entranceClose.setVisibility(View.VISIBLE);
                img_entrance.setEnabled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void BitmapImageToFile(Bitmap bitmap, String type) {
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            new File(path + "/" + getString(R.string.app_name)).mkdirs();
            if (type.equals("street")) {
                street_imageFile = new File(path + "/" + getString(R.string.app_name) + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(street_imageFile);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), street_imageFile.getAbsolutePath(), street_imageFile.getName(), street_imageFile.getName());
            } else if (type.equals("building")) {
                building_imageFile = new File(path + "/" + getString(R.string.app_name) + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(building_imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), building_imageFile.getAbsolutePath(), building_imageFile.getName(), building_imageFile.getName());
            } else {
                entrance_imageFile = new File(path + "/" + getString(R.string.app_name) + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(entrance_imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver(), entrance_imageFile.getAbsolutePath(), entrance_imageFile.getName(), entrance_imageFile.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
