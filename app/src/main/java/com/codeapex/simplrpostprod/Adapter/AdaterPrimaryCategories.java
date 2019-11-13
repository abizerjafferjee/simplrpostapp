package com.codeapex.simplrpostprod.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.CategoryBussinessActivity;
import com.codeapex.simplrpostprod.ModelClass.RecentAdded;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdaterPrimaryCategories extends RecyclerView.Adapter<AdaterPrimaryCategories.Holderclass> {
    FragmentActivity activity;
    List<RecentAdded> list;



    public AdaterPrimaryCategories(FragmentActivity activity, List<RecentAdded> list) {
        this.activity = activity;
        this.list = list;
    }


    @NonNull
    @Override
    public AdaterPrimaryCategories.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_explore,viewGroup,false);
        AdaterPrimaryCategories.Holderclass holderclass =new AdaterPrimaryCategories.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaterPrimaryCategories.Holderclass viewHolder, int i) {

      ;

        viewHolder.category.setText(list.get(i).getCategoryName());


        Picasso.with(activity).load(Constants.IMG_URL.concat(list.get(i).getIconURL())).placeholder(R.drawable.category_default_icon).into(viewHolder.image);
//



        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String categoryId = list.get(viewHolder.getLayoutPosition()).getCategoryId();

                Intent intent = new Intent(activity, CategoryBussinessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("categoryId_explore", categoryId);
                intent.putExtra("categoryName", list.get(viewHolder.getLayoutPosition()).getCategoryName());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView shortname,category;
        ImageView image;

        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linear);

            category = (TextView) itemView.findViewById(R.id.gridListView_title);
            image = (ImageView) itemView.findViewById(R.id.gridListImageView);


        }
    }
}

