package com.codeapex.simplrpostprod.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFirstActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    //==========================Defined Globle variables=====================================//

    LinearLayout linearLayout, linearLayoutt;
    CardView cardView,googleButton,btnFacebookLogin;
    EditText name, username;
    String strname, strusername, strimagurl;
    Boolean isLoading = false;
    ConstraintLayout root_lay;
    ImageView back_press;
    CircleImageView imgUserImage;
    private String TAG = "tti3i";
    private static int PICK_IMAGE_REQUEST = 1;
    String imageBase64;
    private int RESULT_LOAD_IMAGE = 33333;


    private static final int RC_SIGN_IN = 007;
    public static GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;

    private CallbackManager facebookCallbackManager;
    LoginManager loginManager;

    String strEmail ;
    String strId ;
    String strImage_url ;
    String strName ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);

        //============================FindView and Listners=======================================//

        back_press = findViewById(R.id.back_press);
        cardView = findViewById(R.id.cardView4);
        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        name = findViewById(R.id.signup_name);
        linearLayoutt = findViewById(R.id.liner);
        username = findViewById(R.id.signup_username);
        root_lay = findViewById(R.id.root_lay);
        imgUserImage = findViewById(R.id.circularimage);

        googleButton = findViewById(R.id.googleButton);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnSignIn = findViewById(R.id.btn_sign_in);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });


        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        linearLayout = findViewById(R.id.linear);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                    signIn();
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

                getFacebookUserDetail();

            }
        });


    }




    //====================================Functions===========================================//
    public void Login() {
        try {
            if (name.getText().toString().trim().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter name.");
                name.requestFocus();
                return;
            } else if (username.getText().toString().trim().isEmpty()) {
                new Message().showSnack(root_lay, "Please enter username.");
                username.requestFocus();
                return;
            } else {
                loginclick();

            }
        } catch (Exception e) {
        }
    }

    //================================End Functions===========================================//





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getGoogleLoginUserData(result);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imgUserImage.setImageBitmap(bmp);
            final InputStream imageStream;
            try {
                imageStream = this.getContentResolver().openInputStream(selectedImage);
                final Bitmap image = BitmapFactory.decodeStream(imageStream);
               // imageBase64 = encodeImage(image);
                imageBase64 = (new UtilityClass()).imageToBase64(imgUserImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void loginclick() {

        strname = name.getText().toString().trim();
        strusername = username.getText().toString().trim();
        Intent intent = new Intent(SignUpFirstActivity.this, SignupSecondActivity.class);
        intent.putExtra("nameee", strname);
        intent.putExtra("usernameee", strusername);
        UtilityClass obj = new UtilityClass();
        intent.putExtra("Imgbase64", obj.imageToBase64(imgUserImage));
        intent.putExtra("emailID", strEmail);
        Log.d(TAG, "onClick: " + strname + strusername + imageBase64);
        startActivity(intent);

    }




    //=========================================GOOGLE SIGN IN==================================================//


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

            String personPhotoUrl;
            if(acct.getPhotoUrl() == null){
                personPhotoUrl = "";
            }else {
                personPhotoUrl = acct.getPhotoUrl().toString();
            }
            strEmail = acct.getEmail();
            final String id = acct.getId();
            Log.e("googleData",personName+ "  "+strEmail +"  "+id);
            emailValidation(strEmail,personPhotoUrl,personName);
        }else {
            Log.e("Fail","Falied to login");
        }
    }

    public static void googleSignOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
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
                                     strEmail = object.getString("email");;
                                     String first_name = object.getString("first_name");
                                     String last_name = object.getString("last_name");
                                     strId = object.getString("id");
                                     strImage_url = "https://graph.facebook.com/" + strId + "/picture?type=large";
                                     strName = first_name+" "+last_name;
                                     Log.e("ImageFacebook",strImage_url);
                                /* if(object.has("email")){
                                     strEmail = object.getString("email");
                                 }*/
                                    Log.e("facebookData",strEmail+"   "+strName);
                                    emailValidation(strEmail,strImage_url,strName);

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

    public void emailValidation(final String strEmail, final String imgUrl, final String Name){
        if (UtilityClass.isNetworkConnected(SignUpFirstActivity.this)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.emailId1,strEmail);
            Call<Object> call = RetrofitInstance.getdata().create(Api.class).validateEmail(hashMap);
//        loader.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Log.e("Email_Response", new Gson().toJson(response.body()));
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            String result_code = jsonObject.getString(Constants.RESULT_CODE);
                            if (result_code.equals(Constants.ZERO)) {
//                            new Message().showSnack(root_lay, "No data found.");
                                name.setText(Name);
                                if(imgUrl.isEmpty()){

                                }else {
                                    Picasso.with(SignUpFirstActivity.this).load(imgUrl).placeholder(R.drawable.round).into(imgUserImage);
                                }

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

                                new Message().showSnack(root_lay, "Email id is already exist.");

//                            String result_data = jsonObject.getString(Constants.RESULT_DATA);
//                            JSONObject jsonObject1 = new JSONObject(result_data);
//                            String strUserID = jsonObject1.getString(Constants.userId);
//                            String strName = jsonObject1.getString(Constants.name);
//                            String strEmailId = jsonObject1.getString(Constants.emailId1);
//                            String strUserName = jsonObject1.getString(Constants.userName);
//                            String strContactNumber = jsonObject1.getString(Constants.contactNumber);
//                            String strProfilePicURL = jsonObject1.getString(Constants.profilePicURL);

                        /*    SharedPreferences sharedPreferences = getSharedPreferences("Sesssion", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(Constants.isUserLoggedIn, true);
                            editor.putString(Constants.userId, strUserID);
                            editor.commit();

                            startActivity(new Intent(SignUpFirstActivity.this,HomeActivity.class));
                            finish();*/

//                            Log.e("data",strUserID+strName+strEmailId);

                            }
//                        loader.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    new UtilityClass().alertBox(SignUpFirstActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);

                }
            });
        }
        else {
            new Message().showSnack(root_lay,Constants.noInternetMessage);
        }

    }
}