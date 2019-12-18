package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.ImagePreviewActivity;
import com.codeapex.simplrpostprod.Activity.PrivateLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Activity.PublicLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Activity.SearchBusinessActivity;
import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.ResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaptorAddresses extends RecyclerView.Adapter<AdaptorAddresses.Holderclass>  {

    List<ResultPrivateAddress> models;
    SharedPreferences sharedPreferences;
    boolean addtypeprviate ;
    FragmentActivity activity;

    private String TAG;
    String from;
    ProfileScreenInterface intProfile;

    public AdaptorAddresses(FragmentActivity activity, List<ResultPrivateAddress> models, String from, ProfileScreenInterface intProfile) {
        this.activity = activity;
        this.models = models;
        this.from = from;
        this.intProfile = intProfile;
    }



    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_adpterprofile_recycler,viewGroup,false);
        Holderclass holderclass =new Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holderclass viewHolder, int i) {


            if (models.get(i).getPriavte() == true) {
                viewHolder.cardView4.setVisibility(View.GONE);
                final String addressId = models.get(i).getAddressId();
                Log.d(TAG, "codeapexx: " + addressId);
                ///    Toast.makeText(activity, "cpdeapex" + addressId, Toast.LENGTH_SHORT).show();
                viewHolder.name.setText(models.get(i).getShortName());
                viewHolder.address.setText(models.get(i).getPlusCode());
                Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getPictureURL())).placeholder(R.drawable.profile_private).into(viewHolder.image);
                final String imgURL = models.get(i).getPictureURL();
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!imgURL.equals("")) {
                            Intent intent = new Intent(activity, ImagePreviewActivity.class);
                            intent.putExtra("image",Constants.IMG_URL+imgURL);
                            activity.startActivity(intent);
                        }
                    }
                });

                if(from.equals("shareAdd")){
                    viewHolder.sharePrivate.setVisibility(View.INVISIBLE);

                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewHolder.lnlytPrivateContent.getLayoutParams();
                    params1.weight = 0.5f;
                    viewHolder.lnlytPrivateContent.setLayoutParams(params1);


                    viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("addressId",addressId);
                            intent.putExtra("isPrivate",true);
                            activity.setResult(10,intent);
                            activity.finish();
                        }
                    });
                }
                else {
                    viewHolder.sharePrivate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context wrapper = new ContextThemeWrapper(activity.getApplicationContext(), R.style.MyPopupOtherStyle);
                            PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                            popup.getMenuInflater().inflate(R.menu.share_address_option, popup.getMenu());
                            //show menu
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().toString().equals("Within App")){
                                        Intent intent = new Intent(activity.getApplicationContext(), SearchBusinessActivity.class);
                                        intent.putExtra("from","shareProfile");
                                        intent.putExtra("shareAddressId",models.get(viewHolder.getAdapterPosition()).getAddressId());
                                        intent.putExtra("shareAddressIsPublic","0");
                                        activity.startActivity(intent);
                                    }
                                    else  {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PRI-"+models.get(viewHolder.getAdapterPosition()).getAddressReferenceId());
                                        activity.startActivity(Intent.createChooser(intent, "Share"));
                                    }
                                    return true;
                                }
                            });
                        }
                    });
                    viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: "+viewHolder.getLayoutPosition());
                            ResultPrivateAddress obj = models.get(viewHolder.getLayoutPosition());
                            Intent intent = new Intent(activity, PrivateLocationDetailNewActivity.class);
                            intent.putExtra("addressId", addressId);
                            intent.putExtra("isOwn",true);
                            intent.putExtra("isFromShared",true);
                            intProfile.openPrivateAddress(intent);
//                            activity.startActivity(intent);
                        }
                    });
                }



            } else {
                viewHolder.cardView.setVisibility(View.GONE);
                viewHolder.cardView4.setVisibility(View.VISIBLE);
                final String addressIdd = models.get(i).getAddressId();
                viewHolder.pluscode.setText(models.get(i).getPlusCode());
                viewHolder.addreses.setText(models.get(i).getCategoryName());
                viewHolder.namepublic.setText(models.get(i).getShortName());
//                viewHolder.descrpition.setText(models.get(i).getDescription());
                Picasso.with(activity).load(Constants.IMG_URL.concat(models.get(i).getPictureURL())).placeholder(R.drawable.profile_public).into(viewHolder.imagepublic);
                final String imgURL = models.get(i).getPictureURL();
                viewHolder.imagepublic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!imgURL.equals("")) {
                            Intent intent = new Intent(activity, ImagePreviewActivity.class);
                            intent.putExtra("image",Constants.IMG_URL+imgURL);
                            activity.startActivity(intent);
                        }
                    }
                });

                Log.d(TAG, "codeapexx: " + addressIdd);
                //       Toast.makeText(activity, "cpdeapex" + addressIdd, Toast.LENGTH_SHORT).show();
                if(from.equals("shareAdd")){
                    viewHolder.sharePublic.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewHolder.lnlytPublicContent.getLayoutParams();
                    params1.weight = 0.5f;
                    viewHolder.lnlytPublicContent.setLayoutParams(params1);
                    viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("addressId",addressIdd);
                            intent.putExtra("isPrivate",false);
                            activity.setResult(10,intent);
                            activity.finish();
                        }
                    });
                }else {
                    viewHolder.sharePublic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context wrapper = new ContextThemeWrapper(activity.getApplicationContext(), R.style.MyPopupOtherStyle);
                            PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                            popup.getMenuInflater().inflate(R.menu.share_address_option, popup.getMenu());
                            //show menu
                            popup.show();
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().toString().equals("Within App")){
                                        Intent intent = new Intent(activity.getApplicationContext(), SearchBusinessActivity.class);
                                        intent.putExtra("from","shareProfile");
                                        intent.putExtra("shareAddressId",models.get(viewHolder.getAdapterPosition()).getAddressId());
                                        intent.putExtra("shareAddressIsPublic","1");
                                        activity.startActivity(intent);
                                    }
                                    else  {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out my address on Simplr post at https://code-apex.com/simplrPost/PUB-"+models.get(viewHolder.getAdapterPosition()).getAddressReferenceId());
                                        activity.startActivity(Intent.createChooser(intent, "Share"));
                                    }
                                    return true;
                                }
                            });
                        }
                    });
                    viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.getLayoutPosition();

                            Intent intent = new Intent(activity, PublicLocationDetailNewActivity.class);
                            intent.putExtra("addressId", addressIdd);
                            intent.putExtra("isOwn",true);
                            intent.putExtra("isFromShared",true);

                            intProfile.openPublicAddress(intent);
                        }
                    });

                }

            }

    }





    @Override
    public int getItemCount() {
        return models.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        CardView cardView,cardView4;
        TextView name , address,namepublic,pluscode,addreses,descrpition;
        ImageView sharePrivate, sharePublic,image,imagepublic;
        LinearLayout lnlytPrivate, lnlytPublic,lnlytPublicContent,lnlytPrivateContent;

        public Holderclass(@NonNull View itemView)

        {
            super(itemView);
            cardView4 = (CardView) itemView.findViewById(R.id.cardVieww);
            name = (TextView) itemView.findViewById(R.id.name);
            pluscode = (TextView) itemView.findViewById(R.id.pluscode);
            descrpition = (TextView) itemView.findViewById(R.id.descrpition);
            addreses = (TextView) itemView.findViewById(R.id.addreses);
            namepublic = (TextView) itemView.findViewById(R.id.namepublic);
            address = (TextView) itemView.findViewById(R.id.address);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            sharePrivate = (ImageView) itemView.findViewById(R.id.sharePrivate);
            sharePublic = (ImageView) itemView.findViewById(R.id.sharePublic);
            image = (ImageView) itemView.findViewById(R.id.imagee);
            imagepublic = (ImageView) itemView.findViewById(R.id.imagepublic);
            lnlytPrivate = (LinearLayout)itemView.findViewById(R.id.lnlytSharePrivate);
            lnlytPublic = (LinearLayout)itemView.findViewById(R.id.lnlytSharePublic);
            lnlytPrivateContent = (LinearLayout)itemView.findViewById(R.id.lnlytPrivateContent);
            lnlytPublicContent = (LinearLayout)itemView.findViewById(R.id.lnlytPublicContent);
        }
    }
}


