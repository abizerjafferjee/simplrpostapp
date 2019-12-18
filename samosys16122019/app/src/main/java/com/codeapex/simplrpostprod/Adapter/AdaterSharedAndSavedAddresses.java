package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeapex.simplrpostprod.Activity.PrivateLocationDetailNewActivity;
import com.codeapex.simplrpostprod.Interface.SaveUnsaveAddress;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;

public class AdaterSharedAndSavedAddresses extends RecyclerView.Adapter<AdaterSharedAndSavedAddresses.Holderclass> {

    Context applicationContext;
    List<PrivateAddress> privatelist;
    SaveUnsaveAddress saveInterface;
    String listId,user_Id;
    boolean removeHeart;

    public AdaterSharedAndSavedAddresses(Context applicationContext, List<PrivateAddress> privatelist, SaveUnsaveAddress saveInterface, String listId,
                                         boolean removeHeart) {
        this.applicationContext = applicationContext;
        this.privatelist = privatelist;
        this.saveInterface = saveInterface;
        this.listId = listId;
        this.removeHeart = removeHeart;

    }

    @NonNull
    @Override
    public Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.private_addresses,viewGroup,false);
        Holderclass holderclass =new Holderclass(view);
        SharedPreferences preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
        user_Id = preferences.getString(Constants.userId, "");

        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holderclass viewHolder, int i) {



        final String addressIdd = privatelist.get(viewHolder.getLayoutPosition()).getAddressId();
        viewHolder.pluscode.setText(privatelist.get(i).getPlusCode());
        viewHolder.namepublic.setText(privatelist.get(i).getShortName());
//        Glide.with(applicationContext).load(Constants.IMG_URL.concat(privatelist.get(i).getPictureURL())).into(viewHolder.image);

        Picasso.with(applicationContext)
                .load(Constants.IMG_URL.concat(privatelist.get(i).getPictureURL()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.profile_private)
                .into(viewHolder.image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {

                    }
                });


        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, PrivateLocationDetailNewActivity.class);
                intent.putExtra("addressId",privatelist.get(viewHolder.getAdapterPosition()).getAddressId());
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",removeHeart);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }
        });

        if(removeHeart==true){
            viewHolder.cardView4.setVisibility(View.GONE);
        }else {
            viewHolder.cardView4.setVisibility(View.VISIBLE);
        }

        viewHolder.cardView4.setOnClickListener(new View.OnClickListener() {
            Boolean isSave;
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();


                final Bitmap bmap = ((BitmapDrawable)viewHolder.imageheart.getDrawable()).getBitmap();
                Drawable myDrawable = applicationContext.getResources().getDrawable(R.drawable.hearticon);
                final Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                if (bmap.sameAs(myLogo)) {
                    isSave = false;
                    viewHolder.imageheart.setImageResource(R.drawable.like);
                } else {
                    isSave = true;
                    viewHolder.imageheart.setImageResource(R.drawable.hearticon);
                }
                hashMap.put(Constants.userId, user_Id);
                hashMap.put(Constants.listId, listId);
                hashMap.put(Constants.addressId, addressIdd);
                hashMap.put(Constants.isPublic,"0");
                saveInterface.saveUnsaveAddress(hashMap,isSave);

                Log.d(TAG, "omcode: "+hashMap);


            }

        });
    }

    @Override
    public int getItemCount() {
        return privatelist.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        CardView cardView,cardView4;
        TextView name , address,namepublic,pluscode,addreses,descrpition;
        ImageView imageView,image,imagepublic,imageheart;

        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            cardView4 = (CardView) itemView.findViewById(R.id.cardView4);
            namepublic = (TextView) itemView.findViewById(R.id.name);
            pluscode = (TextView) itemView.findViewById(R.id.pluscode);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageheart = (ImageView) itemView.findViewById(R.id.fill);
            image = (ImageView) itemView.findViewById(R.id.imagee);

        }
    }
}
