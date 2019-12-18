package com.codeapex.simplrpostprod.Adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.codeapex.simplrpostprod.Activity.AddSavedListActivity;
import com.codeapex.simplrpostprod.Activity.SharedWithMeActivity;
import com.codeapex.simplrpostprod.Interface.SavedAddressListInterface;
import com.codeapex.simplrpostprod.ModelClass.SavedlistModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AdaterSavedAddrressList extends RecyclerView.Adapter<AdaterSavedAddrressList.Holderclass>{
    Context applicationContext;
    SharedPreferences preferences;
    boolean isSavingProcess;
    SavedAddressListInterface listInterface;

    List<SavedlistModel> models;
    String user_Id;
    Callback<Object> obj;



    public AdaterSavedAddrressList(Context applicationContext, List<SavedlistModel> models, boolean isSavingProcess, SavedAddressListInterface listInterface) {
        this.applicationContext = applicationContext;
        this.models = models;
        this.isSavingProcess = isSavingProcess;
        this.listInterface = listInterface;
    }

    public AdaterSavedAddrressList(Context applicationContext, Callback<Object> objectCallback, List<SavedlistModel> models, boolean isSavingProcess) {
        this.applicationContext = applicationContext;
        this.obj = objectCallback;
        this.models = models;
        this.isSavingProcess = isSavingProcess;
    }


    @NonNull
    @Override
    public AdaterSavedAddrressList.Holderclass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.saved_listcard,viewGroup,false);
        AdaterSavedAddrressList.Holderclass holderclass =new AdaterSavedAddrressList.Holderclass(view);
      preferences = applicationContext.getSharedPreferences("Sesssion", MODE_PRIVATE);
      user_Id = preferences.getString(Constants.userId, "");


        return holderclass;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaterSavedAddrressList.Holderclass viewHolder, final int position) {

        viewHolder.text.setText(models.get(position).getListName());
        if (models.get(position).getIsDefault().equals("1")) {
            viewHolder.imageView.setVisibility(View.GONE);
        }

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSavingProcess ==true){

                    listInterface.listCallBackMethod(models.get(viewHolder.getLayoutPosition()).getListId());




                }
                else {
                    Intent intent = new Intent(applicationContext, SharedWithMeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("listIddd", models.get(viewHolder.getAdapterPosition()).getListId());
                 intent.putExtra("listname", models.get(viewHolder.getAdapterPosition()).getListName());
                 intent.putExtra("shareType", "dd");
                    intent.putExtra("isShared",false);

                applicationContext.startActivity(intent);
                }

            }
        });


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Context wrapper = new ContextThemeWrapper(applicationContext, R.style.MyPopupOtherStyle);


                PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);

                final SavedlistModel selectedObject = models.get(viewHolder.getAdapterPosition());



                /*  The below code in try catch is responsible to display icons*/

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());

                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //inflate menu
                popup.getMenuInflater().inflate(R.menu.menunew, popup.getMenu());


                //show menu
                popup.show();





                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//
                        if (item.getTitle().toString().equals("Edit")){
                            Intent intent = new Intent(applicationContext, AddSavedListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                            intent.putExtra("listId", selectedObject.getListId());
                            intent.putExtra("listname", selectedObject.getListName());
                            intent.putExtra("toolbar_heading", "Edit List");


                            applicationContext.startActivity(intent);
                            //Toast.makeText(applicationContext, item.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                        else  {
                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(v.getRootView().getContext());
                            builder.setMessage("Are you sure you want to delete ?");
                            builder.setCancelable(false);
                            builder
                                    .setPositiveButton(
                                            "Yes",
                                            new DialogInterface
                                                    .OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {

                                                    deleteSavedAddressList(models.get(viewHolder.getAdapterPosition()).getListId(),viewHolder.getAdapterPosition());
                                                }
                                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }





                        return true;
                    }
                });






            }

        });

    }

        /*private void showPopupMenu(final View anchor, boolean isWithIcons, int style) {

            Context wrapper = new ContextThemeWrapper(applicationContext, R.style.MyPopupOtherStyle);


            PopupMenu popup = new PopupMenu(wrapper, anchor, Gravity.END);





            *//*  The below code in try catch is responsible to display icons*//*

            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());

                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //inflate menu
            popup.getMenuInflater().inflate(R.menu.menunew, popup.getMenu());


            //show menu
            popup.show();





            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
//
                    if (item.getTitle().toString().equals("Edit")){
                        Intent intent = new Intent(applicationContext, AddSavedListActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                        intent.putExtra("listId", listId);
                        intent.putExtra("listname", listname);
                        intent.putExtra("toolbar_heading", "Edit List");


                       applicationContext.startActivity(intent);
                        //Toast.makeText(applicationContext, item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    else  {
                        AlertDialog.Builder builder
                                = new AlertDialog
                                .Builder(anchor.getRootView().getContext());
                        builder.setMessage("Are you sure you want to delete ?");
                        builder.setCancelable(false);
                        builder
                                .setPositiveButton(
                                        "Yes",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {

                                                deleteSavedAddressList();
                                            }
                                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }





                    return true;
                }
            });

        }*/
    @Override
    public int getItemCount() {
        return models.size();
    }

    public class Holderclass extends RecyclerView.ViewHolder{
        TextView text;
        ImageView imageView;
        CardView cardView;
        public Holderclass(@NonNull View itemView)
        {

            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textViewsave);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
        }

    }


//
    public void deleteSavedAddressList(String listID, final int posi) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Constants.userId,user_Id);
        hashMap.put(Constants.listId,listID);

        Call<Object> call = RetrofitInstance.getdata().create(Api.class).deleteSavedAddressList(hashMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(ContentValues.TAG, "onResponse: "+response);


                try {

                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    String result_code = jsonObject.getString(Constants.RESULT_CODE);
                    if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                        Log.e("TAG---", "Data get>>>sve\n: " + new Gson().toJson(response.body()));
                        String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                        ((SavedAddressListActivity)applicationContext).getSavedList();

                        removeItem(posi);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(ContentValues.TAG, "onFailure: "+t);



            }
        });



    }

    public void removeItem(int position){
        models.remove(position);
        notifyItemRemoved(position);
    }
}

