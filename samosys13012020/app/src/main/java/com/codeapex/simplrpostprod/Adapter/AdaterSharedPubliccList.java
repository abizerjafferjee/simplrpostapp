package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.publicModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaterSharedPubliccList extends RecyclerView.Adapter<AdaterSharedPubliccList.Holder> {
    public static Context context;
    private List<publicModel> model;
    AlertDialog.Builder alertBox;

    public interface OnItemClickListener {
        void onItemClick(publicModel item, int position);
    }

    private final OnItemClickListener listener;

    public AdaterSharedPubliccList(Context context, List<publicModel> model, OnItemClickListener listener) {
        this.model = model;
        this.context = context;
        this.listener = listener;
        alertBox = new AlertDialog.Builder(context);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_sharedprivate_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int i) {
        holder.bind(model.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView txtAddressName, txtAddress, txtPlusCode;
        CardView crdUnshare;
        CircleImageView imgAddressImage;


        public Holder(View v) {
            super(v);

            imgAddressImage = v.findViewById(R.id.imagepublic);
            crdUnshare = v.findViewById(R.id.crdUnshare);
            txtAddressName = v.findViewById(R.id.namepublic);
            txtAddress = v.findViewById(R.id.addreses);
            txtPlusCode = v.findViewById(R.id.pluscode);

        }

        public void bind(final publicModel publicModelw, final OnItemClickListener listener) {
            Log.e("image", "dataaa" + publicModelw.getPictureURL());

            Picasso.with(context)
                    .load(Constants.IMG_URL + publicModelw.getPictureURL().replace("uploads/", ""))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imgAddressImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            imgAddressImage.setImageResource(R.drawable.image_placeholder);
                        }
                    });

            String url = publicModelw.getShortName();
            URI uri = null;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String[] segments = uri.getPath().split("/");
            String idStr = segments[segments.length - 1];
            String prefix = idStr.replace("address-", "");

            txtAddressName.setText(prefix);
            txtPlusCode.setText(publicModelw.getPlusCode());
            txtAddress.setText(publicModelw.getCategoryName());

            crdUnshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertBox.setMessage("Are you sure you want to unshare this address?").setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    publicModelw.setType("clicked");
                                    listener.onItemClick(publicModelw, getAdapterPosition());
                                    //removeItem(getAdapterPosition());
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = alertBox.create();
                    alert.setCancelable(false);
                    alert.setTitle("Simplr Post");
                    alert.show();
                }
            });

        }
    }


    public void removeItem(int position) {
        model.remove(position);
        notifyItemRemoved(position);
    }

}


