package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codeapex.simplrpostprod.Adapter.ShareAdapter;
import com.codeapex.simplrpostprod.Adapter.ShareExternalAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ShareModel;
import com.codeapex.simplrpostprod.ModelClass.shareItemsModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Share extends Activity {

    private ShareAdapter shareAdapter;
    private ShareExternalAdapter shareExternalAdapter;
    private ArrayList<ShareModel> shareModelArrayList = new ArrayList<>();
    private RecyclerView recyclerView,recyclerView_shareExternal;
    private EditText edt_search;
    private List<shareItemsModel> appNames;
    private String is_owner;
    private ImageButton btn_back;
    private RelativeLayout layout_internalSharing;
    private LinearLayout layout_main,layout_externalSharing;
    private ProgressBar progressBar;
    public static String public_or_private,address_id,unique_link;
    public static Boolean shareDirect = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_address);

        sharedPreferences = getSharedPreferences("Sesssion", Context.MODE_PRIVATE);
        is_owner = this.getIntent().getStringExtra("is_owner");
        public_or_private = this.getIntent().getStringExtra("is_private");
        address_id = this.getIntent().getStringExtra("address_id");
        unique_link = this.getIntent().getStringExtra("unique_link");

        Log.e("unique_link","unique_link"+unique_link);
        findViews();
        getExternalApps();

    }

    private void findViews() {

        recyclerView = findViewById(R.id.recyclerView_share);
        recyclerView_shareExternal = findViewById(R.id.recyclerView_shareExternal);
        edt_search = findViewById(R.id.edt_search);
        layout_internalSharing = findViewById(R.id.layout_internalSharing);
        btn_back = findViewById(R.id.btn_back);
        layout_main = findViewById(R.id.layout_main);
        progressBar = findViewById(R.id.progressBar);
        layout_externalSharing = findViewById(R.id.layout_externalSharing);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length()>0) {
                    shareAdapter.getFilter().filter(s.toString());
                    recyclerView.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0) {
                    shareAdapter.getFilter().filter(s.toString());
                }else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        if (public_or_private.equals("Private")||public_or_private.equals("private")) {
            getAppUser();
            layout_internalSharing.setVisibility(View.VISIBLE);
            layout_externalSharing.setVisibility(View.VISIBLE);
        }else {
            layout_internalSharing.setVisibility(View.GONE);
            layout_externalSharing.setVisibility(View.VISIBLE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getExternalApps() {
        String urlToShare = "https://play.google.com/store/apps/details?id=com.yourapp.packagename";
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "If any extra"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, "Let me recommend you this application \n\n" + urlToShare);

        final List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);
        appNames = new ArrayList<shareItemsModel>();

        for (ResolveInfo info : activities) {
            if (info.activityInfo.packageName.contains("email") ||
                    info.activityInfo.packageName.contains("bluetooth") ||
                    info.activityInfo.packageName.contains("whatsapp") ||
                    info.activityInfo.packageName.contains("facebook") ||
                    info.activityInfo.packageName.contains("messenger") ||
                    info.activityInfo.packageName.contains("line") ||
                    info.activityInfo.packageName.contains("telegram") ||
                    info.activityInfo.packageName.contains("viber") ||
                    info.activityInfo.packageName.contains("hangout") ||
                    info.activityInfo.packageName.contains("mms") ||
                    info.activityInfo.packageName.contains("wechat") ||
                    info.activityInfo.packageName.contains("skype") ||
                    info.activityInfo.packageName.contains("messaging") ||
                    info.activityInfo.packageName.contains("instagram") ||
                    info.activityInfo.packageName.contains("snapchat") ||
                    info.activityInfo.packageName.contains("signal") ||
                    info.activityInfo.packageName.contains("android.gm")) {
                appNames.add(new shareItemsModel(info.loadLabel(getPackageManager()).toString(),
                        info.loadIcon(getPackageManager()), info.activityInfo.packageName));
            }
            Log.e("AppNames",""+info.activityInfo.packageName);
        }

        GridLayoutManager linearLayoutManager_external = new GridLayoutManager(Activity_Share.this,3);
        recyclerView_shareExternal.setLayoutManager(linearLayoutManager_external);
        shareExternalAdapter = new ShareExternalAdapter(Activity_Share.this,appNames);
        recyclerView_shareExternal.setAdapter(shareExternalAdapter);

    }

    public void getAppUser(){
        Parser.callApi(Activity_Share.this, "Please wait...", true, ApiClient.getClient().create(ApiInterface.class).getAppUser(), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("App User ", "response::" + response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("resultCode");

                    if (result_code.equals("1")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("resultData");
                        String user_id = sharedPreferences.getString(Constants.userId,"");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.optJSONObject(i);
                            String name = object.optString("name");
                            String userId = object.optString("userId");
                            if (name.equals("null")||name==null){
                                name = "";
                            }
                            String phone = object.optString("contactNumber");
                            if (phone.equals("null")||name==null){
                                phone = "";
                            }
                            if (!name.equals("null")&&!name.equals("")&& !object.getString("contactNumber").equals("null")&&!object.getString("contactNumber").equals("")){

                                if (!user_id.equals(userId)) {
                                    shareModelArrayList.add(new ShareModel(object.optString("userId"), object.optString("profilePicURL"), name, phone));
                                }
                            }
                        }

                    } else if (result_code.equals("0")) {
                        new Message().showSnack(layout_main, "" + jsonObject.optString("data"));
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Activity_Share.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    shareAdapter = new ShareAdapter(Activity_Share.this,shareModelArrayList);
                    recyclerView.setAdapter(shareAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
