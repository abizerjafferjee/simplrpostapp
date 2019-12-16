package com.codeapex.simplrpostprod.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.codeapex.simplrpostprod.Activity.AddAddressOptionActivity;
import com.codeapex.simplrpostprod.Activity.EditProfileActivity;
import com.codeapex.simplrpostprod.Activity.SavedAddressListActivity;
import com.codeapex.simplrpostprod.Activity.SharedWithMeActivity;
import com.codeapex.simplrpostprod.Activity.ImagePreviewActivity;
import com.codeapex.simplrpostprod.Activity.SignInActivity;
import com.codeapex.simplrpostprod.Adapter.AdaptorAddresses;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.SaveUserDetail;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements ProfileScreenInterface, OnRefreshListener {
    RecyclerView recyclerView;
    ProgressBar loader;
    CircleImageView imgUserImage;
    TextView profileName, profileUserName, profileEmail;
    LinearLayout linearLayout;
    LinearLayout linearLayoutt;
    ImageView circleImageView;
    LinearLayout root_lay;
    List<ResultPrivateAddress> models;
    SharedPreferences preferences;
    String userId, name, emailId, contactNumber, dpimg, isEmailIdVerified, isContactNumberVerified;
    LinearLayout linearprivate;
    TextView text, txt_contactNumber, txt_msg_profileComplete;
    String question, answer;
    ProfileScreenInterface intProfile;
    ConstraintLayout consEmptyScreen;
    SwipeRefreshLayout swipeRefreshLayout;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    String imgURL = "";
    ProgressBar progressBarImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);


    }


    @Nullable
    @Override


    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
//        swpSwipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        circleImageView = view.findViewById(R.id.circlee);
        consEmptyScreen = view.findViewById(R.id.consEmptyScreen);
        recyclerView = view.findViewById(R.id.recyecler);
        linearLayout = view.findViewById(R.id.savedlinear);
        loader = view.findViewById(R.id.Loader);
        root_lay = view.findViewById(R.id.root_lay);
        imgUserImage = view.findViewById(R.id.imgUserImage);
        linearLayoutt = view.findViewById(R.id.sharedlinear);
        linearprivate = view.findViewById(R.id.linearprivate);
        profileName = view.findViewById(R.id.profileName);
        profileUserName = view.findViewById(R.id.profileUserName);
        profileEmail = view.findViewById(R.id.profileEmail);
        text = view.findViewById(R.id.text);
        txt_contactNumber = view.findViewById(R.id.txt_contactNumber);
        progressBarImage = view.findViewById(R.id.progressBarImage);

        txt_msg_profileComplete = view.findViewById(R.id.txt_msg_profileComplete);
        // txt_msg_profileComplete.setVisibility(View.GONE);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);

        swipeRefreshLayout.setOnRefreshListener(this);

        preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        userId = preferences.getString(Constants.userId, "");

        models = new ArrayList<>();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("userId", userId);
                intent.putExtra("emailId", emailId);
                intent.putExtra("contactNumber", contactNumber);
                intent.putExtra("isEmailIdVerified", isEmailIdVerified);
                intent.putExtra("isContactNumberVerified", isContactNumberVerified);
                intent.putExtra("dpimg", "");
                intent.putExtra("question", question);
                intent.putExtra("answer", answer);
                startActivityForResult(intent, 1024);
            }
        });
        txt_msg_profileComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bitmap bitmap = ((BitmapDrawable) imgUserImage.getDrawable()).getBitmap();

                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, _bs);*/
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("userId", userId);
                intent.putExtra("emailId", emailId);
                intent.putExtra("contactNumber", contactNumber);
                intent.putExtra("isEmailIdVerified", isEmailIdVerified);
                intent.putExtra("isContactNumberVerified", isContactNumberVerified);
                intent.putExtra("dpimg", "");
                intent.putExtra("question", question);
                intent.putExtra("answer", answer);
                startActivityForResult(intent, 1024);
            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SavedAddressListActivity.class);
                startActivity(intent);
            }
        });

        linearLayoutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SharedWithMeActivity.class);
                intent.putExtra("isShared", true);
                intent.putExtra("shareType", "user");
                startActivity(intent);
            }
        });
        linearprivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAddressOptionActivity.class);
                startActivityForResult(intent, 1098);
            }
        });

        intProfile = this;
        get_profile();

        if (hasPermissions(PERMISSIONS)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                Toast.makeText(getActivity(), "Some Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,};

    @Override
    public void onRefresh() {
        models.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(getActivity(), models, "xyz", intProfile);
        recyclerView.setAdapter(custom_adaterFriends);
        get_profile();

        //Toast.makeText(getContext(),"hello",Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        get_profile();
        UtilityClass.hideLoading(loader, getActivity());
    }

    //..........................................Api...............................//
    public void get_profile() {
        try {
            if (UtilityClass.isNetworkConnected(getContext())) {
                SaveUserDetail model = UtilityClass.getLoginData(getContext());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, userId);

                UtilityClass.showLoading(loader, getActivity());
                Call<Object> call = RetrofitInstance.getdata().create(Api.class).getProfile(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (loader != null)
                            UtilityClass.hideLoading(loader, getActivity());

                        if (response.isSuccessful()) {
                            try {

                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                Log.e("PROFILE DATA:", "" + jsonObject);
                                String result_code = jsonObject.getString(Constants.RESULT_CODE);

                                if (result_code.equals(Constants.ZERO)) {
                                    //new Message().showSnack(root_lay, "No profile data available");

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
                                } else if (result_code.equals("1.0") || result_code.equals("1")) {
                                    String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                    JSONObject jsonObject1 = new JSONObject(result_data);
                                    userId = jsonObject1.getString("userId");
                                    if (jsonObject1.has("name")) {
                                        name = jsonObject1.getString("name");
                                    }
                                    if (jsonObject1.has("emailId")) {
                                        emailId = jsonObject1.getString("emailId");
                                    }
                                    contactNumber = jsonObject1.getString("contactNumber");
                                    isEmailIdVerified = jsonObject1.getString("isEmailIdVerified");
                                    isContactNumberVerified = jsonObject1.getString("isContactNumberVerified");
                                    question = jsonObject1.getString("security_question");
                                    answer = jsonObject1.getString("security_answer");

                                    txt_contactNumber.setText(contactNumber);
                                    txt_contactNumber.setVisibility(View.GONE);
                                    if (name!=null && !name.equals("")){
                                        profileName.setText(" "+name.substring(0,1).toUpperCase() + name.substring(1));
                                    }

                                    //profileUserName.setText(userName);
                                    profileEmail.setText(emailId);

                                    Log.e("question", "drfgfgdfg : " + question);
                                    if (question.equals("")) {
                                        txt_msg_profileComplete.setVisibility(View.VISIBLE);
                                    } else {
                                        txt_msg_profileComplete.setVisibility(View.GONE);
                                    }
                                    if (jsonObject1.has("profilePicURL")) {
                                        imgURL = jsonObject1.getString("profilePicURL");
                                    }else {
                                        progressBarImage.setVisibility(View.GONE);
                                    }
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Constants.userId, userId);
                                    editor.putString(Constants.contactNumber, contactNumber);

                                    if (jsonObject1.has("profilePicURL")) {
                                        editor.putString(Constants.profilePicURL, imgURL);
                                        if (imgURL != null && !imgURL.isEmpty()) {
                                            progressBarImage.setVisibility(View.VISIBLE);
                                            dpimg = Constants.IMG_URL + jsonObject1.getString("profilePicURL");

                                            Picasso.with(getContext())
                                                    .load(dpimg)
                                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                    .placeholder(R.drawable.profileplchlder)
                                                    .into(imgUserImage, new com.squareup.picasso.Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressBarImage.setVisibility(View.GONE);
                                                            imgUserImage.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                                                                    intent.putExtra("image", Constants.IMG_URL + imgURL);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            progressBarImage.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }else {
                                            imgUserImage.setImageResource(R.drawable.profileplchlder);

                                        }
                                    }else {
                                        editor.putString(Constants.profilePicURL, "");
                                        progressBarImage.setVisibility(View.GONE);
                                        imgUserImage.setImageResource(R.drawable.profileplchlder);
                                    }

                                    editor.commit();

                                }

                                getPrivateAddresses();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                        UtilityClass.hideLoading(loader, getActivity());
                    }
                });


            } else {
                new Message().showSnack(root_lay, Constants.noInternetMessage);
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1098 && resultCode == RESULT_OK) {
            getPrivateAddresses();
        } else if (requestCode == 1024 && resultCode == RESULT_OK) {
            get_profile();
        }
    }

    //...................................................api.............................//
    public void getPrivateAddresses() {
        UtilityClass.showLoading(loader, getActivity());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPrivateAddresses(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("Private_Response", "" + preferences.getString(Constants.userId, "") + new Gson().toJson(response.body()));
                UtilityClass.hideLoading(loader, getActivity());
                try {
                    models.clear();

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        consEmptyScreen.setVisibility(View.GONE);
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                        JSONArray jsonArray = new JSONArray(result_data);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String addressId = jsonObject1.getString("addressId");

                            String shortName = jsonObject1.getString("shortName");

                            String plusCode = jsonObject1.getString("plusCode");

                            String addressReferenceId = jsonObject1.getString("addressReferenceId");


                            String pictureURL = jsonObject1.getString("pictureURL");
                            ResultPrivateAddress resultPrivateAddress = new ResultPrivateAddress(addressId, shortName, plusCode, addressReferenceId, pictureURL, true, "", "");
                            models.add(resultPrivateAddress);


                        }


                    }

                    getPublicAddresses();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                UtilityClass.hideLoading(loader, getActivity());

            }
        });


//    }
    }


    public void getPublicAddresses() {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));

        UtilityClass.showLoading(loader, getActivity());

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getPublicAddresses(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                Log.e("Public_Response", new Gson().toJson(response.body()));
                UtilityClass.hideLoading(loader, getActivity());
                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        consEmptyScreen.setVisibility(View.GONE);

                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                        JSONArray jsonArray = new JSONArray(result_data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String addressId = jsonObject1.getString("addressId");

                            String shortName = jsonObject1.getString("shortName");

                            String plusCode = jsonObject1.getString("plusCode");

                            String addressReferenceId = jsonObject1.getString("addressReferenceId");

                            String pictureURL = jsonObject1.getString("pictureURL");

                            String description = jsonObject1.getString("description");

                            String categoryName = jsonObject1.getString("categoryName");


                            ResultPrivateAddress resultPrivateAddress = new ResultPrivateAddress(addressId, shortName, plusCode, addressReferenceId, pictureURL, false, categoryName, description);
                            models.add(resultPrivateAddress);

                        }

                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

                    recyclerView.setLayoutManager(linearLayoutManager);
                    AdaptorAddresses custom_adaterFriends = new AdaptorAddresses(getActivity(), models, "xyz", intProfile);
                    recyclerView.setAdapter(custom_adaterFriends);

                    if (models.size() == 0) {
                        consEmptyScreen.setVisibility(View.VISIBLE);
                    } else {
                        consEmptyScreen.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);

                UtilityClass.hideLoading(loader, getActivity());
            }
        });


//    }
    }


    //Profile Screen Interface Methods

    public void openPrivateAddress(Intent objIntent) {
        UtilityClass.showLoading(loader, getActivity());
        startActivityForResult(objIntent, 1098);
    }

    public void openPublicAddress(Intent objIntent) {
        UtilityClass.showLoading(loader, getActivity());
        startActivityForResult(objIntent, 1098);
    }


}