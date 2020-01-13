package com.codeapex.simplrpostprod.UtilityClass.Api_utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Parser {


    public static ACProgressFlower progressbar;

    public static void callApi(final Context context, String progressMessage, Boolean is_progress, Call<ResponseBody> call, final Response_Call_Back response_call_back){

        if (is_progress) {
            progressbar = initProgressDialog(context,"");
        }

        call.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("URL",""+call.request().url());
                if(response.isSuccessful()) {
                    try {
                        //Log.e("response...:","succrss"+response.body().string());
                        response_call_back.getResponseFromServer(response.body().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    try {

                        Log.e("retro error message...:",""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (progressbar!=null) {
                    progressbar.dismiss();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressbar!=null) {
                    progressbar.dismiss();
                }
            }
        });

    }

    public static ACProgressFlower initProgressDialog(Context c,String text) {
        ACProgressFlower dialog = new ACProgressFlower.Builder(c)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(text).build();
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

}
