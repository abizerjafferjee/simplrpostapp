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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_ViewPublicAddress;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.ModelClass.savedItems;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdatperSavedAddresses extends RecyclerView.Adapter<AdatperSavedAddresses.Holderclass> {

    Context applicationContext;
    List<savedItems> publiclist;
    SaveUnsaveAddress saveInterface;
    String listId, user_Id;
    boolean removeHeart;

    public AdatperSavedAddresses(Context applicationContext, List<savedItems> publiclist, SaveUnsaveAddress saveInterface, String listId, boolean removeHeart) {
        this.applicationContext = applicationContext;
        this.publiclist = publiclist;
        this.saveInterface = saveInterface;
        this.listId = listId;
        this.removeHeart = removeHeart;
    }

    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_adapter_item, viewGroup, false);
        Holderclass holderclass = new Holderclass(view);
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");
        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdatperSavedAddresses.Holderclass viewHolder, int i) {
        final String addressIdd = publiclist.get(viewHolder.getLayoutPosition()).getAddressId();

        String url = publiclist.get(i).getUnique_link();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String[] segments = uri.getPath().split("/");
        String idStr = segments[segments.length-1];
        String prefix = idStr.replace("address-","");

        viewHolder.pluscode.setText(publiclist.get(i).getPlus_code());
        viewHolder.uniqueLink.setText(prefix);
        viewHolder.address.setText(publiclist.get(i).getCountry());
        viewHolder.tag.setText("");
        viewHolder.lnlytSharePublic.setVisibility(View.VISIBLE);
        if (publiclist.get(i).getStreet_image()!=null && !publiclist.get(i).getStreet_image().equals("")&&!publiclist.get(i).getStreet_image().contains("address_default_image.png")) {
            if (publiclist.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
            }
        } else if (publiclist.get(i).getBuilding_image()!=null && !publiclist.get(i).getBuilding_image().equals("") &&!publiclist.get(i).getBuilding_image().contains("address_default_image.png")) {
            if (publiclist.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
            }
        } else if (publiclist.get(i).getEntrance_image()!=null && !publiclist.get(i).getEntrance_image().equals("") &&!publiclist.get(i).getEntrance_image().contains("address_default_image.png")) {
            if (publiclist.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
            }
        }

        viewHolder.fill.setOnClickListener(new View.OnClickListener() {
            Boolean isSave;
            @Override
            public void onClick(View v) {
                HashMap<String, String> data = new HashMap<>();
                final Bitmap bmap = ((BitmapDrawable) viewHolder.fill.getDrawable()).getBitmap();
                Drawable myDrawable = applicationContext.getResources().getDrawable(R.drawable.hearticon);
                final Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                if (bmap.sameAs(myLogo)) {
                    isSave = false;
                    viewHolder.fill.setImageResource(R.drawable.like);
                } else {
                    isSave = true;
                    viewHolder.fill.setImageResource(R.drawable.hearticon);
                }
                data.put(Constants.userId, user_Id);
                data.put(Constants.listId, listId);
                data.put(Constants.addressId, addressIdd);
                data.put(Constants.isPublic, "1");
                saveInterface.saveUnsaveAddress(data, isSave);
            }
        });

        viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                applicationContext.startActivity(new Intent(applicationContext, Activity_ViewPublicAddress.class)
                        .putExtra("address_id", publiclist.get(i).getAddressId())
                        .putExtra("user_id", publiclist.get(i).getUserId())
                        .putExtra("profile_img", publiclist.get(i).getUserImage())
                        .putExtra("user_name", publiclist.get(i).getUserName())
                        .putExtra("plus_code", publiclist.get(i).getPlus_code())
                        .putExtra("public_private_tag", publiclist.get(i).getAddress_tag())
                        .putExtra("qr_code_img", publiclist.get(i).getQrCode_image())
                        .putExtra("street_img", publiclist.get(i).getStreet_image())
                        .putExtra("building_img", publiclist.get(i).getBuilding_image())
                        .putExtra("entrance_img", publiclist.get(i).getEntrance_image())
                        .putExtra("address_unique_link", publiclist.get(i).getUnique_link())
                        .putExtra("country", publiclist.get(i).getCountry())
                        .putExtra("city", publiclist.get(i).getCity())
                        .putExtra("street", publiclist.get(i).getStreet_name())
                        .putExtra("building", publiclist.get(i).getBuilding_name())
                        .putExtra("entrance", publiclist.get(i).getEntrance_name())
                        .putExtra("latitude", String.valueOf(publiclist.get(i).getLatitude()))
                        .putExtra("longitude", String.valueOf(publiclist.get(i).getLongitude()))
                        .putExtra("direction_txt", publiclist.get(i).getDirection_text())
                        .putExtra("street_img_type", publiclist.get(i).getStreet_img_type())
                        .putExtra("building_img_type", publiclist.get(i).getBuilding_img_type())
                        .putExtra("entrance_img_type", publiclist.get(i).getEntrance_img_type())
                        .putExtra("from", "favorite"));
            }
        });

        viewHolder.unsharePublic.setVisibility(View.VISIBLE);
        viewHolder.unsharePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Parser.callApi(applicationContext, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).removeSavedAddress(publiclist.get(i).getId()), new Response_Call_Back() {
                    @Override
                    public void getResponseFromServer(String response) {
                        Log.e("shared with business", "response :" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String result_code = jsonObject.getString("resultCode");

                            if (result_code.equals("1")) {
                                publiclist.remove(i);
                                notifyDataSetChanged();
                            } else if(result_code.equals("0")){
                                Toast.makeText(applicationContext,""+jsonObject.getString("resultData"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                });
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
        TextView uniqueLink, address, pluscode, tag, unsharePublic;
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
            unsharePublic = itemView.findViewById(R.id.unsharePublic_);

        }
    }
}

