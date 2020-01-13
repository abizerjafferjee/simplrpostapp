package com.codeapex.simplrpostprod.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.AdatperSavedAddresses;
import com.codeapex.simplrpostprod.Adapter.AddressImageGridAdapter;
import com.codeapex.simplrpostprod.Adapter.ShareAdapter;
import com.codeapex.simplrpostprod.Adapter.ShareExternalAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.addressGridImage;
import com.codeapex.simplrpostprod.ModelClass.savedItems;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.PlaceLikelihoodBufferResponse;
import com.google.android.libraries.places.compat.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.model.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Activity_ViewPublicAddress extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout bottom_sheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private CoordinatorLayout layout_main;
    private ImageView btn_dropDown, btn_back, img_qrCode, img_street, img_building, img_entrance;
    private CircleImageView img_profile;
    private TextView txt_titleHeader, btn_save, tag, txt_userName, txt_plusCode_info, txt_plusCode, txt_uniqueLink, txt_address, txt_directionText, txt_addressInfo, txt_street_img_type, txt_building_img_type, txt_entrance_img_type;
    private ImageButton btn_infoAddLink, btn_infoPlusCode, btn_deleteAddress, btn_copyUniqueLink, btn_copyPlusCode, btn_direction, btn_share, btn_currentLocation, btn_editAddress;
    private Button btn_manage;
    private String user_id, address_id, profile_img, user_name, plus_code, public_private_tag, qr_code_img, street_img, building_img, entrance_img, address_unique_link, country, city, street, building, entrance, direction_txt, street_img_type, building_img_type, entrance_img_type;
    private double latitude, longitude;
    private SharedPreferences sharedPreferences;
    private String is_owner = "false";
    private DisplayMetrics metrics;
    private ArrayList<String> savedIds = new ArrayList<>();
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private LinearLayout card_street, card_building, card_entrance;

    GoogleMap mMap;
    LatLng currentLocation_latlng;
    MarkerOptions markerOpt;
    PlaceDetectionClient mPlaceDetectionClient;
    SupportMapFragment mapView;
    protected LocationManager locationManager;
    String provider;
    String from = "";
    LinearLayout linear_direction, linear_address, linear_imageMain;
    ArrayList<addressGridImage> gridImages = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_address);

        sharedPreferences = this.getSharedPreferences("Sesssion", MODE_PRIVATE);

        findViews();
        getIntentFromActivity();
        bottomSheetSetup();
        getLocation();
        getSharedWithUser();
    }

    @Override
    public void onBackPressed() {
        if (from != null && from.equals("home")) {
            startActivity(new Intent(Activity_ViewPublicAddress.this, Home_Activity_new.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 2000, this);
    }

    private void getIntentFromActivity() {
        user_id = this.getIntent().getStringExtra("user_id");
        from = this.getIntent().getStringExtra("from");
        address_id = this.getIntent().getStringExtra("address_id");
        profile_img = this.getIntent().getStringExtra("profile_img");
        user_name = this.getIntent().getStringExtra("user_name");
        plus_code = this.getIntent().getStringExtra("plus_code");
        public_private_tag = this.getIntent().getStringExtra("public_private_tag");
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
        direction_txt = this.getIntent().getStringExtra("direction_txt");
        street_img_type = this.getIntent().getStringExtra("street_img_type");
        building_img_type = this.getIntent().getStringExtra("building_img_type");
        entrance_img_type = this.getIntent().getStringExtra("entrance_img_type");

        if (this.getIntent().getStringExtra("latitude") != null && !this.getIntent().getStringExtra("latitude").equals("") && this.getIntent().getStringExtra("longitude") != null && !this.getIntent().getStringExtra("longitude").equals("")) {
            latitude = Double.parseDouble(this.getIntent().getStringExtra("latitude"));
            longitude = Double.parseDouble(this.getIntent().getStringExtra("longitude"));
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }


        if (!profile_img.equals("null") && !profile_img.equals("")) {
            Picasso.with(Activity_ViewPublicAddress.this).load(Constants.IMG_URL + profile_img)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.profileplchlder)
                    .into(img_profile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            img_profile.setImageResource(R.drawable.profileplchlder);

                        }
                    });

        }

        if (qr_code_img != null && !qr_code_img.isEmpty()) {
            Picasso.with(Activity_ViewPublicAddress.this).load(Constants.IMG_URL + qr_code_img.replace("uploads/", "")).into(img_qrCode);
        }

        if (!user_name.equals("null")&& user_name != null && !user_name.equals("")) {
            txt_userName.setText(user_name);
        } else {
            txt_userName.setText("");
        }
        txt_plusCode.setText(plus_code);

        if (!street_img.equals("null") && !street_img.contains("address_default_image.png")) {
            //linear_imageMain.setVisibility(View.GONE);
            gridImages.add(new addressGridImage(street_img, "Street"));
        }

        if (!building_img.equals("null") && !building_img.contains("address_default_image.png")) {
            //linear_imageMain.setVisibility(View.GONE);
            gridImages.add(new addressGridImage(building_img, "Building"));
        }

        if (!entrance_img.equals("null") && !entrance_img.contains("address_default_image.png")) {
            //linear_imageMain.setVisibility(View.GONE);
            gridImages.add(new addressGridImage(entrance_img, "Entrance"));
        }

        if (gridImages.isEmpty()) {
            linear_imageMain.setVisibility(View.GONE);
        }

        AddressImageGridAdapter addressImageGridAdapter = new AddressImageGridAdapter(Activity_ViewPublicAddress.this, gridImages);
        GridLayoutManager linearLayoutManager_external = new GridLayoutManager(Activity_ViewPublicAddress.this, 3);
        recyclerView.setLayoutManager(linearLayoutManager_external);
        recyclerView.setAdapter(addressImageGridAdapter);

        txt_uniqueLink.setText(address_unique_link);
        if (!entrance.equals("")) {
            txt_address.setText(entrance);
        }

        if (!building.equals("")) {
            if (!entrance.equals("")) {
                txt_address.setText(txt_address.getText().toString() + ", " + building);
            } else {
                txt_address.setText(building);

            }
        }

        if (!street.equals("")) {
            if (!building.equals("")) {
                txt_address.setText(txt_address.getText().toString() + ", " + street);
            } else {
                txt_address.setText(street);

            }
        }

        if (!country.equals("")) {
            if (!street.equals("")) {
                txt_address.setText(txt_address.getText().toString() + ", " + country);
            } else {
                txt_address.setText(country);

            }
        }

        if (direction_txt == null || direction_txt.equals("")) {
            linear_direction.setVisibility(View.GONE);
        }

        txt_directionText.setText(direction_txt);
        txt_street_img_type.setText(street_img_type);
        txt_building_img_type.setText(building_img_type);
        txt_entrance_img_type.setText(entrance_img_type);

        if (public_private_tag != null && !public_private_tag.equals("")) {

            if (public_private_tag.equals("public")) {
                tag.setText("Public");
                //txt_titleHeader.setText("Public");

            } else {
                tag.setText("Private");
                //txt_titleHeader.setText("Private");
                btn_save.setVisibility(View.INVISIBLE);
                if (sharedPreferences.getString(Constants.userId, "").equals(user_id)) {
                    btn_share.setVisibility(View.VISIBLE);
                } else {
                    btn_share.setVisibility(View.INVISIBLE);
                }
            }

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("PUB",""+public_private_tag);
                    btn_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_bookmark_fill_24dp, 0);
                    Parser.callApi(Activity_ViewPublicAddress.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).saveOtherUserAddress(sharedPreferences.getString(Constants.userId,""), address_id,public_private_tag), new Response_Call_Back() {
                        @Override
                        public void getResponseFromServer(String response) {
                            Log.e("Save other address::", "response::" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result_code = jsonObject.getString("resultCode");
                                if (result_code.equals("1")) {
                                    new Message().showSnackGreen(layout_main, "Address saved successfully.");
                                } else if (result_code.equals("0")) {
                                    new Message().showSnack(layout_main, "" + jsonObject.optString("resultData"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            if (sharedPreferences.getString(Constants.userId, "").equals(user_id)) {
                btn_save.setVisibility(View.GONE);
                btn_editAddress.setVisibility(View.VISIBLE);
                btn_deleteAddress.setVisibility(View.VISIBLE);
                is_owner = "true";

                if (public_private_tag.equals("public")) {
                    btn_manage.setText("Share my address");
                    btn_manage.setTag("share");
                    btn_manage.setVisibility(View.GONE);
                } else {
                    btn_manage.setText("Manage access");
                    btn_manage.setTag("manage");
                    btn_manage.setVisibility(View.VISIBLE);
                }

            } else {
                btn_editAddress.setVisibility(View.GONE);
                btn_deleteAddress.setVisibility(View.GONE);

                if (public_private_tag.equals("public")) {
                    btn_manage.setText("Share my address");
                    btn_manage.setTag("share");
                    btn_manage.setVisibility(View.VISIBLE);
                } else {
                    btn_manage.setVisibility(View.GONE);
                    btn_manage.setText("Manage access");
                    btn_manage.setTag("manage");
                }
            }
        }

    }

    public void getSharedWithUser() {

        if (UtilityClass.isNetworkConnected(Activity_ViewPublicAddress.this)) {

            Parser.callApi(Activity_ViewPublicAddress.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).getSavedAddress(sharedPreferences.getString(Constants.userId,""),"public"), new Response_Call_Back() {
                @Override
                public void getResponseFromServer(String response) {
                    Log.e("shared with user", "response :" + response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result_code = jsonObject.getString("resultCode");
                        if (result_code.equals("1")) {
                            JSONArray resultData = jsonObject.optJSONArray("resultData");
                            for (int i = 0; i < resultData.length(); i++) {
                                JSONObject object = resultData.optJSONObject(i);

                                String addressId = object.optString("addressId");
                                if (addressId.equals(address_id)){
                                    btn_save.setEnabled(false);
                                    btn_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_bookmark_fill_24dp, 0);
                                    break;
                                }else {
                                    btn_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_bookmark_border_black_24dp, 0);
                                }

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    private void findViews() {
        bottom_sheet = findViewById(R.id.bottom_sheet);
        layout_main = findViewById(R.id.layout_main);
        btn_dropDown = findViewById(R.id.btn_dropDown);
        btn_back = findViewById(R.id.btn_back);
        txt_titleHeader = findViewById(R.id.txt_titleHeader);
        btn_save = findViewById(R.id.btn_save);
        tag = findViewById(R.id.tag);
        btn_direction = findViewById(R.id.btn_direction);
        btn_share = findViewById(R.id.btn_share);
        btn_currentLocation = findViewById(R.id.btn_currentLocation);
        btn_editAddress = findViewById(R.id.btn_editAddress);
        img_profile = findViewById(R.id.img_profile);
        txt_userName = findViewById(R.id.txt_userName);
        txt_plusCode_info = findViewById(R.id.txt_plusCode_info);
        txt_plusCode = findViewById(R.id.txt_plusCode);
        img_qrCode = findViewById(R.id.img_qrCode);
        img_street = findViewById(R.id.img_street);
        img_building = findViewById(R.id.img_building);
        img_entrance = findViewById(R.id.img_entrance);
        txt_uniqueLink = findViewById(R.id.txt_uniqueLink);
        txt_address = findViewById(R.id.txt_address);
        txt_directionText = findViewById(R.id.txt_directionText);
        txt_street_img_type = findViewById(R.id.street_img_type);
        txt_building_img_type = findViewById(R.id.building_img_type);
        txt_entrance_img_type = findViewById(R.id.entrance_img_type);
        btn_manage = findViewById(R.id.btn_manage);
        txt_addressInfo = findViewById(R.id.txt_addressInfo);
        btn_copyPlusCode = findViewById(R.id.btn_copyPlusCode);
        btn_copyUniqueLink = findViewById(R.id.btn_copyUniqueLink);
        btn_deleteAddress = findViewById(R.id.btn_deleteAddress);
        linear_direction = findViewById(R.id.linear_direction);
        linear_address = findViewById(R.id.linear_address);
        card_street = findViewById(R.id.card_street);
        card_building = findViewById(R.id.card_building);
        card_entrance = findViewById(R.id.card_entrance);
        linear_imageMain = findViewById(R.id.linear_imageMain);
        recyclerView = findViewById(R.id.recyclerView);
        btn_infoAddLink = findViewById(R.id.btn_infoAddLink);
        btn_infoPlusCode = findViewById(R.id.btn_infoPlusCode);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        float div = convertPxToDp(this, width) / 3;

        ViewGroup.LayoutParams layoutParams = card_street.getLayoutParams();
        layoutParams.width = (int) (div * 1.6);
        card_street.setLayoutParams(layoutParams);
        card_building.setLayoutParams(layoutParams);
        card_entrance.setLayoutParams(layoutParams);


        btn_deleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ViewPublicAddress.this);
                builder.setMessage("Are you sure, you want to delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d(TAG, "public_private_tag " + public_private_tag);

                        if (public_private_tag.equals("private")) {

                            Log.d(TAG, "delete Private param: " + sharedPreferences.getString(Constants.userId, "") + address_id);

                            Parser.callApi(Activity_ViewPublicAddress.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).deletePrivateAddress(sharedPreferences.getString(Constants.userId, ""), address_id), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e(TAG, "private delete onResponse: " + response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");
                                        if (result_code.equals("1")) {
                                            AlertDialog.Builder builder
                                                    = new AlertDialog
                                                    .Builder(Activity_ViewPublicAddress.this);
                                            builder.setMessage("Address deleted successfully.");

                                            builder.setCancelable(false);
                                            builder.setPositiveButton(
                                                    "Ok",
                                                    new DialogInterface
                                                            .OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            finish();
                                                        }
                                                    });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                        } else if (result_code.equals("0")) {
                                            //new Message().showSnack(root_lay, "" + jsonObject.optString("data"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {

                            Log.d(TAG, "public Private param: " + sharedPreferences.getString(Constants.userId, "") + address_id);

                            Parser.callApi(Activity_ViewPublicAddress.this, "Please wait...", false, ApiClient.getClient().create(ApiInterface.class).deletePublicAddress(sharedPreferences.getString(Constants.userId, ""), address_id), new Response_Call_Back() {
                                @Override
                                public void getResponseFromServer(String response) {
                                    Log.e(TAG, "public delete onResponse: " + response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String result_code = jsonObject.getString("resultCode");
                                        if (result_code.equals("1")) {
                                            AlertDialog.Builder builder
                                                    = new AlertDialog
                                                    .Builder(Activity_ViewPublicAddress.this);
                                            builder.setMessage("Address deleted successfully.");

                                            builder.setCancelable(false);
                                            builder.setPositiveButton(
                                                    "Ok",
                                                    new DialogInterface
                                                            .OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            finish();
                                                        }
                                                    });

                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                        } else if (result_code.equals("0")) {
                                            //new Message().showSnack(root_lay, "" + jsonObject.optString("data"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        btn_copyPlusCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txt_plusCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Activity_ViewPublicAddress.this, "Copied.", Toast.LENGTH_SHORT).show();
            }
        });

        btn_copyUniqueLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txt_uniqueLink.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Activity_ViewPublicAddress.this, "Copied.", Toast.LENGTH_SHORT).show();
            }
        });

        /*txt_uniqueLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = txt_uniqueLink.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(urlString)); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent); }

                catch (ActivityNotFoundException ex) {
                     intent.setPackage(null);
                     startActivity(intent);
                }
            }
        });*/

        btn_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag().equals("manage")) {

                    if (public_private_tag.equals("public")) {
                        startActivity(new Intent(Activity_ViewPublicAddress.this, SharedListActivity.class)
                                .putExtra("addressID", address_id)
                                .putExtra("isPublic", "1"));
                    } else {
                        startActivity(new Intent(Activity_ViewPublicAddress.this, SharedListActivity.class)
                                .putExtra("addressID", address_id)
                                .putExtra("isPublic", "0"));
                    }

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            /*if (public_private_tag.equals("public")) {
                                shareAddress(user_id, address_id, public_private_tag, true);
                            }else {
                                shareAddress(user_id, address_id, public_private_tag, false);
                            }*/

                            /*startActivity(new Intent(Activity_ViewPublicAddress.this, AddLocation_Activity.class).putExtra("address_id", "")
                                    .putExtra("user_id", user_id)
                                    .putExtra("profile_img", "")
                                    .putExtra("user_name", "")
                                    .putExtra("plus_code", "")
                                    .putExtra("public_private_tag", "")
                                    .putExtra("qr_code_img", "")
                                    .putExtra("street_img", "")
                                    .putExtra("building_img", "")
                                    .putExtra("entrance_img", "")
                                    .putExtra("address_unique_link", "")
                                    .putExtra("country", "")
                                    .putExtra("city", "")
                                    .putExtra("street", "")
                                    .putExtra("building", "")
                                    .putExtra("entrance", "")
                                    .putExtra("latitude", "")
                                    .putExtra("longitude", "")
                                    .putExtra("direction_txt", "")
                                    .putExtra("from", "share"));*/

                            Intent intent = new Intent(Activity_ViewPublicAddress.this, AddressesActivity.class);
                            intent.putExtra("recieverId", user_id);
                            startActivityForResult(intent, 10);
                            //new Message().showSnackGreen(layout_main, "Address shared successfully.");
                        }
                    }, 1000);
                }
            }
        });

        /*btn_currentLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

            }
        });*/

        btn_editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Location:", "from edit" + "/" + user_id + latitude + ", Longitude:" + longitude);

                startActivity(new Intent(Activity_ViewPublicAddress.this, AddLocation_Activity.class)
                        .putExtra("user_id", user_id)
                        .putExtra("address_id", address_id)
                        .putExtra("profile_img", profile_img)
                        .putExtra("user_name", user_name)
                        .putExtra("plus_code", plus_code)
                        .putExtra("public_private_tag", public_private_tag)
                        .putExtra("qr_code_img", qr_code_img)
                        .putExtra("street_img", street_img)
                        .putExtra("building_img", building_img)
                        .putExtra("entrance_img", entrance_img)
                        .putExtra("address_unique_link", address_unique_link)
                        .putExtra("country", country)
                        .putExtra("city", city)
                        .putExtra("street", street)
                        .putExtra("building", building)
                        .putExtra("latitude", String.valueOf(latitude))
                        .putExtra("longitude", String.valueOf(longitude))
                        .putExtra("entrance", entrance)
                        .putExtra("direction_txt", direction_txt)
                        .putExtra("street_img_type", street_img_type)
                        .putExtra("building_img_type", building_img_type)
                        .putExtra("entrance_img_type", entrance_img_type)
                        .putExtra("from", "edit"));
            }
        });

        img_qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_ViewPublicAddress.this, Activity_QrCode.class)
                        .putExtra("direction_txt", txt_directionText.getText().toString())
                        .putExtra("address_unique_link", txt_uniqueLink.getText().toString())
                        .putExtra("direction_txt", txt_directionText.getText().toString())
                        .putExtra("address", txt_address.getText().toString())
                        .putExtra("plus_code", txt_plusCode.getText().toString())
                        .putExtra("address_id", address_id)
                        .putExtra("is_private", public_private_tag)
                        .putExtra("is_owner", is_owner)
                        .putExtra("qr_code_img", qr_code_img));
            }
        });

        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != 0 && longitude != 0) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps?f=d&daddr=" + latitude + "," + longitude));
                    //Uri.parse("https://www.google.com/maps?f=d&daddr="+ txt_plusCode.getText().toString()));
                    startActivity(intent);

                }
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_owner.equals("true")) {
                    startActivity(new Intent(Activity_ViewPublicAddress.this, Activity_Share.class)
                            .putExtra("is_owner", "true")
                            .putExtra("is_private", tag.getText().toString())
                            .putExtra("shareDirect", false)
                            .putExtra("unique_link", address_unique_link)
                            .putExtra("address_id", address_id));
                } else {
                    startActivity(new Intent(Activity_ViewPublicAddress.this, Activity_Share.class).putExtra("is_owner", "false")
                            .putExtra("is_private", tag.getText().toString())
                            .putExtra("shareDirect", false)
                            .putExtra("is_owner", "false")
                            .putExtra("unique_link", address_unique_link)
                            .putExtra("address_id", address_id));
                }

            }
        });

        btn_infoPlusCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_PlusCodeTipAlert();
            }
        });

        btn_infoAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_uniqueLinkTipAlert();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        //Map scroll
        mMap = ((WorkaroundMapFragment) Activity_ViewPublicAddress.this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getApplicationContext());
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //checkGPSAndPermission();
        mapView.getMapAsync(Activity_ViewPublicAddress.this);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                //scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("Location:", "" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        currentLocation_latlng = new LatLng(location.getLatitude(), location.getLongitude());

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

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

            }
        });

        if (latitude != 0 && longitude != 0) {
            Log.d("Latitude Longitude", "status" + latitude + "/" + longitude);

            //Marker
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            String title = user_name + " is here";

            //Marker
            markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(latitude, longitude))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_static));

            mMap.addMarker(markerOpt);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
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

    private void bottomSheetSetup() {
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        // set the peek height
        //bottomSheetBehavior.setPeekHeight(320);
        // set hideable or not
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

    private void open_PlusCodeTipAlert() {
        final Dialog dialog = new Dialog(Activity_ViewPublicAddress.this, R.style.DialogSlideAnim);
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

    private void open_uniqueLinkTipAlert() {
        final Dialog dialog = new Dialog(Activity_ViewPublicAddress.this, R.style.DialogSlideAnim);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.tool_tip_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(view);

        TextView title = view.findViewById(R.id.txt_title);
        TextView sub_title = view.findViewById(R.id.txt_subTitle);
        Button btn_add = view.findViewById(R.id.btn_add);
        ImageButton btn_clear = view.findViewById(R.id.btn_clear);

        title.setText("Unique link name");
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

}
