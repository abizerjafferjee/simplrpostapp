package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_Share;
import com.codeapex.simplrpostprod.Activity.ImagePreviewActivity;
import com.codeapex.simplrpostprod.ModelClass.addressGridImage;
import com.codeapex.simplrpostprod.ModelClass.shareItemsModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddressImageGridAdapter extends RecyclerView.Adapter<AddressImageGridAdapter.ViewHolder> {

    Context context;
    private List<addressGridImage> originalData = null;


    public AddressImageGridAdapter(Context context, List<addressGridImage> arrayList) {
        this.originalData = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_image_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final addressGridImage shareItemsModel = originalData.get(position);
        holder.street_img_type.setText(shareItemsModel.txt);


        Picasso.with(context)
                .load(Constants.IMG_URL + shareItemsModel.img.replace("uploads/", ""))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.img_street, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.img_street.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!shareItemsModel.img.contains("address_default_image.png")) {
                                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                                    intent.putExtra("image", Constants.IMG_URL + shareItemsModel.img.replace("uploads/", ""));
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        holder.img_street.setImageResource(R.drawable.image_placeholder);
                    }
                });

        holder.img_street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra("image", Constants.IMG_URL + shareItemsModel.img.replace("uploads/",""));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return originalData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView street_img_type;
        private ImageView img_street;

        public ViewHolder(View itemView) {
            super(itemView);
            street_img_type = itemView.findViewById(R.id.street_img_type);
            img_street = itemView.findViewById(R.id.img_street);
        }
    }

}
