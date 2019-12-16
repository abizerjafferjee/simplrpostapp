package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codeapex.simplrpostprod.Adapter.IssueAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.IssueModel;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueListActivity extends AppCompatActivity {

    ImageView back_press;
    RecyclerView recyclerView;
    ConstraintLayout root_lay;
    private List<IssueModel> issueModel = new ArrayList<>();
    LinearLayoutManager manager;
    IssueAdapter adapter;
    ProgressBar Loaderr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        findIDs();
    }

//===========================FIND VIEW IDS==============================//

    public void findIDs(){
        back_press = findViewById(R.id.back_press);
        Loaderr = findViewById(R.id.Loaderr);
        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IssueListActivity.super.onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        root_lay = findViewById(R.id.root_lay);

        manager = new LinearLayoutManager(this);
        adapter = new IssueAdapter(getApplicationContext(), issueModel, new IssueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(IssueModel item, int position) {
                if(item.getType().equals("clicked")){
                    Intent intent = new Intent();
                    intent.putExtra("issue",item.getIssue());
                    intent.putExtra("issueID",item.getIssueID());
                    setResult(2,intent);
                    finish();

                }
            }
        });

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getIssue();
    }

//================================GET ISSUE API====================================//

    private void getIssue (){
        if (UtilityClass.isNetworkConnected(getApplicationContext())) {
            HashMap<String, String> hashMap = new HashMap<>();
            UtilityClass.showLoading(Loaderr, IssueListActivity.this);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getIssues(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("FAQResponse", new Gson().toJson(response.body()));
                    recyclerView.setVisibility(View.VISIBLE);
                    if (response.isSuccessful()) {
                        UtilityClass.hideLoading(Loaderr, IssueListActivity.this);
                        try {
                            Log.e("TAG", "Data get>>>\n: " + new Gson().toJson(response.body()));
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                                new Message().showSnack(root_lay, "No data found.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                new Message().showSnack(root_lay, "This account has been deleted from this platform.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {


                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {

                                new Message().showSnack(root_lay, "This account does not exist.");
                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");


                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                JSONArray jsonArray = new JSONArray(result_data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    IssueModel model = new IssueModel();
                                    model.setIssueID(c.getString(Constants.issueId));
                                    model.setIssue(c.getString(Constants.issue));
                                    issueModel.add(model);
                                }
                                adapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loaderr, IssueListActivity.this);
                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }

    }
}
