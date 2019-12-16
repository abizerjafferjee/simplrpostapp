package com.codeapex.simplrpostprod.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.codeapex.simplrpostprod.Adapter.AdaterNotification;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ModelNotification;
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
import static android.content.Context.MODE_PRIVATE;

public class NotifiactionFragment extends Fragment implements OnRefreshListener {

    //==========================Defined Globles Vriables==================================//

    RecyclerView recyclerView;
    String user_Id;
    ConstraintLayout root_lay;
    ProgressBar Loader;
    List<ModelNotification> models;

    SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification, container, false);



        SharedPreferences preferences = getActivity().getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        Log.d(TAG, "onCreateView: " + user_Id);

        models = new ArrayList<>();
        root_lay = view.findViewById(R.id.root_lay);
        Loader = view.findViewById(R.id.Loader);
        recyclerView = view.findViewById(R.id.recyclervieww);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshNotification);

        swipeRefreshLayout.setOnRefreshListener(this);

        getNotificationList();

        return view;
    }

    @Override
    public void onRefresh(){
        models.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        AdaterNotification custom_adaterFriends = new AdaterNotification(getActivity(),models);
        recyclerView.setAdapter(custom_adaterFriends);
        getNotificationList();
        //Toast.makeText(getContext(),"hello",Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }


    public void getNotificationList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, user_Id);
        hashMap.put(Constants.start, "0");
        hashMap.put(Constants.count, "20");
        UtilityClass.showLoading(Loader,getActivity());

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).getNotificationList(hashMap);
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

                        JSONArray jsonArray = jsonObject.getJSONArray(Constants.RESULT_DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                            String notificationId = jsonObject1.getString("notificationId");
                           // Log.d(TAG, "onResponse: ");
                            String notificationInformation = jsonObject1.getString("information");

                            String createDate = jsonObject1.getString("createDate");


                            ModelNotification modelNotification = new ModelNotification(notificationId,notificationInformation,createDate);
                            models.add(modelNotification);

                        }


                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);

                        AdaterNotification custom_adaterFriends = new AdaterNotification(getActivity(),models);
                        recyclerView.setAdapter(custom_adaterFriends);
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



}

//================================Services Api========================================//

//
//    public void base_url() {
//        try {
////            if (new TypefaceUtil().isNetworkConnected(getApplicationContext())) {
//            if (!isLoading) {
//                isLoading = true;
//                Call<Object> call = RetrofitInstance.getdata().create(Api.class).privacy_policy();
//                Loader.setVisibility(View.VISIBLE);
//                call.enqueue(new Callback <Object> () {
//                    @Override
//                    public void onResponse(Call<Object> call, Response<Object> response) {
//                        if (response.isSuccessful()) {
//                        try{
//                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
//                            String result_code = jsonObject.getString("result_code");
//                            Toast.makeText(getActivity(), "sucess"+response, Toast.LENGTH_SHORT).show();
//
//
//
//
//
//
//                        }catch (Exception e){
//
//                        }
//
//
//
//
//
//
//
//
//                        }
//                    }
//
//
//
//
//
//                    @Override
//                    public void onFailure(Call<Object> call, Throwable t) {
//
//                    }
//                });
//            }
//        }catch (Exception e){
//
//        }
//
//
////     public void base_url() {
////        try {
////            apiService.adminownerList(society_id).enqueue(new Callback<List<GetAdminOwnerList>>() {
////                @Override
////                public void onResponse(Call<List<GetAdminOwnerList>> call, Response<List<GetAdminOwnerList>> response) {
////                    List<GetAdminOwnerList> models = response.body();
////                    progressDialog.dismiss();
////                    try {
////                        for (int i = 0; i < models.size(); i++) {
////                            String owner_id = models.get(i).getFlatOwnerId();
////                            String owner_name = models.get(i).getOwnerName();
////                            String owner_photo = models.get(i).getOwnerPhoto();
////                            String wing_name = models.get(i).getWingName();
////                            String flat_number = models.get(i).getFlatNumber();
////                            GetAdminOwnerList ownerList = new GetAdminOwnerList(owner_id, owner_name, owner_photo, wing_name, flat_number);
////                            list.add(ownerList);
////                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
////                            owner_recycler.setLayoutManager(linearLayoutManager);
////                            Admin_Owner_Adapter admin_owner_adapter = new Admin_Owner_Adapter(getActivity(), list);
////                            owner_recycler.setAdapter(admin_owner_adapter);
////                        }
////                    } catch (Exception e) {
////                        System.out.println(e);
////                    }
////                }
////
////                @Override
////                public void onFailure(Call<List<GetAdminOwnerList>> call, Throwable t) {
////                    progressDialog.dismiss();
////                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
////                    alertDialogBuilder.setMessage("Server Error or Make sure your internet connection is on..!!");
////                    alertDialogBuilder.setPositiveButton("Ok",
////                            new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface arg0, int arg1) {
////                                }
////                            });
////                    alertDialogBuilder.setNegativeButton("cancel",
////                            new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface arg0, int arg1) {
////
////                                }
////                            });
////                    AlertDialog alertDialog = alertDialogBuilder.create();
////                    alertDialog.show();
////                }
////            });
////        } catch (Exception e) {
////            System.out.println(e);
////        }
////    }
//    }
//}