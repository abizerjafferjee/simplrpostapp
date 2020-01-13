package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_ViewPublicAddress;
import com.codeapex.simplrpostprod.Activity.Home_Activity_new;
import com.codeapex.simplrpostprod.Activity.SharedListActivity;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.UserData;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AdatperSharedWithOther extends RecyclerView.Adapter<AdatperSharedWithOther.Holderclass> {

    Context applicationContext;
    List<UserData> publiclist;
    SaveUnsaveAddress saveInterface;
    String listId, user_Id;
    boolean removeHeart;
    SharedPreferences preferences;

    public AdatperSharedWithOther(Context applicationContext, List<UserData> publiclist) {
        this.applicationContext = applicationContext;
        this.publiclist = publiclist;
        this.saveInterface = saveInterface;
        this.listId = listId;
        this.removeHeart = removeHeart;
        preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_friend_recycler, viewGroup, false);
        Holderclass holderclass = new Holderclass(view);
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdatperSharedWithOther.Holderclass viewHolder, int i) {

        viewHolder.uniqueLink.setText(publiclist.get(i).getName());
        viewHolder.address.setText(publiclist.get(i).getContactNumber());
        viewHolder.tag.setText("");

        if (publiclist.get(i).getProfilePicURL()!=null && !publiclist.get(i).getProfilePicURL().equals("")&&!publiclist.get(i).getProfilePicURL().contains("address_default_image.png")) {

            Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getProfilePicURL().replace("uploads/", ""))).placeholder(R.drawable.profileplchlder).into(viewHolder.imagepublic);

        }

        viewHolder.unsharePublic.setVisibility(View.VISIBLE);
        viewHolder.unsharePublic.setText("Block");
        viewHolder.unsharePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unshareAddressWithUser(publiclist.get(i).getID(),publiclist.get(i).getAddressTag(),i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return publiclist.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder {

        RelativeLayout cardView4;
        LinearLayout cardView,lnlytSharePublic;
        TextView uniqueLink, address, pluscode, tag,unsharePublic;
        ImageView imagepublic, fill;

        public Holderclass(@NonNull View itemView) {
            super(itemView);
            cardView4 = itemView.findViewById(R.id.cardView4);
            cardView = itemView.findViewById(R.id.cardView);
            lnlytSharePublic = itemView.findViewById(R.id.lnlytSharePublic);
            address = itemView.findViewById(R.id.address);
            uniqueLink = itemView.findViewById(R.id.uniqueLink);
            imagepublic = itemView.findViewById(R.id.imagepublic);
            fill = itemView.findViewById(R.id.fillICON);
            pluscode = itemView.findViewById(R.id.pluscode);
            tag = itemView.findViewById(R.id.tag);
            unsharePublic = itemView.findViewById(R.id.unsharePublic);

        }
    }

    public void unshareAddressWithBusiness(String receiverID, final int posi,String addressId,String strIsPublic) {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        String struserId = preferences.getString(Constants.userId, "");

        if (UtilityClass.isNetworkConnected(applicationContext)) {
            try {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(Constants.userId, struserId);
                hashMap.put(Constants.receiverId, receiverID);
                hashMap.put(Constants.addressId, addressId);
                hashMap.put(Constants.isAddressPublic, strIsPublic);


                Call<Object> call = RetrofitInstance.getdata().create(Api.class).unshareAddressWithBusiness(hashMap);
                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Log.e("unreB_Response", new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                    AlertDialog.Builder alertbox = new AlertDialog.Builder(applicationContext);
                                    alertbox.setMessage("Un shared successfully.");
                                    alertbox.setTitle("");
                                    alertbox.setCancelable(false);
                                    alertbox.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface arg0,
                                                                    int arg1) {
                                                    arg0.dismiss();

                                                }
                                            });
                                    alertbox.show();
                                }

//                                loader.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        new UtilityClass().alertBox(applicationContext, t.getLocalizedMessage(), t.getLocalizedMessage());
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void unshareAddressWithUser(String ID, String strIsPublic,int posi) {
        Parser.callApi(applicationContext, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).unShareAddress(ID,strIsPublic), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("resultCode");
                    if (result_code.equals("1")) {
                        //new Message().showSnackGreen(Home_Activity_new.Companion.getMain_layout_home(), "Address saved successfully.");

                        publiclist.remove(posi);
                        notifyDataSetChanged();

                    } else if (result_code.equals("0")) {
                        new Message().showSnack(Home_Activity_new.Companion.getMain_layout_home(), "" + jsonObject.optString("resultData"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

