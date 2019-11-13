package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.ImagePreviewActivity;
import com.codeapex.simplrpostprod.ModelClass.Service;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AdaterPublicAddServices extends RecyclerView.Adapter<AdaterPublicAddServices.Holderclass>  {
    Context applicationContext;
    List<Service> servicelist;




    public AdaterPublicAddServices(Context applicationContext, List<Service> servicelist) {
        this.applicationContext = applicationContext;
        this.servicelist = servicelist;
    }


    @NonNull
    @Override
    public AdaterPublicAddServices.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gridaddlist_layout,viewGroup,false);
        AdaterPublicAddServices.Holderclass holderclass =new AdaterPublicAddServices.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterPublicAddServices.Holderclass viewHolder, final int i) {


        String[] arr = servicelist.get(i).getServiceFileURL().toString().split("\\.");
        if ((arr[arr.length - 1].equals("jpg"))||(arr[arr.length - 1].equals("jpeg"))||arr[arr.length - 1].equals("png")) {
//            Glide.with(applicationContext).load(Constants.IMG_URL.concat(servicelist.get(i).getServiceFileURL())).into(viewHolder.imageview);

            Picasso.with(applicationContext)
                    .load(Constants.IMG_URL.concat(servicelist.get(i).getServiceFileURL()))
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

        }
        else if (arr[arr.length - 1].equals("pdf")) {
            viewHolder.imageview.setImageResource(R.drawable.pdf);
        }
        else {
            viewHolder.imageview.setImageResource(R.drawable.docx_file_format_symbol);
        }


        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] arr = servicelist.get(i).getServiceFileURL().toString().split("\\.");
                if ((arr[arr.length - 1].equals("jpg"))||(arr[arr.length - 1].equals("jpeg"))||arr[arr.length - 1].equals("png")) {
                    Intent intent = new Intent(applicationContext, ImagePreviewActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("image",Constants.IMG_URL.concat(servicelist.get(i).getServiceFileURL()));
                    applicationContext.startActivity(intent);
                }
                else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://docs.google.com/gview?embedded=true&url="+Constants.IMG_URL.concat(servicelist.get(i).getServiceFileURL())));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    applicationContext.startActivity(browserIntent);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return servicelist.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imageview;

        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            imageview = (ImageView)itemView.findViewById(R.id.imageview);
        }
    }
}



