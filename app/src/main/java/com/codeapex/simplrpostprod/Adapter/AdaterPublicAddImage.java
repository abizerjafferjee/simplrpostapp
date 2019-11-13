package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.ImagePreviewActivity;
import com.codeapex.simplrpostprod.ModelClass.Image;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AdaterPublicAddImage extends RecyclerView.Adapter<AdaterPublicAddImage.Holderclass> {


    Context applicationContext;
    List<Image> imagelist;






    public AdaterPublicAddImage(Context applicationContext, List<Image> imagelist) {
        this.applicationContext = applicationContext;
        this.imagelist = imagelist;
    }
//
//    public AdaterPublicAddImage(Context applicationContext) {
//        this.applicationContext = applicationContext;
//    }


    @NonNull
    @Override
    public AdaterPublicAddImage.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridpubliclistt_layout,viewGroup,false);
        AdaterPublicAddImage.Holderclass holderclass =new AdaterPublicAddImage.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterPublicAddImage.Holderclass viewHolder, final int i) {

//        Glide.with(applicationContext).load(Constants.IMG_URL.concat(imagelist.get(i).getImageURL())).into(viewHolder.imageview);
        Picasso.with(applicationContext)
                .load(Constants.IMG_URL.concat(imagelist.get(i).getImageURL()))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.profile_public)
                .into(viewHolder.imageview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {

                    }
                });


        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, ImagePreviewActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("image",Constants.IMG_URL.concat(imagelist.get(i).getImageURL()));
                applicationContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{

        ImageView imageview;

        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            imageview = (ImageView)itemView.findViewById(R.id.imageview);
        }
    }
}







