package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codeapex.simplrpostprod.Adapter.FAQAdapter;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.FAQModel;
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

public class FAQActivity extends AppCompatActivity {
    ImageView back_press;
    RecyclerView recyclerView;
    ConstraintLayout root_lay;
    SharedPreferences preferences;
    private List<FAQModel> faqModel = new ArrayList<>();
    FAQAdapter adapter;
    LinearLayoutManager manager;
    ConstraintLayout consReport;
    ProgressBar Loaderr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        findIDs();
    }

//===================================FIND IDS======================================//
    public void findIDs(){
        Loaderr = findViewById(R.id.Loaderr);
        back_press = findViewById(R.id.back_press);
        consReport = findViewById(R.id.consReport);
        consReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FAQActivity.this, ReportAnIssue.class);
                startActivity(intent);
            }
        });
        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        root_lay = findViewById(R.id.root_lay);
        preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
        manager = new LinearLayoutManager(this);
        adapter = new FAQAdapter(getApplicationContext(), faqModel, new FAQAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FAQModel item, int position) {
                if(item.getType().equals("clicked")){
                    Intent intent = new Intent(FAQActivity.this, FAQDetailActivity.class);
                    intent.putExtra("qustion",item.getQuestion());
                    intent.putExtra("answer",item.getAnswer());
                    startActivity(intent);
                }
            }
        });

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        getFAQ();
    }


//=================================GET FAQActivity API=====================================//

    private void getFAQ (){
        if (UtilityClass.isNetworkConnected(getApplicationContext())) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, preferences.getString(Constants.userId, ""));
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getFAQ(hashMap);
            //UtilityClass.showLoading(Loaderr, FAQActivity.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    //UtilityClass.hideLoading(Loaderr, FAQActivity.this);
                    Log.e("FAQResponse", new Gson().toJson(response.body()));
                    recyclerView.setVisibility(View.VISIBLE);
                    if (response.isSuccessful()) {

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
                                    FAQModel model = new FAQModel();
                                    model.setQuestionID(c.getString(Constants.questionId));
                                    model.setQuestion(c.getString(Constants.question));
                                    model.setAnswer(c.getString(Constants.answer));
                                    faqModel.add(model);
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
                   // UtilityClass.hideLoading(Loaderr, FAQActivity.this);
                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }

    }

}
