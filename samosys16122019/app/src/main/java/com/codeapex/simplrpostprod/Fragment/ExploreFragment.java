package com.codeapex.simplrpostprod.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.PrivateLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Activity.PublicLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Activity.QRScanActivity;
import com.codeapex.simplrpostprod.Activity.SearchBusinessActivity;
import com.codeapex.simplrpostprod.Adapter.AdaterPrimaryCategories;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.RecentAdded;
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

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class ExploreFragment extends Fragment {

    RecyclerView recyclerView, recyclerVieww, trendingBusiiness;
    ImageView scan;
    TextView tView, serachhere;

    String strrefrenceNum;
    ConstraintLayout root_lay;
    private RelativeLayout hiddenPanel, cardlayput, newww;
    // View myView;
    boolean isUp;
    SeekBar seekBar;
    RelativeLayout show;
    LinearLayout category;
    CardView cardView;
    ImageView imageView;

    List<RecentAdded> list;
    List<RecentAdded> bussineslist;
    String user_Id;
    String text;
    int isPublic = 1;
    ProgressBar Loader;
    private int CAMREA_CODE = 1;

    TextView txtvFilterCategoryName, txtvCancelButton, txtvApplyButton, trendingcategories, business, recentadded;
    private int MY_PERMISSIONS_REQUESTS = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.explore, container, false);

        //
        //imageView = view.findViewById(R.id.fillter);
        cardView = view.findViewById(R.id.cardView);
        scan = view.findViewById(R.id.scan);

        root_lay = view.findViewById(R.id.root_lay);
        serachhere = view.findViewById(R.id.serachhere);


        Loader = view.findViewById(R.id.Loader);

        show = view.findViewById(R.id.show);
        isUp = false;


        SharedPreferences preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        Log.d(TAG, "onCreateView: " + user_Id);


        list = new ArrayList<>();
        bussineslist = new ArrayList<>();


        getPrimaryCategories();


        cardlayput = view.findViewById(R.id.cardlayout);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBusinessActivity.class);
                intent.putExtra("from","explore");
                startActivity(intent);


            }
        });


//

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("CAMERA is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUESTS);
                                Intent intent = new Intent(getActivity(), QRScanActivity.class);
                                startActivityForResult(intent, 1);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUESTS);
                    }

                } else {
                    Intent intent = new Intent(getActivity(), QRScanActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });


        recyclerView = view.findViewById(R.id.categoryRecycler);
        //validateQR();

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                validateQR(data.getStringExtra("code"));
            }
        }
    }



    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
//





    public void getPrimaryCategories() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        Log.d(TAG, "getPrivateAddresses: " + user_Id);
        UtilityClass.showLoading(Loader,getActivity());

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPrimaryCategories(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, "nbewwww: " + response);
                UtilityClass.hideLoading(Loader,getActivity());

                try {

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

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                        new Message().showSnack(root_lay, "This account has been blocked.");


                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                        new Message().showSnack(root_lay, "All fields not sent.");

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                        new Message().showSnack(root_lay, " Please check the request method.");
                    } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                        JSONArray jsonArray = new JSONArray(result_data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                            String categoryId = jsonObject1.getString("categoryId");
                            String categoryName = jsonObject1.getString("categoryName");

                            String iconURL = jsonObject1.getString("iconURL");

                            RecentAdded resultPrivateAddress = new RecentAdded(categoryId, categoryName, iconURL);
                            list.add(resultPrivateAddress);

                            @SuppressLint("WrongConstant") GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3,
                                    LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(gridLayoutManager);
                            recyclerView.setHasFixedSize(true);

                        }
                        AdaterPrimaryCategories custom_adaterFriends = new AdaterPrimaryCategories(getActivity(), list);
                        recyclerView.setAdapter(custom_adaterFriends);
                      //  recentadded.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                UtilityClass.hideLoading(Loader,getActivity());

            }
        });


//    }
    }


    public void validateQR(String refNo) {


        try {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, user_Id);
            hashMap.put(Constants.referenceNumber, refNo);
            Log.d(TAG, "validateQR: " + user_Id + refNo);

            UtilityClass.showLoading(Loader,getActivity());


            Call<Object> call = RetrofitInstance.getdata().create(Api.class).validateQR(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,getActivity());
                    if (response.isSuccessful()) {
                        try {

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

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");


                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "All fields not sent.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                JSONObject jsonObject1 = new JSONObject(result_data);
                                String addressId = jsonObject1.getString("addressId");
                                String userId = jsonObject1.getString("userId");
                                Log.d(TAG, "addressId: " + addressId);
                                isPublic = jsonObject1.getInt("isPublic");
                                Log.d(TAG, "isPublic: " + isPublic);
                                if (isPublic == 1) {
                                    Intent intent = new Intent(getActivity(), PublicLocationDetailNewActivity.class);
                                    intent.putExtra("addressId", addressId);
                                    if (user_Id.equals(userId)) {
                                        intent.putExtra("isOwn",true);
                                    }
                                    else {
                                        intent.putExtra("isOwn",false);
                                    }
                                    intent.putExtra("isFromShared",true);
                                    startActivity(intent);
                                } else {

                                    Intent intent = new Intent(getActivity(), PrivateLocationDetailNewActivity.class);
                                    intent.putExtra("addressId", addressId);
                                    if (user_Id.equals(userId)) {
                                        intent.putExtra("isOwn",true);
                                    }
                                    else {
                                        intent.putExtra("isOwn",false);
                                    }
                                    intent.putExtra("isFromShared",true);
                                    startActivity(intent);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
                    UtilityClass.hideLoading(Loader,getActivity());
                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission denied to camera", Toast.LENGTH_SHORT).show();
                    boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.CAMERA );
                    if (! showRationale) {
                        openSettingsDialog();
                }

                }
                return;
            }
        }
    }

}
