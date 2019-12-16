package com.codeapex.simplrpostprod.UtilityClass.Api_utils;

import com.codeapex.simplrpostprod.RetrofitApi.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by SOHEl KHAN on 09-Mar-18.
 */

public class ApiClient {

    private static final String BASE_URL = Constants.MAIN_URL;

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

}