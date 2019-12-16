package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.ModelClass.Category;
import com.codeapex.simplrpostprod.R;

import java.util.List;

public class AdaterCategory extends RecyclerView.Adapter<AdaterCategory.Holderclass> {
    Context applicationContext;
    List<Category> list;
    CategorySelected categorySelected;


    public AdaterCategory(Context applicationContext, List<Category> list,CategorySelected categorySelected) {
        this.applicationContext = applicationContext;
        this.list = list;
        this.categorySelected=categorySelected;
    }

    @NonNull
    @Override
    public AdaterCategory.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_card, viewGroup, false);
        AdaterCategory.Holderclass holderclass = new AdaterCategory.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaterCategory.Holderclass viewHolder, final int i) {

        final String category_id = list.get(i).getCategoryId();
        viewHolder.category.setText(list.get(i).getCategoryName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorySelected.onCategorySelection(list.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder {
        TextView category;

        public Holderclass(@NonNull View itemView) {

            super(itemView);
            category = (TextView) itemView.findViewById(R.id.category);


        }
    }

    public interface CategorySelected {
        void onCategorySelection(Category category);
    }
}
