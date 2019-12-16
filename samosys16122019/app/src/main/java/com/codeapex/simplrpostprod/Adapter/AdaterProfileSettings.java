package com.codeapex.simplrpostprod.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeapex.simplrpostprod.Activity.ChangePasswordActivity;
import com.codeapex.simplrpostprod.Activity.SignInActivity;
import com.codeapex.simplrpostprod.ModelClass.MyList;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class AdaterProfileSettings extends RecyclerView.Adapter<AdaterProfileSettings.Holderclass> {
    Context applicationContext;
    private MyList[] listdata;
    private int selectedPosition;




    public AdaterProfileSettings(Context applicationContext, MyList[] myListData) {
        this.applicationContext = applicationContext;
        this.listdata = myListData;

    }

    @NonNull
    @Override
    public AdaterProfileSettings.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_profilesetting,viewGroup,false);
        AdaterProfileSettings.Holderclass holderclass =new AdaterProfileSettings.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaterProfileSettings.Holderclass viewHolder, final int position) {
        final MyList myListData = listdata[position];
        viewHolder.text.setText(listdata[position].getDescription());

        viewHolder.imageView.setImageResource(listdata[position].getImgId());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0: {
                        if (UtilityClass.isNetworkConnected(applicationContext)) {
                            Intent intent = new Intent(applicationContext, ChangePasswordActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            applicationContext.startActivity(intent);
                        }
                        else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage(Constants.noInternetMessage);
                            alertbox.setTitle("");
                            alertbox.setCancelable(false);
                            alertbox.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                        }
                                    });
                            alertbox.show();
                        }
                    }
                    break;
                    case 1: {
                        if (UtilityClass.isNetworkConnected(applicationContext)) {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage("Are you sure you want to deactivate this account? You can login anytime to activate your account again.");
                            alertbox.setTitle("");
                            alertbox.setCancelable(false);
                            alertbox.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alertbox.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            deactivate();

                                        }
                                    });
                            alertbox.show();
                        }
                        else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage(Constants.noInternetMessage);
                            alertbox.setTitle("");
                            alertbox.setCancelable(false);
                            alertbox.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                        }
                                    });
                            alertbox.show();
                        }


                        break;
                    }
                    case 2: {
                        if (UtilityClass.isNetworkConnected(applicationContext)) {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage("Are you sure you want to delete this account? You won't be able to access your account again after deleting it. For temporary period, choose deactivate option.");
                            alertbox.setTitle("");
                            alertbox.setCancelable(false);
                            alertbox.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alertbox.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            delete();

                                        }
                                    });
                            alertbox.show();
                        }
                        else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage(Constants.noInternetMessage);
                            alertbox.setTitle("");
                            alertbox.setCancelable(false);
                            alertbox.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                        }
                                    });
                            alertbox.show();
                        }

                        break;
                    }
                    case 3: {

                        if (UtilityClass.isNetworkConnected(applicationContext)) {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage("Are you sure you want to logout?");
                            alertbox.setCancelable(false);
                            alertbox.setTitle("Alert");
                            alertbox.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            alertbox.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            logout();

                                        }
                                    });
                            alertbox.show();
                        }
                        else {
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                            alertbox.setMessage(Constants.noInternetMessage);
                            alertbox.setCancelable(false);
                            alertbox.setTitle("");
                            alertbox.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {

                                        }
                                    });
                            alertbox.show();
                        }


                        break;
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        TextView text;
        ImageView imageView;
        CardView cardView;
        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }

    }



    public void logout() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId,"");
        Log.d(TAG, "onCreateView: "+struserId);

        try {



            final String strDeviceid = android.provider.Settings.Secure.getString(applicationContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);
            hashMap.put(Constants.deviceId, strDeviceid);
            hashMap.put(Constants.deviceType, "a");
            hashMap.put(Constants.pushToken, " ");
            Log.d(TAG, "logout: "+struserId+strDeviceid+Constants.deviceType+Constants.pushToken);



            Call<Object> call = RetrofitInstance.getdata().create(Api.class).signOut(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        try {
                            Log.e("TAG", "logout>>>\n: " + new Gson().toJson(response.body()));
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);


                                SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Constants.isUserLoggedIn, false);
                                editor.commit();


                                Intent intent = new Intent(applicationContext, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                               applicationContext.startActivity(intent);
//                                ((Activity)applicationContext).finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }
    }


    public void deactivate() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId,"");

        try {



            final String strDeviceid = android.provider.Settings.Secure.getString(applicationContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);

            Log.d(TAG, "deactive: "+struserId+strDeviceid+Constants.deviceType+Constants.pushToken);



            Call<Object> call = RetrofitInstance.getdata().create(Api.class).deactivateAccount(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        try {
                            Log.e("TAG", "Datagetdeactive>>>\n: " + new Gson().toJson(response.body()));
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Constants.isUserLoggedIn, false);
                                editor.commit();
                                Intent intent = new Intent(applicationContext, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                applicationContext.startActivity(intent);
                                ((Activity)applicationContext).finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }
    }


    public void delete() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId,"");

        try {



            final String strDeviceid = android.provider.Settings.Secure.getString(applicationContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);

            Log.d(TAG, "delete: "+struserId+strDeviceid+Constants.deviceType+Constants.pushToken);



            Call<Object> call = RetrofitInstance.getdata().create(Api.class).deleteAccount(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        try {
                            Log.e("TAG", "Datagetdelete>>>\n: " + new Gson().toJson(response.body()));
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(Constants.isUserLoggedIn, false);
                                editor.commit();
                                Intent intent = new Intent(applicationContext, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                applicationContext.startActivity(intent);
                                ((Activity)applicationContext).finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });
        } catch (Exception e) {

        }
    }


}