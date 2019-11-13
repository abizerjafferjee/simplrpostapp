package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    //==========Defined Globle variables====================//

    private static final int RC_SIGN_IN = 007;
    public static GoogleApiClient mGoogleApiClient;
    ProgressBar loader;
    LinearLayout linearLayoutSignup;
    TextView textforgotPasswprd;
    CardView cardSubmitButton;
    EditText Edtemail_id, Edtpassword;
    ConstraintLayout root_lay;
    CardView googleButton,btnFacebookLogin;
    LoginManager loginManager;
    private SignInButton btnSignIn;
    private CallbackManager facebookCallbackManager;

    public static void googleSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in_one);

        FirebaseApp.initializeApp(this);

        //Crashlytics.getInstance().crash();





        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("1", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();


                        Log.d("hello", token);
                        SharedPreferences sharedPreferences=getSharedPreferences("Sesssion",MODE_PRIVATE);
                        SharedPreferences.Editor editor =sharedPreferences.edit();
                        editor.putString("token",token);
                        editor.commit();
                    }
                });




        findIds();
    }

    public void findIds(){
        //==========Find ids====================//
        linearLayoutSignup = findViewById(R.id.layoutSignup);
        loader = findViewById(R.id.Loader);
        textforgotPasswprd = findViewById(R.id.forget);
        cardSubmitButton = findViewById(R.id.cardView4);
        Edtemail_id = findViewById(R.id.signin_email_id);
        Edtpassword = findViewById(R.id.signin_password);
        root_lay = findViewById(R.id.root_lay);

        googleButton = findViewById(R.id.googleButton);
        btnSignIn = findViewById(R.id.btn_sign_in);

        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == googleButton){
                    if (UtilityClass.isNetworkConnected(SignInActivity.this)) {
                        signIn();
                    }
                    else {
                        new Message().showSnack(root_lay,Constants.noInternetMessage);
                    }
                }

            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnSignIn.setSize(SignInButton.SIZE_ICON_ONLY);
        btnSignIn.setScopes(gso.getScopeArray());




        loginManager = LoginManager.getInstance();
        loginManager.logOut();
        facebookCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(SignInActivity.this)) {
                    getFacebookUserDetail();
                }
                else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }

            }
        });


        //==========All listeners and Apis====================//

        linearLayoutSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(SignInActivity.this)) {
                    Intent intent = new Intent(SignInActivity.this, SignUpFirstActivity.class);
                    startActivity(intent);
                }
                else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }

            }
        });

        textforgotPasswprd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilityClass.isNetworkConnected(SignInActivity.this)) {
                    Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                    startActivity(intent);
                }
                else {
                    new Message().showSnack(root_lay,Constants.noInternetMessage);
                }
            }
        });


        //...................................Api...............................//

        cardSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (UtilityClass.isNetworkConnected(getApplicationContext())) {
                        View view = getWindow().getCurrentFocus();
                        if (view != null) {
                            UtilityClass.hideKeyboard(SignInActivity.this);
                        }
                        if (Edtemail_id.getText().toString().trim().isEmpty()) {
                            new Message().showSnack(root_lay, "Please enter email id/username.");
                            Edtemail_id.requestFocus();
                            return;
                        }
                        else if (Edtpassword.getText().toString().trim().length() == 0) {
                            new Message().showSnack(root_lay, "Please enter a password.");
                            Edtpassword.requestFocus();
                            return;
                        } else {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Constants.emailId,Edtemail_id.getText().toString().trim());
                            hashMap.put(Constants.password,Edtpassword.getText().toString().trim());
                            Call<Object> call = RetrofitInstance.getdata().create(Api.class).signIn(hashMap);
                            UtilityClass.showLoading(loader,SignInActivity.this);
                            call.enqueue(new Callback<Object>() {
                                @Override
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    UtilityClass.hideLoading(loader,SignInActivity.this);
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                                            if (result_code.equals(Constants.ZERO)) {
                                                new Message().showSnack(root_lay, "No data found.");

                                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                                new Message().showSnack(root_lay, "This account has been deleted from this platform.");

                                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                                new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {

                                                new Message().showSnack(root_lay, " Email Id & password does not match.");
                                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {

                                                new Message().showSnack(root_lay, "This account does not exist.");
                                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                                new Message().showSnack(root_lay, "This account has been blocked.");


                                            } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                                String result_data = jsonObject.getString(Constants.RESULT_DATA);
                                                JSONObject jsonObject1 = new JSONObject(result_data);
                                                SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean(Constants.isUserLoggedIn, true);
                                                editor.putString(Constants.userId, jsonObject1.getString(Constants.userId));
                                                editor.commit();

                                                register_device();
                                                startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                                                finish();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Object> call, Throwable t) {
                                    new UtilityClass().alertBox(SignInActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                                    UtilityClass.hideLoading(loader,SignInActivity.this);
                                }
                            });
                        }
                    }
                    else {
                        new Message().showSnack(root_lay,Constants.noInternetMessage);
                    }
                } catch (Exception e) {
                }
            }
        });
    }




//=========================================GOOGLE SIGN IN==================================================//

    //...................................Api...............................//
    public void register_device() {
       SharedPreferences preferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
       String struserId = preferences.getString(Constants.userId,"");


        try {



            final String strDeviceid = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.userId,struserId);
            hashMap.put(Constants.deviceId, strDeviceid);
            hashMap.put(Constants.deviceType, "a");
            hashMap.put(Constants.pushToken, preferences.getString("token",""));

            UtilityClass.showLoading(loader,SignInActivity.this);


            Call<Object> call = RetrofitInstance.getdata().create(Api.class).registerDevice(hashMap);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(loader,SignInActivity.this);
                    if (response.isSuccessful()) {
                        try {

                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
                                new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                                new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                            }
                            else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                                new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                            } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                                new Message().showSnack(root_lay, "Email Id and password didn't matched.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                new Message().showSnack(root_lay, "This account has been blocked.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                new Message().showSnack(root_lay, "This account does not exist.");


                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                new Message().showSnack(root_lay, "Contact number already registered.");

                            }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                new Message().showSnack(root_lay, " Please check the request method.");
                            }

                            else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                String result_data = jsonObject.getString(Constants.RESULT_DATA);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(loader,SignInActivity.this);
                    new UtilityClass().alertBox(SignInActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);
                }
            });
        } catch (Exception e) {

        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //mGoogleApiClient.connect();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            getGoogleLoginUserData(result);
        } else {

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    getGoogleLoginUserData(googleSignInResult);
                    //Log.d("NO OPR","NO OPR");
                }
            });
        }
    }



    private void getGoogleLoginUserData(GoogleSignInResult result) {
        googleSignOut();
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "display name: " + acct.getDisplayName());
            final String personName = acct.getDisplayName();

            final String email = acct.getEmail();
            final String id = acct.getId();
            Log.e("googleData",personName+ "  "+email +"  "+id);
            emailValidation(email);
        }else {
            //Log.e("Fail","Login Failed");
            Log.e("Fail",result.getStatus().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getGoogleLoginUserData(result);
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
    }





 //==========================================FacebookLogin=========================================//
 protected void getFacebookUserDetail() {

     loginManager.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
     loginManager.logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));
     loginManager.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(LoginResult loginResult) {
             GraphRequest request =  GraphRequest.newMeRequest(
                     loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                         @Override
                         public void onCompleted(JSONObject object, GraphResponse response) {
                             Log.e("TAG", object.toString());
                             try {
                                 String strEmail = object.getString("email");;
                                 String first_name = object.getString("first_name");
                                 String last_name = object.getString("last_name");
                                 String strId = object.getString("id");
                                 String strImage_url = "https://graph.facebook.com/" + strId + "/picture?type=large";
                                 String strName = first_name+" "+last_name;
                                 Log.e("ImageFacebook",strImage_url);
                                /* if(object.has("email")){
                                     strEmail = object.getString("email");
                                 }*/
                                 Log.e("facebookData",strEmail+"   "+strName);
                                 emailValidation(strEmail);

                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }

                         }

                     });
             Bundle permission_param = new Bundle();
             permission_param.putString("fields", "first_name,last_name,email,id,picture.width(500).height(500)");
             request.setParameters(permission_param);
             request.executeAsync();
         }

         @Override
         public void onCancel() {

         }

         @Override
         public void onError(FacebookException error) {

         }
     });


 }

    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
        loginManager.logOut();
    }
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


//===============================================Email Validation API==============================================//

    public void emailValidation(final String strEmail){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.emailId1,strEmail);
        Call<Object> call = RetrofitInstance.getdata().create(Api.class).validateEmail(hashMap);
        UtilityClass.showLoading(loader,SignInActivity.this);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(loader,SignInActivity.this);
                Log.e("Email_Response", new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "No data found.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "This account has been deleted");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "This account has been deactivated");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");

                        } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
                            JSONObject jsonObject1 = new JSONObject(result_data);
                            String strUserID = jsonObject1.getString(Constants.userId);
                            String strName = jsonObject1.getString(Constants.name);
                            String strEmailId = jsonObject1.getString(Constants.emailId1);
                            String strUserName = jsonObject1.getString(Constants.userName);
                            String strContactNumber = jsonObject1.getString(Constants.contactNumber);
                            String strProfilePicURL = jsonObject1.getString(Constants.profilePicURL);

                            SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(Constants.isUserLoggedIn, true);
                            editor.putString(Constants.userId, strUserID);
                            editor.commit();

                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                            finish();

                            Log.e("data",strUserID+strName+strEmailId);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                new UtilityClass().alertBox(SignInActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                UtilityClass.hideLoading(loader,SignInActivity.this);
            }
        });
    }

}
