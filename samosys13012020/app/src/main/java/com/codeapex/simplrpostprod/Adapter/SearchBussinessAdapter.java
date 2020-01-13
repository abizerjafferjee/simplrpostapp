package com.codeapex.simplrpostprod.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_Share;
import com.codeapex.simplrpostprod.Activity.Activity_ViewPublicAddress;
import com.codeapex.simplrpostprod.Interface.SearchScreenInterface;
import com.codeapex.simplrpostprod.ModelClass.ModelResultPrivateAddress;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.squareup.picasso.Picasso;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SearchBussinessAdapter extends RecyclerView.Adapter<SearchBussinessAdapter.Holderclass> {

    SearchScreenInterface searchInterface;
    List<ModelResultPrivateAddress> lsit;
    Context applicationContext;
    private ArrayList<ModelResultPrivateAddress> mFilteredList;
    String fromScreen,is_owner="false";
    SharedPreferences sharedPreferences;


    public SearchBussinessAdapter(Context applicationContext, List<ModelResultPrivateAddress> lsit, SearchScreenInterface searchInterface, String from) {
        this.lsit = lsit;
        this.applicationContext = applicationContext;
        this.searchInterface = searchInterface;
        this.fromScreen = from;

        sharedPreferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);

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

        String url = lsit.get(i).getUnique_link();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String[] segments = uri.getPath().split("/");
        String idStr = segments[segments.length - 1];
        String prefix = idStr.replace("address-", "");

        viewHolder.uniqueLink.setText(prefix);
        viewHolder.address.setText(lsit.get(i).getCountry());
        viewHolder.address.setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.tag.setText(lsit.get(i).getAddress_tag());
        viewHolder.pluscode.setText(lsit.get(i).getPlus_code());

        if (lsit.get(i).getStreet_image() != null && !lsit.get(i).getStreet_image().equals("") && !lsit.get(i).getStreet_image().contains("address_default_image.png")) {
            if (lsit.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getStreet_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            }
        }
        else if (lsit.get(i).getBuilding_image() != null && !lsit.get(i).getBuilding_image().equals("") && !lsit.get(i).getBuilding_image().contains("address_default_image.png")) {
            if (lsit.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getBuilding_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            }
        }
        else if (lsit.get(i).getEntrance_image() != null && !lsit.get(i).getEntrance_image().equals("") && !lsit.get(i).getEntrance_image().contains("address_default_image.png")) {
            if (lsit.get(i).getAddress_tag().equals("public")) {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            } else {
                Picasso.with(applicationContext).load(Constants.IMG_URL.concat(lsit.get(i).getEntrance_image().replace("uploads/", ""))).placeholder(R.drawable.image_placeholder).into(viewHolder.imageView);
            }
        }

        if (lsit.get(i).getAddress_tag().equals("public")) {
            viewHolder.sharePublic.setVisibility(View.VISIBLE);

        } else {
            if (lsit.get(i).getUserId().equals(sharedPreferences.getString(Constants.userId,""))) {
                viewHolder.sharePublic.setVisibility(View.VISIBLE);
            }else {
                viewHolder.sharePublic.setVisibility(View.GONE);
            }
        }

        viewHolder.sharePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferences.getString(Constants.userId, "").equals(lsit.get(i).getUserId())) {
                    is_owner = "true";
                }else {
                    is_owner = "false";

                }
                if (is_owner.equals("true")) {
                    applicationContext.startActivity(new Intent(applicationContext, Activity_Share.class)
                            .putExtra("is_owner", "true")
                            .putExtra("is_private", lsit.get(i).getAddress_tag())
                            .putExtra("shareDirect", false)
                            .putExtra("unique_link", lsit.get(i).getUnique_link())
                            .putExtra("address_id", lsit.get(i).getAddressId()));
                } else {
                    applicationContext.startActivity(new Intent(applicationContext, Activity_Share.class).putExtra("is_owner", "false")
                            .putExtra("is_private", lsit.get(i).getAddress_tag())
                            .putExtra("shareDirect",false)
                            .putExtra("is_owner", "false")
                            .putExtra("unique_link", lsit.get(i).getUnique_link())
                            .putExtra("address_id", lsit.get(i).getAddressId()));
                }
            }
        });

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationContext.startActivity(new Intent(applicationContext, Activity_ViewPublicAddress.class)
                        .putExtra("address_id", lsit.get(i).getAddressId())
                        .putExtra("user_id", lsit.get(i).getUserId())
                        .putExtra("profile_img", lsit.get(i).getUserImage())
                        .putExtra("user_name", lsit.get(i).getUserName())
                        .putExtra("plus_code", lsit.get(i).getPlus_code())
                        .putExtra("public_private_tag", lsit.get(i).getAddress_tag())
                        .putExtra("qr_code_img", lsit.get(i).getQrCode_image())
                        .putExtra("street_img", lsit.get(i).getStreet_image())
                        .putExtra("building_img", lsit.get(i).getBuilding_image())
                        .putExtra("entrance_img", lsit.get(i).getEntrance_image())
                        .putExtra("address_unique_link", lsit.get(i).getUnique_link())
                        .putExtra("country", lsit.get(i).getCountry())
                        .putExtra("city", lsit.get(i).getCity())
                        .putExtra("street", lsit.get(i).getStreet_name())
                        .putExtra("building", lsit.get(i).getBuilding_name())
                        .putExtra("entrance", lsit.get(i).getEntrance_name())
                        .putExtra("latitude", String.valueOf(lsit.get(i).getLatitude()))
                        .putExtra("longitude", String.valueOf(lsit.get(i).getLongitude()))
                        .putExtra("street_img_type", lsit.get(i).getStreet_img_type())
                        .putExtra("building_img_type", lsit.get(i).getBuilding_img_type())
                        .putExtra("entrance_img_type", lsit.get(i).getEntrance_img_type())
                        .putExtra("from", "search")
                        .putExtra("direction_txt", lsit.get(i).getDirection_text()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lsit.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder {
        TextView sharePublic, tag, uniqueLink, address, pluscode;
        ImageView imageView;
        RelativeLayout cardView;

        public Holderclass(@NonNull View itemView) {

            super(itemView);

            uniqueLink = itemView.findViewById(R.id.uniqueLink);
            sharePublic = itemView.findViewById(R.id.sharePublic);
            tag = itemView.findViewById(R.id.tag);
            imageView = itemView.findViewById(R.id.imagepublic);
            cardView = itemView.findViewById(R.id.cardView4);
            address = itemView.findViewById(R.id.address);
            pluscode = itemView.findViewById(R.id.pluscode);
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