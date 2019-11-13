package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.codeapex.simplrpostprod.Activity.PublicLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.PublicAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdatperSharedWithMe extends RecyclerView.Adapter<AdatperSharedWithMe.Holderclass>{


    Context applicationContext;
    List<PublicAddress> publiclist;
    SaveUnsaveAddress saveInterface;
    String listId,user_Id;
    boolean removeHeart;


    public AdatperSharedWithMe(Context applicationContext, List<PublicAddress> publiclist, SaveUnsaveAddress saveInterface, String listId, boolean removeHeart) {
        this.applicationContext = applicationContext;
        this.publiclist = publiclist;
        this.saveInterface = saveInterface;
        this.listId = listId;
        this.removeHeart = removeHeart;

    }

    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_friend_recycler,viewGroup,false);
        Holderclass holderclass =new Holderclass(view);
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");

        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdatperSharedWithMe.Holderclass viewHolder, int i) {
        final String addressIdd = publiclist.get(viewHolder.getLayoutPosition()).getAddressId();
        viewHolder.pluscode.setText(publiclist.get(i).getPlusCode());
        viewHolder.namepublic.setText(publiclist.get(i).getShortName());
        viewHolder.descrpition.setText(publiclist.get(i).getDescription());
//        Glide.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getPictureURL())).into(viewHolder.image);


        Picasso.with(applicationContext).load(Constants.IMG_URL.concat(publiclist.get(i).getPictureURL())).placeholder(R.drawable.profile_public).into(viewHolder.image);
//        viewHolder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(applicationContext, ImagePreviewActivity.class);
//                intent.putExtra("image",Constants.IMG_URL.concat(publiclist.get(viewHolder.getAdapterPosition()).getPictureURL()));
//                applicationContext.startActivity(intent);
//            }
//        });



        if(removeHeart==true){
            viewHolder.cardView4.setVisibility(View.GONE);
        }else {
            viewHolder.cardView4.setVisibility(View.VISIBLE);
        }

        viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
            Boolean isSave;
            @Override
            public void onClick(View v) {

                HashMap<String, String> data = new HashMap<>();

                final Bitmap bmap = ((BitmapDrawable)viewHolder.fill.getDrawable()).getBitmap();
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
                data.put(Constants.isPublic,"1");
                saveInterface.saveUnsaveAddress(data,isSave);

            }

        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String addressId = publiclist.get(viewHolder.getLayoutPosition()).getAddressId();

                Intent intent = new Intent(applicationContext, PublicLocationDetailNewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("addressId", addressId);
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",removeHeart);
                intent.putExtra("listID",listId);

                applicationContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return publiclist.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        CardView cardView,cardView4;
        TextView name , address,namepublic,pluscode,addreses,descrpition;
        ImageView imageView,image,imagepublic,fill;

        public Holderclass(@NonNull View itemView)
        {
            super(itemView);
            cardView4 =itemView.findViewById(R.id.cardView4);
            cardView =itemView.findViewById(R.id.cardView);
            namepublic =  itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.imagee);
            fill = (ImageView) itemView.findViewById(R.id.fillICON);
            descrpition = (TextView) itemView.findViewById(R.id.descrpition);
            pluscode = (TextView) itemView.findViewById(R.id.pluscode);

        }
    }
}

