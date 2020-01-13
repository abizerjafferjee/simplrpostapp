package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.model.Progress;
import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tooltip.Tooltip;

import cc.cloudist.acplibrary.ACProgressFlower;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "ghyggh";
    ImageView imageView, imgUserImage;
    File imageFile = null;
    Uri filePath;
    Button button;
    TextView txtVerifyEmail, txt_contactNumber, txt_info;
    RelativeLayout relativeLayout,top;
    String name, userId, emailId, contactNumber, dpimg, countryCode = "",
            phoneNumber = "", isEmailIdVerified, isContactNumberVerified;
    ProgressBar Loader;
    EditText edtName, edtEmail, edt_answer;
    AlertDialog.Builder alertBox;
    Spinner spinnerQuestions;
    LinearLayout root_lay;
    ScrollView root_lay1;
    String imageBase64, spinnerValue, otp_id;
    private int RESULT_LOAD_IMAGE = 33333;
    Boolean isFromEdit = true;
    SharedPreferences sharedpreferences;
    String question, answer;
    View edt_line_answer;
    private SharedPreferences sharedpreferences_session;
    private ProgressBar progressBarImage;
    String imgURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedpreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        sharedpreferences_session = getSharedPreferences("Sesssion", Context.MODE_PRIVATE);

        name = this.getIntent().getStringExtra("name");
        userId = this.getIntent().getStringExtra("userId");
        emailId = this.getIntent().getStringExtra("emailId");
        contactNumber = this.getIntent().getStringExtra("contactNumber");
        isEmailIdVerified = this.getIntent().getStringExtra("isEmailIdVerified");
        isContactNumberVerified = this.getIntent().getStringExtra("isContactNumberVerified");
        question = this.getIntent().getStringExtra("question");
        answer = this.getIntent().getStringExtra("answer");

        contactNumber = sharedpreferences_session.getString(Constants.contactNumber, "");
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("name", name);
        editor.putString("emailId", emailId);
        editor.putString("contactNumber", contactNumber);
        editor.commit();

        button = findViewById(R.id.submit_button_id);
        relativeLayout = findViewById(R.id.top);
        Loader = findViewById(R.id.Loader);
        edtName = findViewById(R.id.EdtName);
        spinnerQuestions = findViewById(R.id.spinnerQuestions);
        txt_contactNumber = findViewById(R.id.txt_contactNumber);
        edtEmail = findViewById(R.id.EdtEmail);
        imgUserImage = findViewById(R.id.imgUserImage);
        spinnerQuestions = findViewById(R.id.spinnerQuestions);
        txtVerifyEmail = findViewById(R.id.txtVerifyEmail);
        edt_answer = findViewById(R.id.edt_answer);
        root_lay = findViewById(R.id.root_lay);
        root_lay1 = findViewById(R.id.root_lay1);
        txt_info = findViewById(R.id.txt_info);
        edt_line_answer = findViewById(R.id.edt_line_answer);
        progressBarImage = findViewById(R.id.progressBarImage);
        top = findViewById(R.id.top);

        edt_answer.setText(answer);

        txt_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Tooltip.Builder(v, R.style.Tooltip2)
                        .setText("Add an additional layer of security\nto protect your account.")
                        .setCancelable(true)
                        .setGravity(Gravity.TOP)
                        .setArrowHeight(R.dimen.file_name_textSize)
                        .show();

            }
        });


        edtName.setText(name);
        txt_contactNumber.setText(contactNumber);
        edtEmail.setText(emailId);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.question, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestions.setAdapter(adapter);

        if (countryCode != null) {
            int spinnerPosition = adapter.getPosition(countryCode);
            spinnerQuestions.setSelection(spinnerPosition);
        }

        edt_answer.setVisibility(View.INVISIBLE);
        edt_line_answer.setVisibility(View.INVISIBLE);
        spinnerQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = position + parent.getItemAtPosition(position).toString();
                if (spinnerValue.trim().equals("0Security question")) {
                    edt_answer.setVisibility(View.INVISIBLE);
                    edt_line_answer.setVisibility(View.INVISIBLE);
                } else {
                    edt_answer.setVisibility(View.VISIBLE);
                    edt_line_answer.setVisibility(View.VISIBLE);
                }
                if (question != null && !question.isEmpty()) {
                    if (Integer.parseInt("" + question.charAt(0)) != position) {
                        edt_answer.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.e("question", "data" + question);
        if (question != null && !question.isEmpty()) {
            spinnerQuestions.setSelection(Integer.parseInt("" + question.charAt(0)));
        }

        txtVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailVerified();

            }
        });

        imageView = findViewById(R.id.back_press);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().getCurrentFocus();
                if (view != null) {
                    UtilityClass.hideKeyboard(EditProfileActivity.this);
                }
                editProfile();

            }
        });



         imgURL = sharedpreferences_session.getString(Constants.profilePicURL, "");
        Log.e("imgURL","imgURL::"+imgURL);
        if (imgURL != null && !imgURL.isEmpty()) {
            dpimg = Constants.IMG_URL + imgURL;

            Picasso.with(EditProfileActivity.this)
                    .load(dpimg)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.profileplchlder)
                    .into(imgUserImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBarImage.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBarImage.setVisibility(View.GONE);
                        }
                    });
        }else {
            progressBarImage.setVisibility(View.GONE);
            imgUserImage.setImageResource(R.drawable.profileplchlder);
        }

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imgURL != null && !imgURL.isEmpty()) {
                    Context wrapper = new ContextThemeWrapper(EditProfileActivity.this, R.style.MyPopupOtherStyle);
                    PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                    popup.getMenuInflater().inflate(R.menu.edit_profile_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getTitle().toString().equals("Remove Picture")) {
                                imgUserImage.setImageResource(R.drawable.profileplchlder);
                                Log.e("imgurl","::"+imgURL );
                                imgURL=null;
                                imageFile = null;

                            } else {
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            }
                            return true;
                        }
                    });

                }else {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgUserImage.setImageBitmap(bitmap);
                BitmapImagetoFile(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == 2) {
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(EditProfileActivity.this);
            builder.setMessage("Your profile has been updated successfully.");
            builder.setTitle("Success");
            builder.setCancelable(false);
            builder
                    .setPositiveButton(
                            "Ok",
                            new DialogInterface
                                    .OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent();
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (resultCode == 3) {
            //txtVerifyPhoneNumber.setVisibility(View.GONE);
        }


    }

    private void BitmapImagetoFile(Bitmap bitmap) {
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            new File(path + "/" + getString(R.string.app_name)).mkdirs();
            imageFile = new File(path + "/" + getString(R.string.app_name) + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//.....................................api..........................//

    void emailVerified() {
        emailId = edtEmail.getText().toString().trim();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.email_id, emailId);
        Call<Object> call1 = RetrofitInstance.getdata().create(Api.class).emailVerified(hashMap);
        final ACProgressFlower progressFlower = Parser.initProgressDialog(EditProfileActivity.this, "");

        call1.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (progressFlower != null) {
                    progressFlower.dismiss();
                }
                if (response.isSuccessful()) {

                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        Log.e("jsonObject", "" + jsonObject);
                        String result_code = jsonObject.getString(Constants.RESULT_CODE);
                        if (result_code.equals(Constants.ZERO)) {
                            new Message().showSnack(root_lay, "This account has been deactivated from this platform.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_ONE)) {
                            new Message().showSnack(root_lay, "This account has been deleted from this platform.");
                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_TWO)) {
                            new Message().showSnack(root_lay, "Something went wrong. Please try after some time.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_THREE)) {
                            new Message().showSnack(root_lay, "No data found.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                            new Message().showSnack(root_lay, "Username is already used by someone else. ");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        } else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        } else if (result_code.equals(Constants.RESULT_CODE_ONE)) {

                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(EditProfileActivity.this);
                            builder.setMessage("An email has been sent to you with link to verify your email account.");
                            builder.setCancelable(false);
                            builder
                                    .setPositiveButton(
                                            "Ok",
                                            new DialogInterface
                                                    .OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    //Intent intent = new Intent();
                                                    //setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

                new UtilityClass().alertBox(EditProfileActivity.this, t.getLocalizedMessage(), Constants.someissuetitle);
                UtilityClass.hideLoading(Loader, EditProfileActivity.this);
            }
        });
    }

    void editProfile() {
        Log.e("spinnerValue", "" + spinnerValue);
        if (UtilityClass.isNetworkConnected(EditProfileActivity.this)) {
            try {

                if (edtName.getText().toString().trim().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter name.");
                    edtName.requestFocus();
                    return;
                } else if (edtEmail.getText().toString().trim().length() == 0) {
                    new Message().showSnack(root_lay, "Please enter an email address.");
                    edtEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
                    new Message().showSnack(root_lay, "Invalid email address.");
                    edtEmail.requestFocus();
                    return;
                } else if (spinnerValue.trim().equals("0Security question")) {
                    new Message().showSnack(root_lay, "Please select security question.");
                    return;
                } else if (edt_answer.getText().toString().trim().length() == 0) {
                    new Message().showSnack(root_lay, "Please enter answer.");
                    edt_answer.requestFocus();
                    return;
                } else if (imageFile == null) {
                    //new Message().showSnack(root_lay, "Image null");
                    name = edtName.getText().toString().trim();
                    emailId = edtEmail.getText().toString().trim();
                    contactNumber = txt_contactNumber.getText().toString().trim();
///code by khushbu
                    if(imgURL==null){
                        updateProfile("remove");
                    }else {
                        updateProfile("");
                    }


                } else {

                    name = edtName.getText().toString().trim();
                    emailId = edtEmail.getText().toString().trim();
                    contactNumber = txt_contactNumber.getText().toString().trim();
                    Log.e("dataa", "" + Formatter.formatShortFileSize(EditProfileActivity.this, imageFile.length()));

                    updateProfileMultipart();
                }


            } catch (Exception e) {
            }
        } else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }
    }

    private void updateProfileMultipart() {


        RequestBody userId_ = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody contactNumber_ = RequestBody.create(MediaType.parse("multipart/form-data"), contactNumber);
        RequestBody emailId_ = RequestBody.create(MediaType.parse("multipart/form-data"), emailId);
        RequestBody security_question_ = RequestBody.create(MediaType.parse("multipart/form-data"), spinnerValue);
        RequestBody security_answer_ = RequestBody.create(MediaType.parse("multipart/form-data"), edt_answer.getText().toString());
        RequestBody name_ = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData(Constants.profilePicURL, imageFile.getName(), requestFile);


        Parser.callApi(EditProfileActivity.this, "Sign In...", true, ApiClient.getClient().create(ApiInterface.class).updateProfileMultipart(userId_, contactNumber_, emailId_, security_question_, security_answer_, name_, body), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Edit response::", "" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result_code = jsonObject.getString("resultCode");

                    if (result_code.equals("1")) {
                        JSONObject resultData = jsonObject.getJSONObject("resultData");
                        JSONObject json_otpId = resultData.optJSONObject("otpId");
                        String otpId_ = json_otpId.optString("otpId");
                        //String otpType = json_otpId.optString("otpType");
                        String isEmailVerified = resultData.optString("isEmailVerified");
                        if (isEmailVerified.equals("0")) {
                            startActivity(new Intent(EditProfileActivity.this, OTPActivity_forgot.class)
                                    .putExtra("otpId", otpId_)
                                    .putExtra("otpType", "3")
                                    .putExtra("userId", userId)
                                    .putExtra("from", "email_edit")
                                    .putExtra("email", edtEmail.getText().toString().trim())
                                    .putExtra("mobileNumber", contactNumber));

                        } else {
                            new Message().showSnackGreen(root_lay, "Profile updated successfully!");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(EditProfileActivity.this, Home_Activity_new.class)
                                            .putExtra("userId", userId));
                                    finish();
                                }
                            }, 300);
                        }
                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, ""+jsonObject.optString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateProfile(String body) {

        String userId_ = userId;
        String contactNumber_ = contactNumber;
        String emailId_ = emailId;
        String security_question_ = spinnerValue;
        String security_answer_ = edt_answer.getText().toString();
        String name_ = name;
       // String body = "";

        Parser.callApi(EditProfileActivity.this, "Sign In...", true, ApiClient.getClient().create(ApiInterface.class)
                .updateProfile(userId_, contactNumber_, emailId_, security_question_, security_answer_, name_, body), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {
                Log.e("Edit without image ::", "" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String result_code = jsonObject.getString("resultCode");

                    if (result_code.equals("1")) {
                        JSONObject resultData = jsonObject.getJSONObject("resultData");
                        JSONObject json_otpId = resultData.optJSONObject("otpId");
                        String otpId_ = json_otpId.optString("otpId");
                        String isEmailVerified = resultData.optString("isEmailVerified");
                        if (isEmailVerified.equals("0")) {
                            isEmailVerified="0";
                            startActivity(new Intent(EditProfileActivity.this, OTPActivity_forgot.class)
                                    .putExtra("otpId", otpId_)
                                    .putExtra("otpType", "3")
                                    .putExtra("userId", userId)
                                    .putExtra("from", "email_edit")
                                    .putExtra("email", edtEmail.getText().toString().trim())
                                    .putExtra("mobileNumber", contactNumber));

                        } else {
                            new Message().showSnackGreen(root_lay, "Profile updated successfully!");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(EditProfileActivity.this, Home_Activity_new.class)
                                            .putExtra("userId", userId));
                                    finish();
                                }
                            }, 300);
                        }
                    } else if (result_code.equals("0")) {
                        new Message().showSnack(root_lay, ""+jsonObject.optString("data"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}




