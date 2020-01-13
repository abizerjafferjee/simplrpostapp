package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_Share;
import com.codeapex.simplrpostprod.Activity.Activity_ViewPublicAddress;
import com.codeapex.simplrpostprod.Activity.AddressesActivity;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
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

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class AdaptorAddresses extends RecyclerView.Adapter<AdaptorAddresses.Holderclass> {

    List<ModelResultPrivateAddress> models;
    SharedPreferences sharedPreferences;
    boolean addtypeprviate;
    FragmentActivity activity;

    private String From;
    String from,is_owner;
    ProfileScreenInterface intProfile;

    public AdaptorAddresses(FragmentActivity activity, List<ModelResultPrivateAddress> models, String from, ProfileScreenInterface intProfile) {
        this.activity = activity;
        this.models = models;
        this.from = from;
        this.intProfile = intProfile;
        sharedPreferences = activity.getSharedPreferences("Sesssion", MODE_PRIVATE);

    }


    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_adpterprofile_recycler, viewGroup, false);
        Holderclass holderclass = new Holderclass(view);

        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holderclass viewHolder, final int i) {

        String url = models.get(i).getUnique_link();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String[] segments = uri.getPath().split("/");
        String idStr = segments[segments.length-1];
        String prefix = idStr.replace("address-","");

        if (models.get(i).getAddress_tag().equals("private")) {
            viewHolder.cardView4.setVisibility(View.GONE);
            viewHolder.contactNumberPrivate.setText(models.get(i).getCountry());
            viewHolder.name.setText(prefix);
            viewHolder.address.setText(models.get(i).getPlus_code());

            if (models.get(i).getStreet_image()!=null && !models.get(i).getStreet_image().equals("")&&!models.get(i).getStreet_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                }
            } else if (models.get(i).getBuilding_image()!=null && !models.get(i).getBuilding_image().equals("") &&!models.get(i).getBuilding_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                }
            } else if (models.get(i).getEntrance_image()!=null && !models.get(i).getEntrance_image().equals("") &&!models.get(i).getEntrance_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.image);
                }
            }

            if (from.equals("shareAdd")) {
                viewHolder.sharePrivate.setVisibility(View.INVISIBLE);

                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewHolder.lnlytPrivateContent.getLayoutParams();
                params1.weight = 0.5f;
                viewHolder.lnlytPrivateContent.setLayoutParams(params1);

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activity.startActivity(new Intent(activity, Activity_ViewPublicAddress.class)
                                .putExtra("address_id", models.get(i).getAddressId())
                                .putExtra("user_id", models.get(i).getUserId())
                                .putExtra("profile_img", models.get(i).getUserImage())
                                .putExtra("user_name", models.get(i).getUserName())
                                .putExtra("plus_code", models.get(i).getPlus_code())
                                .putExtra("public_private_tag", models.get(i).getAddress_tag())
                                .putExtra("qr_code_img", models.get(i).getQrCode_image())
                                .putExtra("street_img", models.get(i).getStreet_image())
                                .putExtra("building_img", models.get(i).getBuilding_image())
                                .putExtra("entrance_img", models.get(i).getEntrance_image())
                                .putExtra("address_unique_link", models.get(i).getUnique_link())
                                .putExtra("country", models.get(i).getCountry())
                                .putExtra("city", models.get(i).getCity())
                                .putExtra("street", models.get(i).getStreet_name())
                                .putExtra("building", models.get(i).getBuilding_name())
                                .putExtra("entrance", models.get(i).getEntrance_name())
                                .putExtra("latitude", String.valueOf(models.get(i).getLatitude()))
                                .putExtra("longitude", String.valueOf(models.get(i).getLongitude()))
                                .putExtra("street_img_type", models.get(i).getStreet_img_type())
                                .putExtra("building_img_type", models.get(i).getBuilding_img_type())
                                .putExtra("entrance_img_type", models.get(i).getEntrance_img_type())
                                .putExtra("from", "view")
                                .putExtra("direction_txt", models.get(i).getDirection_text()));

                    }
                });

            }
            else {
                viewHolder.sharePrivate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (from.equals("share my address")){
                            if ( models.get(viewHolder.getAdapterPosition()).getAddress_tag().equals("public")) {
                                shareAddress(AddressesActivity.recieverId, models.get(viewHolder.getAdapterPosition()).getAddressId(), models.get(viewHolder.getAdapterPosition()).getAddress_tag(), true);
                            }else {
                                shareAddress(AddressesActivity.recieverId, models.get(viewHolder.getAdapterPosition()).getAddressId(), models.get(viewHolder.getAdapterPosition()).getAddress_tag(), false);
                            }

                        }else {
                            if (sharedPreferences.getString(Constants.userId, "").equals(models.get(viewHolder.getAdapterPosition()).getUserId())) {
                                is_owner = "true";
                            }else {
                                is_owner = "false";

                            }
                            if (is_owner.equals("true")) {
                                activity.startActivity(new Intent(activity, Activity_Share.class)
                                        .putExtra("is_owner", "true")
                                        .putExtra("is_private", models.get(viewHolder.getAdapterPosition()).getAddress_tag())
                                        .putExtra("shareDirect", false)
                                        .putExtra("unique_link", models.get(viewHolder.getAdapterPosition()).getUnique_link())
                                        .putExtra("address_id", models.get(viewHolder.getAdapterPosition()).getAddressId()));
                            } else {
                                activity.startActivity(new Intent(activity, Activity_Share.class).putExtra("is_owner", "false")
                                        .putExtra("is_private", models.get(viewHolder.getAdapterPosition()).getAddress_tag())
                                        .putExtra("shareDirect",false)
                                        .putExtra("is_owner", "false")
                                        .putExtra("unique_link", models.get(viewHolder.getAdapterPosition()).getUnique_link())
                                        .putExtra("address_id", models.get(viewHolder.getAdapterPosition()).getAddressId()));
                            }
                        }
                    }
                });
                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activity.startActivity(new Intent(activity, Activity_ViewPublicAddress.class)
                                .putExtra("user_id", models.get(i).getUserId())
                                .putExtra("address_id", models.get(i).getAddressId())
                                .putExtra("profile_img", models.get(i).getUserImage())
                                .putExtra("user_name", models.get(i).getUserName())
                                .putExtra("plus_code", models.get(i).getPlus_code())
                                .putExtra("public_private_tag", models.get(i).getAddress_tag())
                                .putExtra("qr_code_img", models.get(i).getQrCode_image())
                                .putExtra("street_img", models.get(i).getStreet_image())
                                .putExtra("building_img", models.get(i).getBuilding_image())
                                .putExtra("entrance_img", models.get(i).getEntrance_image())
                                .putExtra("address_unique_link", models.get(i).getUnique_link())
                                .putExtra("country", models.get(i).getCountry())
                                .putExtra("city", models.get(i).getCity())
                                .putExtra("street", models.get(i).getStreet_name())
                                .putExtra("building", models.get(i).getBuilding_name())
                                .putExtra("entrance", models.get(i).getEntrance_name())
                                .putExtra("latitude", models.get(i).getLatitude())
                                .putExtra("longitude", models.get(i).getLongitude())
                                .putExtra("street_img_type", models.get(i).getStreet_img_type())
                                .putExtra("building_img_type", models.get(i).getBuilding_img_type())
                                .putExtra("entrance_img_type", models.get(i).getEntrance_img_type())
                                .putExtra("direction_txt", models.get(i).getDirection_text())
                                .putExtra("from", "view"));
                    }
                });
            }

        }
        else {
            viewHolder.cardView.setVisibility(View.GONE);
            viewHolder.cardView4.setVisibility(View.VISIBLE);

            viewHolder.name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            viewHolder.address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            final String addressIdd = models.get(i).getAddressId();
            viewHolder.pluscode.setText(models.get(i).getPlus_code());
            viewHolder.contactNumberPublic.setText(models.get(i).getCountry());
            viewHolder.namepublic.setText(prefix);
            if (models.get(i).getStreet_image()!=null && !models.get(i).getStreet_image().equals("")&&!models.get(i).getStreet_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
                }
            } else if (models.get(i).getBuilding_image()!=null && !models.get(i).getBuilding_image().equals("") &&!models.get(i).getBuilding_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
                }
            } else if (models.get(i).getEntrance_image()!=null && !models.get(i).getEntrance_image().equals("") &&!models.get(i).getEntrance_image().contains("address_default_image.png")) {
                if (models.get(i).getAddress_tag().equals("public")) {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.public_placeholder).into(viewHolder.imagepublic);
                } else {
                    Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.private_placeholder).into(viewHolder.imagepublic);
                }
            }

            if (from.equals("shareAdd")) {
                viewHolder.sharePublic.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewHolder.lnlytPublicContent.getLayoutParams();
                params1.weight = 0.5f;
                viewHolder.lnlytPublicContent.setLayoutParams(params1);
                viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("addressId", addressIdd);
                        intent.putExtra("isPrivate", false);
                        activity.setResult(10, intent);
                        activity.finish();
                    }
                });
            } else {
                viewHolder.sharePublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Context wrapper = new ContextThemeWrapper(activity.getApplicationContext(), R.style.MyPopupOtherStyle);
                        PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                        popup.getMenuInflater().inflate(R.menu.share_address_option, popup.getMenu());
                        //show menu
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getTitle().toString().equals("Within App")) {
                                    Intent intent = new Intent(activity.getApplicationContext(), SearchBusinessFragment.class);
                                    intent.putExtra("from", "shareProfile");
                                    intent.putExtra("shareAddressId", models.get(i).getAddressId());
                                    intent.putExtra("shareAddressIsPublic", "1");
                                    activity.startActivity(intent);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PUB-" + models.get(i).getAddressId());
                                    activity.startActivity(Intent.createChooser(intent, "Share"));
                                }
                                return true;
                            }
                        });*/
                        if (sharedPreferences.getString(Constants.userId, "").equals(models.get(viewHolder.getAdapterPosition()).getUserId())) {
                            is_owner = "true";
                        }else {
                            is_owner = "false";

                        }
                        if (is_owner.equals("true")) {
                            activity.startActivity(new Intent(activity, Activity_Share.class)
                                    .putExtra("is_owner", "true")
                                    .putExtra("is_private", models.get(viewHolder.getAdapterPosition()).getAddress_tag())
                                    .putExtra("shareDirect", false)
                                    .putExtra("unique_link", models.get(viewHolder.getAdapterPosition()).getUnique_link())
                                    .putExtra("address_id", models.get(viewHolder.getAdapterPosition()).getAddressId()));
                        } else {
                            activity.startActivity(new Intent(activity, Activity_Share.class).putExtra("is_owner", "false")
                                    .putExtra("is_private", models.get(viewHolder.getAdapterPosition()).getAddress_tag())
                                    .putExtra("shareDirect",false)
                                    .putExtra("is_owner", "false")
                                    .putExtra("unique_link", models.get(viewHolder.getAdapterPosition()).getUnique_link())
                                    .putExtra("address_id", models.get(viewHolder.getAdapterPosition()).getAddressId()));
                        }
                    }
                });
                viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(models.size()>i) {
                            viewHolder.getLayoutPosition();
                            activity.startActivity(new Intent(activity, Activity_ViewPublicAddress.class)
                                    .putExtra("address_id", models.get(i).getAddressId())
                                    .putExtra("user_id", models.get(i).getUserId())
                                    .putExtra("profile_img", models.get(i).getUserImage())
                                    .putExtra("user_name", models.get(i).getUserName())
                                    .putExtra("plus_code", models.get(i).getPlus_code())
                                    .putExtra("public_private_tag", models.get(i).getAddress_tag())
                                    .putExtra("qr_code_img", models.get(i).getQrCode_image())
                                    .putExtra("street_img", models.get(i).getStreet_image())
                                    .putExtra("building_img", models.get(i).getBuilding_image())
                                    .putExtra("entrance_img", models.get(i).getEntrance_image())
                                    .putExtra("address_unique_link", models.get(i).getUnique_link())
                                    .putExtra("country", models.get(i).getCountry())
                                    .putExtra("city", models.get(i).getCity())
                                    .putExtra("street", models.get(i).getStreet_name())
                                    .putExtra("building", models.get(i).getBuilding_name())
                                    .putExtra("entrance", models.get(i).getEntrance_name())
                                    .putExtra("latitude", String.valueOf(models.get(i).getLatitude()))
                                    .putExtra("longitude", String.valueOf(models.get(i).getLongitude()))
                                    .putExtra("direction_txt", models.get(i).getDirection_text())
                                    .putExtra("street_img_type", models.get(i).getStreet_img_type())
                                    .putExtra("building_img_type", models.get(i).getBuilding_img_type())
                                    .putExtra("entrance_img_type", models.get(i).getEntrance_img_type())
                                    .putExtra("from", "view"));
                        }
                    }
                });

            }

        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder {
        RelativeLayout cardView, cardView4;
        TextView name, address, namepublic, pluscode, contactNumberPrivate,contactNumberPublic, descrpition,sharePrivate, sharePublic;
        ImageView  image, imagepublic;
        LinearLayout lnlytPrivate, lnlytPublic, lnlytPublicContent, lnlytPrivateContent;

        public Holderclass(@NonNull View itemView) {
            super(itemView);
            cardView4 =  itemView.findViewById(R.id.cardView4);
            name = (TextView) itemView.findViewById(R.id.name);
            pluscode = (TextView) itemView.findViewById(R.id.pluscode);
            contactNumberPublic = (TextView) itemView.findViewById(R.id.contactNumberPublic);
            contactNumberPrivate = (TextView) itemView.findViewById(R.id.contactNumberPrivate);
            namepublic = (TextView) itemView.findViewById(R.id.namepublic);
            address = (TextView) itemView.findViewById(R.id.address);
            cardView =  itemView.findViewById(R.id.cardView);
            sharePrivate =  itemView.findViewById(R.id.sharePrivate);
            sharePublic =  itemView.findViewById(R.id.sharePublic);
            image = (ImageView) itemView.findViewById(R.id.imagee);
            imagepublic = (ImageView) itemView.findViewById(R.id.imagepublic);
            lnlytPrivate = (LinearLayout) itemView.findViewById(R.id.lnlytSharePrivate);
            lnlytPublic = (LinearLayout) itemView.findViewById(R.id.lnlytSharePublic);
            lnlytPrivateContent = (LinearLayout) itemView.findViewById(R.id.lnlytPrivateContent);
            lnlytPublicContent = (LinearLayout) itemView.findViewById(R.id.lnlytPublicContent);
        }
    }

    public void shareAddress(String receiverId,String addressID, String isPublic, Boolean isWithUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, sharedPreferences.getString(Constants.userId, ""));
        hashMap.put(Constants.receiverId, receiverId); //searched user or public address
        hashMap.put(Constants.addressId, addressID); //address to share
        hashMap.put(Constants.isAddressPublic, isPublic);
        Log.d(TAG, "saveAddressToSavedList: " + receiverId  + "  " + addressID + "  " + isPublic);

        Call<Object> call;
        if (isWithUser)
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithUser(hashMap);
        else
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithBusiness(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("share_Response", new Gson().toJson(response.body()));

                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_EIGHT)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                    } else if (result_code.equals("-11.0")) {
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
                        alertbox.setMessage("This address is already shared.");
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
                    } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                        fill.setImageResource(R.drawable.hearticon);
//                        new UtilityClass().simpleAlertBox(SearchBusinessFragment.this,"Your address shared successfully.");
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
                        alertbox.setMessage("Your address shared successfully.");
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

//                        Toast.makeText(SearchBusinessFragment.this, "Your address shared successfully.", Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);

            }
        });


    }

}


