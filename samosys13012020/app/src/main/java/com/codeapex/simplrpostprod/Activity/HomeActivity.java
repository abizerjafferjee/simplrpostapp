package com.codeapex.simplrpostprod.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.Fragment.SettingsFragment;
import com.codeapex.simplrpostprod.Fragment.ExploreFragment;
import com.codeapex.simplrpostprod.Fragment.NotifiactionFragment;
import com.codeapex.simplrpostprod.Fragment.ProfileFragment;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    ProfileFragment profilefragment = new ProfileFragment();
    ExploreFragment explorefragment = new ExploreFragment();
    NotifiactionFragment notificationfragment = new NotifiactionFragment();
    SettingsFragment settingsfragment = new SettingsFragment();
    ImageView imageView;
    Fragment frgtActive = profilefragment;
    FragmentManager fragmentManager = getSupportFragmentManager();
    LinearLayout rootLayer;
    ProgressBar Loader;

    String referenceId;
    ConstraintLayout root_lay;
    SharedPreferences preferences;
    String userId, name,intent_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

            fragmentManager.beginTransaction().add(R.id.frgtContainerr, profilefragment, "home").commit();
            tabLayout = findViewById(R.id.sliding);

            //getting profile data
            userId = this.getIntent().getStringExtra("userId");

            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.profile_filled));
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.magnifyingglassbrowser));
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.belliconunfilled));
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.settingunfilled));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            rootLayer = findViewById(R.id.rootLayer);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setCurrentTabFragment(tab.getPosition());
                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.profile_filled);
                    } else if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.searchfilled);
                    } else if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.belliconfilled);
                    } else if (tab.getPosition() == 3) {
                        tab.setIcon(R.drawable.settingsfilled);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.profileunfilled);
                    } else if (tab.getPosition() == 1) {
                        tab.setIcon(R.drawable.magnifyingglassbrowser);
                    } else if (tab.getPosition() == 2) {
                        tab.setIcon(R.drawable.belliconunfilled);
                    } else if (tab.getPosition() == 3) {
                        tab.setIcon(R.drawable.settingunfilled);

                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            clearStack();

            if (getIntent().hasExtra("type")) {
                String type = getIntent().getStringExtra("type");
                try {
                    if (type.equals("admin")) {
                        TabLayout.Tab tab = tabLayout.getTabAt(2);
                        tab.select();
                    } else if (type.equals("userShare")) {
                        Intent intent = new Intent(HomeActivity.this, SharedWithMeActivity.class);
                        intent.putExtra("isShared", true);
                        intent.putExtra("shareType", "user");
                        intent.putExtra("addressId", getIntent().getStringExtra("addressId"));
                        intent.putExtra("isAddressPublic", getIntent().getStringExtra("isAddressPublic"));
                        startActivity(intent);
                    } else if (type.equals("businessShare")) {
                        Intent intent1 = new Intent(HomeActivity.this, PublicLocationDetailNewActivity.class);
                        intent1.putExtra("addressId", getIntent().getStringExtra("recipientBusinessId"));
                        intent1.putExtra("isOwn", true);
                        intent1.putExtra("isFromShared", true);
                        intent1.putExtra("isFromNotification", true);
                        intent1.putExtra("sharedAddressId", getIntent().getStringExtra("addressId"));
                        intent1.putExtra("isAddressPublic", getIntent().getStringExtra("isAddressPublic"));
                        startActivity(intent1);
                    }
                } catch (Exception e) {

                }
            }
    }

    //Method to call API
    public void getAddressId() {
        if (UtilityClass.isNetworkConnected(HomeActivity.this)) {
            SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
            String user_Id = preferences.getString(Constants.userId, "");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId, user_Id);
            hashMap.put(Constants.addressReferenceId, referenceId);

            Call<Object> call = RetrofitInstance.getdata().create(Api.class).getAddressIdWithReferenceCode(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {

                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(rootLayer, "This account has been deactivated from this platform.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(rootLayer, "This account has been deleted from this platform.");
                        }
                        else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(rootLayer, "Something went wrong. Please try after some time.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(rootLayer, "No data found.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(rootLayer, "This account has been blocked.");


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(rootLayer, "All fields not sent.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(rootLayer, " Please check the request method.");
                        }

                        else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            final JSONObject jsonObject1 = new JSONObject(result_data);
                            if (referenceId.substring(0,3).equals("PUB")) {
                                Intent intent = new Intent(HomeActivity.this, PublicLocationDetailNewActivity.class);
                                intent.putExtra("addressId", jsonObject1.getString("addressId"));
                                if (jsonObject1.getString("isOwn").equals("1")) {
                                    intent.putExtra("isOwn",true);
                                }
                                else {
                                    intent.putExtra("isOwn",false);
                                }
                                intent.putExtra("isFromShared",true);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(HomeActivity.this, PrivateLocationDetailNewActivity.class);
                                intent.putExtra("addressId", jsonObject1.getString("addressId"));
                                if (jsonObject1.getString("isOwn").equals("1")) {
                                    intent.putExtra("isOwn",true);
                                }
                                else {
                                    intent.putExtra("isOwn",false);
                                }
                                intent.putExtra("isFromShared",true);
                                startActivity(intent);
                            }



                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t);
//                UtilityClass.hideLoading(Loader,HomeActivity.this);

                }
            });
        }
        else {
            new Message().showSnack(rootLayer, Constants.noInternetMessage);
        }



    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition)
        {
            case 0 :
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().hide(frgtActive).show(profilefragment).commit();
                        frgtActive = profilefragment;
                    }
                },300);

                break;
            case 1 :

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fragmentManager.findFragmentByTag("explore") == null) {
                            fragmentManager.beginTransaction().add(R.id.frgtContainerr, explorefragment, "explore").hide(explorefragment).commit();
                        }
                        fragmentManager.beginTransaction().hide(frgtActive).show(explorefragment).commit();
                        frgtActive = explorefragment;
                    }
                },300);
                break;
            case 2 :
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fragmentManager.findFragmentByTag("notification") == null) {
                            fragmentManager.beginTransaction().add(R.id.frgtContainerr, notificationfragment, "notification").hide(notificationfragment).commit();
                        }
                        fragmentManager.beginTransaction().hide(frgtActive).show(notificationfragment).commit();
                        frgtActive = notificationfragment;
                    }
                },300);
                break;
            case 3 :

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fragmentManager.findFragmentByTag("settings") == null) {
                            fragmentManager.beginTransaction().add(R.id.frgtContainerr, settingsfragment, "settings").hide(settingsfragment).commit();
                        }
                        fragmentManager.beginTransaction().hide(frgtActive).show(settingsfragment).commit();
                        frgtActive = settingsfragment;
                    }
                },300);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if(tabLayout.getSelectedTabPosition() == 0){
            tabLayout.getSelectedTabPosition();


            AlertDialog.Builder alertbox = new AlertDialog.Builder(HomeActivity.this);
            alertbox.setMessage("");
            alertbox.setCancelable(false);
            alertbox.setTitle("Are you sure you want to exit?");
            alertbox.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertbox.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            finish();

                        }
                    });
            alertbox.show();




        }else {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            setCurrentTabFragment(0);
        }
    }

    public void clearStack() {
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                Fragment mFragment = getSupportFragmentManager().getFragments().get(i);
                if (mFragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
                }
            }
        }
    }

}