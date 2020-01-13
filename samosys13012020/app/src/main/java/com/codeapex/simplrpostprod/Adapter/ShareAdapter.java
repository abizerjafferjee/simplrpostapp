package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Activity.Activity_Share;
import com.codeapex.simplrpostprod.ModelClass.ShareModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements Filterable {

    Context context;
    private List<ShareModel> originalData = null;
    private List<ShareModel> filteredData = null;
    SharedPreferences preferences;
    Boolean shareDirect = false;

    public ShareAdapter(Context context, List<ShareModel> arrayList) {
        this.originalData = arrayList;
        this.filteredData = arrayList;
        this.context = context;
        preferences = context.getSharedPreferences("Sesssion", MODE_PRIVATE);
        shareDirect = Activity_Share.shareDirect;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ShareModel shareModel = filteredData.get(position);
        holder.txt_userName.setText(shareModel.getUserName());
        holder.txt_mobileNumber.setText(shareModel.getMobileNumber());
        if (shareModel.getUserImage() != null && !shareModel.getUserImage().equals("")) {
            // Picasso.with(context).load(Constants.IMG_URL+shareModel.getUserImage()).into(holder.img_profile);

            Picasso.with(context)
                    .load(Constants.IMG_URL + shareModel.getUserImage())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.profileplchlder)
                    .into(holder.img_profile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = filteredData.get(position).getUserId();
                if (Activity_Share.public_or_private.equals("public")) {
                    shareAddress(userId, Activity_Share.address_id, Activity_Share.public_or_private, true);
                }else {
                    shareAddress(userId, Activity_Share.address_id, Activity_Share.public_or_private, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
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

        private TextView txt_userName, txt_mobileNumber, btn_share;
        private CircleImageView img_profile;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_userName = itemView.findViewById(R.id.txt_userName);
            txt_mobileNumber = itemView.findViewById(R.id.txt_mobileNumber);
            img_profile = itemView.findViewById(R.id.img_profile);
            btn_share = itemView.findViewById(R.id.btn_share);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredData = originalData;
                } else {
                    List<ShareModel> filteredList = new ArrayList<>();
                    for (ShareModel row : originalData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUserId().contains(charSequence) || row.getUserImage().contains(charSequence) || row.getUserName().toLowerCase().contains(charString.toLowerCase()) || row.getMobileNumber().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filteredData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<ShareModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public void shareAddress(String receiverId,String addressID, String isPublic, Boolean isWithUser) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
        hashMap.put(Constants.receiverId, receiverId); //searched user or public address
        hashMap.put(Constants.addressId, addressID); //address to share
        hashMap.put(Constants.isAddressPublic, isPublic);
        Log.d(TAG, "saveAddressToSavedList: " + receiverId  + "  " + addressID + "  " + isPublic);

        Call<Object> call;
        if (isWithUser)
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithUser(hashMap);
        else
            call = RetrofitInstance.getdata().create(Api.class).shareAddressWithBusiness(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.e("share_Response", new Gson().toJson(response.body()));

                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.ZERO)) {

                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_EIGHT)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                    } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                    } else if (result_code.equals("-11.0")) {
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                        alertbox.setMessage("This address is already shared.");
                        alertbox.setTitle("");
                        alertbox.setCancelable(false);
                        alertbox.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.dismiss();

                                    }
                                });
                        alertbox.show();
                    } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                        fill.setImageResource(R.drawable.hearticon);
//                        new UtilityClass().simpleAlertBox(SearchBusinessFragment.this,"Your address shared successfully.");
                        AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
                        alertbox.setMessage("Your address shared successfully.");
                        alertbox.setTitle("");
                        alertbox.setCancelable(false);
                        alertbox.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        arg0.dismiss();
                                    }
                                });
                        alertbox.show();

//                        Toast.makeText(SearchBusinessFragment.this, "Your address shared successfully.", Toast.LENGTH_SHORT).show();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);

            }
        });


    }

}
