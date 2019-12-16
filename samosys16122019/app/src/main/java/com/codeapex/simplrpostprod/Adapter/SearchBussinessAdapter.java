package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Interface.SearchScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.BussinessModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class SearchBussinessAdapter extends RecyclerView.Adapter<SearchBussinessAdapter.Holderclass> {



    SearchScreenInterface searchInterface;
    List<BussinessModel> lsit;
   Context applicationContext;
   private ArrayList<BussinessModel> mFilteredList;
    String fromScreen;


   public SearchBussinessAdapter(Context applicationContext, List<BussinessModel> lsit, SearchScreenInterface searchInterface, String from) {
       this.lsit = lsit;
       this.applicationContext = applicationContext;
        this.searchInterface = searchInterface;
        this.fromScreen = from;
    }

    @NonNull
    @Override
    public SearchBussinessAdapter.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_profile_recycler, viewGroup, false);
        SearchBussinessAdapter.Holderclass holderclass = new SearchBussinessAdapter.Holderclass(view);


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBussinessAdapter.Holderclass viewHolder, final int i) {

        if (lsit.get(i).getUser() == true) {
            viewHolder.busssiness.setVisibility(View.GONE);
            viewHolder.user.setVisibility(View.VISIBLE);
            viewHolder.username.setText(lsit.get(i).getUserName());
            Log.d(TAG, "onBindViewHolder: "+lsit.get(i).getUser());
            Log.d(TAG, "onBindViewHolder: "+lsit.get(i).getUserName());
            viewHolder.name.setText(lsit.get(i).getName());
//            Glide.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getProfilePicURL())).into(viewHolder.imageView);

            Picasso.with(applicationContext)
                    .load(Constants.IMG_URL.concat(lsit.get(i).getProfilePicURL()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.profile_public)
                    .into(viewHolder.imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });


            if (fromScreen.equals("shareProfile")) {
                viewHolder.imgShare.setVisibility(View.GONE);
                viewHolder.user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchInterface.selectedSearchedAddress(lsit.get(i));
                    }
                });
            }

        } else if(lsit.get(i).getUser() == false){
            viewHolder.user.setVisibility(View.GONE);
            viewHolder.busssiness.setVisibility(View.VISIBLE);
            final String addressIdd = lsit.get(i).getAddressId();
            viewHolder.shortname.setText(lsit.get(i).getShortName());
            viewHolder.txtvPlusCode.setText(lsit.get(i).getPlusCode());
            viewHolder.txtvCategory.setText(lsit.get(i).getCategory());
            Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getProfilePicURL())).placeholder(R.drawable.profile_public).into(viewHolder.imageView);
        }
        viewHolder.busssiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInterface.selectedSearchedAddress(lsit.get(i));
            }
        });

        viewHolder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInterface.selectedSearchedAddress(lsit.get(i));
            }
        });


    }

    @Override
    public int getItemCount() {
        return lsit.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder {
        TextView username,name,shortname,txtvCategory,txtvPlusCode;
        ImageView imageView,imgShare;
        CardView user,busssiness;
//        ;

        public Holderclass(@NonNull View itemView) {

            super(itemView);

            shortname = (TextView) itemView.findViewById(R.id.shortname);
            username = (TextView) itemView.findViewById(R.id.username);
            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.imagepublic);
            user = (CardView) itemView.findViewById(R.id.cardViewusere);
            busssiness = (CardView) itemView.findViewById(R.id.cardbussiness);
            imgShare = itemView.findViewById(R.id.imgShare);
            txtvCategory = itemView.findViewById(R.id.category);
            txtvPlusCode = itemView.findViewById(R.id.pluscode);
        }
    }


//
//
//    public Filter getFilter() {
//
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//
//                String charString = charSequence.toString();
//
//                if (charString.isEmpty()) {
//
//                    mFilteredList = (ArrayList<BussinessModel>) lsit;
//                } else {
//
//                    ArrayList<BussinessModel> filteredList = new ArrayList<>();
//
//                    for (BussinessModel model : lsit) {
//
//                        if (model.getUserName().toLowerCase().contains(charString) || model.getName().toLowerCase().contains(charString)) {
//
//                            filteredList.add(model);
//                        }
//                    }
//
//                    mFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mFilteredList = (ArrayList<BussinessModel>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
}