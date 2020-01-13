package com.codeapex.simplrpost.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Interface.ProfileScreenInterface;
import com.codeapex.simplrpostprod.Activity.PublicLocationDetailNewActivity;
import com.codeapex.simplrpostprod.ModelClass.ModelCategoryBusiness;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaterCategoryBussiness  extends RecyclerView.Adapter<AdaterCategoryBussiness.Holderclass> {

    List<ModelCategoryBusiness> models;
    Context applicationContext;
    ProfileScreenInterface profileInterface;

    public AdaterCategoryBussiness(Context applicationContext, List<ModelCategoryBusiness> models, ProfileScreenInterface interface1) {
        this.applicationContext = applicationContext;
        this.models = models;
        this.profileInterface = interface1;
    }


    @NonNull
    @Override
    public AdaterCategoryBussiness.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_profile_recycler,viewGroup,false);
        AdaterCategoryBussiness.Holderclass holderclass =new AdaterCategoryBussiness.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaterCategoryBussiness.Holderclass viewHolder, int i) {

        /*viewHolder.shortname.setText(models.get(i).getShortName());
        viewHolder.txtvPlusCode.setText(models.get(i).getPlusCode());
        viewHolder.txtvCategory.setText(models.get(i).getCategoryName());

        Picasso.with(applicationContext).load(Constants.IMG_URL.concat(models.get(i).getPictureURL())).placeholder(R.drawable.profile_public).into(viewHolder.imageView);

        viewHolder.busssiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, PublicLocationDetailNewActivity.class);
                intent.putExtra("addressId", models.get(viewHolder.getAdapterPosition()).getAddressId());
                intent.putExtra("isOwn",false);
                intent.putExtra("isFromShared",true);
                  profileInterface.openPublicAddress(intent);


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{

        TextView username,name,shortname,txtvCategory,txtvPlusCode;
        ImageView imageView,imgShare;
        CardView user,busssiness;


        public Holderclass(@NonNull View itemView)
        {
            super(itemView);


            /*shortname = (TextView) itemView.findViewById(R.id.shortname);
            username = (TextView) itemView.findViewById(R.id.username);
            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.imagepublic);
            user = (CardView) itemView.findViewById(R.id.cardViewusere);
            busssiness = (CardView) itemView.findViewById(R.id.cardbussiness);
            imgShare = itemView.findViewById(R.id.imgShare);
            txtvCategory = itemView.findViewById(R.id.category);
            txtvPlusCode = itemView.findViewById(R.id.pluscode);*/
        }
    }
}

