package com.codeapex.simplrpostprod.UtilityClass;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";


    public static final String PREF_KEY_USERID = "Keyuserid";


    //----------------------------------------------------UserIdModel---------------------------------------------------------
    public static void saveUesrIDData(Context context, SaveUserDetail response) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson myObj = new Gson();
        String json = myObj.toJson(response);
        editor.putString(PREF_KEY_USERID, json);
        editor.apply();
    }


    public static SaveUserDetail getUserIDData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson myObj = new Gson();
        String response = sharedPreferences.getString(PREF_KEY_USERID, "");
        return myObj.fromJson(response, SaveUserDetail.class);
    }


    //----------------------------------------------------Clear Data---------------------------------------------------------
    public static void clearSharedPre(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();


    }

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

}
