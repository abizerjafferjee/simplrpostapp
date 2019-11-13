package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.UserModelReciepent;
import com.codeapex.simplrpostprod.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaterShardlistPrivate  extends RecyclerView.Adapter<AdaterShardlistPrivate.Holder> {
    public static Context context;
    private List<UserModelReciepent> model;
    AlertDialog.Builder alertBox;
    public interface OnItemClickListener {
        void onItemClick(UserModelReciepent item, int position);
    }

    private final OnItemClickListener listener;

    public AdaterShardlistPrivate(Context context, List<UserModelReciepent> model, OnItemClickListener listener) {
        this.model = model;
        this.context = context;
        this.listener = listener;
        alertBox = new AlertDialog.Builder(context);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_sharedpublic_list, viewGroup, false);
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
        TextView txtUserName,txtUserNameuser;
        ImageView imgUserImage;
        CardView crdUnshare;

        public Holder(View v) {
            super(v);

            imgUserImage = v.findViewById(R.id.imgUserImage);
            crdUnshare = v.findViewById(R.id.crdUnshare);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtUserNameuser = v.findViewById(R.id.txtUserNameuser);

        }

        public void bind(final UserModelReciepent userModelReciepent, final OnItemClickListener listener) {

            Picasso.with(context).load(userModelReciepent.getProfilePicURL()).placeholder(R.drawable.profile_private).into(imgUserImage);
            txtUserName.setText(userModelReciepent.getName());
            txtUserNameuser.setText(userModelReciepent.getUserName());
            crdUnshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertBox.setMessage("Are you sure you want to unshare this address?").setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    userModelReciepent.setType("clicked");
                                    listener.onItemClick(userModelReciepent,getAdapterPosition());

//                                    removeItem(getAdapterPosition());
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


    public void removeItem(int position){
        model.remove(position);
        notifyItemRemoved(position);
    }


}

