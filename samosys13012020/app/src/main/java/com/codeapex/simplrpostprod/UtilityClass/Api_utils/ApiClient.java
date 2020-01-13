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
    private static final String BASE_URL_PLUS_CODE = "https://plus.codes/";
    private static final String BASE_URL_PLACE_ID = "https://maps.googleapis.com/maps/api/place/details/";
    private static final String BASE_URL_PLACE_ID_FROM_LATLNG = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private static final String BASE_URL_LOCATION = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
    private static final String BASE_URL_GEOCODE_ = "https://maps.googleapis.com/maps/api/geocode/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .build();

        return retrofit;
    }

    public static Retrofit getClientPlusCode() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_PLUS_CODE)
                    .client(okHttpClient)
                    .build();

        return retrofit;
    }

    public static Retrofit getClientLocationFromLatLng() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_LOCATION)
                    .client(okHttpClient)
                    .build();

        return retrofit;
    }

    public static Retrofit getClientAddressFromPlaceId() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GEOCODE_)
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static Retrofit getClientPlaceIdFromLatLng() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_PLACE_ID_FROM_LATLNG)
                .client(okHttpClient)
                .build();

        return retrofit;
    }
}