package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Api;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "ghyggh" ;
    ImageView imageView,imgUserImage;
    Button button;
    TextView txtVerifyPhoneNumber, txtVerifyEmail;
    RelativeLayout relativeLayout;
    String name,userId,userName,emailId,contactNumber,dpimg, countryCode = "",
            phoneNumber = "", isEmailIdVerified, isContactNumberVerified;
    ProgressBar Loader;
    EditText edtName,edtUserName,edtContact,edtEmail;
    AlertDialog.Builder alertBox;
    Spinner spinnerPhone;
    ConstraintLayout root_lay;
    String imageBase64, spinnerValue, otp_id;
    private int RESULT_LOAD_IMAGE = 33333;
    Boolean isFromEdit = true;

    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        button = findViewById(R.id.submit_button_id);
        relativeLayout = findViewById(R.id.top);
        Loader = findViewById(R.id.Loader);
        edtName = findViewById(R.id.EdtName);
        edtUserName = findViewById(R.id.EdtUserName);
        edtContact = findViewById(R.id.EdtContactNO);
        edtEmail = findViewById(R.id.EdtEmail);
        imgUserImage = findViewById(R.id.imgUserImage);
        spinnerPhone = findViewById(R.id.spinnerPhone);
        txtVerifyPhoneNumber = findViewById(R.id.txtVerifyPhoneNumber);
        txtVerifyEmail = findViewById(R.id.txtVerifyEmail);

        root_lay = findViewById(R.id.root_lay);

        sharedpreferences = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);


        name = getIntent().getExtras().getString("name");
        userId = getIntent().getExtras().getString("userId");
        userName = getIntent().getExtras().getString("userName");
        emailId = getIntent().getExtras().getString("emailId");
        contactNumber = getIntent().getExtras().getString("contactNumber");
        isEmailIdVerified = getIntent().getExtras().getString("isEmailIdVerified");
        isContactNumberVerified = getIntent().getExtras().getString("isContactNumberVerified");

        Bitmap bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("dpimg"),0,getIntent().getByteArrayExtra("dpimg").length);
        imgUserImage.setImageBitmap(bitmap);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("name", name);
        editor.putString("emailId", emailId);
        editor.putString("contactNumber", contactNumber);
        editor.commit();

        for(int i = 0;i<contactNumber.length();i++)
        {
            if(i<4)
                countryCode = countryCode + contactNumber.charAt(i);
            else
                phoneNumber = phoneNumber + contactNumber.charAt(i);
        }


        edtName.setText(name);
        edtUserName.setText(userName);
        edtContact.setText(phoneNumber);
        edtEmail.setText(emailId);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(adapter);

        if (countryCode != null) {
            int spinnerPosition = adapter.getPosition(countryCode);
            spinnerPhone.setSelection(spinnerPosition);
        }

        spinnerPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


            if (isEmailIdVerified.equals("0"))
                txtVerifyEmail.setVisibility(View.VISIBLE);
            else
                txtVerifyEmail.setVisibility(View.GONE);



            if (isContactNumberVerified.equals("0"))
                txtVerifyPhoneNumber.setVisibility(View.VISIBLE);
            else
                txtVerifyPhoneNumber.setVisibility(View.GONE);



            txtVerifyEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailVerified();

                }
            });

            txtVerifyPhoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EditProfileActivity.this, OTPVerificationActivity.class);
                    intent.putExtra("contactNumber", contactNumber);
                    intent.putExtra("toVerify",true);
                    intent.putExtra("isFromEdit",true);
                    startActivityForResult(intent, 3);
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

        imgUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context wrapper = new ContextThemeWrapper(EditProfileActivity.this, R.style.MyPopupOtherStyle);


                PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);

                popup.getMenuInflater().inflate(R.menu.edit_profile_menu, popup.getMenu());


                //show menu
                popup.show();





                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
//
                        if (item.getTitle().toString().equals("Remove Picture")){
                            imgUserImage.setImageResource(R.drawable.profileplchlder);
                        }
                        else  {
                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                        }

                        return true;
                    }
                });

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                //imageBase64 = encodeImage(image);
                imageBase64 = new UtilityClass().imageToBase64(imgUserImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else if (resultCode == 2){
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
        }
        else if (resultCode == 3) {
            txtVerifyPhoneNumber.setVisibility(View.GONE);
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

//.....................................api..........................//


    void emailVerified()
    {
        emailId = edtEmail.getText().toString().trim();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.email_id,emailId);
        Call<Object> call1 = RetrofitInstance.getdata().create(Api.class).emailVerified(hashMap);
        UtilityClass.showLoading(Loader,EditProfileActivity.this);

        call1.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                UtilityClass.hideLoading(Loader,EditProfileActivity.this);
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
                            new Message().showSnack(root_lay, "No data found.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                            new Message().showSnack(root_lay, "This account has been blocked.");


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                            new Message().showSnack(root_lay, "Username is already used by someone else. ");


                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                            new Message().showSnack(root_lay, "All fields not sent.");

                        }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                            new Message().showSnack(root_lay, " Please check the request method.");
                        }

                        else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                            //String result_data = jsonObject.getString(Constants.RESULT_DATA);

                            //JSONObject jsonObject1 = new JSONObject(result_data);
                            //otp_id = jsonObject1.getString(Constants.otpId);


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

                new UtilityClass().alertBox(EditProfileActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                UtilityClass.hideLoading(Loader,EditProfileActivity.this);
            }
        });
    }





        void editProfile () {
        if (UtilityClass.isNetworkConnected(EditProfileActivity.this)) {
            try {

                if (edtName.getText().toString().trim().isEmpty()) {
                    new Message().showSnack(root_lay, "Please enter name.");
                    edtName.requestFocus();
                    return;
                } else if (edtUserName.getText().toString().trim().length() == 0) {
                    new Message().showSnack(root_lay, "Please enter username.");
                    edtUserName.requestFocus();
                    return;
                } else if (edtEmail.getText().toString().trim().length() == 0) {
                    new Message().showSnack(root_lay, "Please enter an email id.");
                    edtEmail.requestFocus();
                    return;
                } else if (edtContact.getText().toString().trim().length() == 0) {
                    new Message().showSnack(root_lay, "Please enter a contact number.");
                    edtContact.requestFocus();
                    return;
                }
                else {
                    name = edtName.getText().toString().trim();
                    userName = edtUserName.getText().toString().trim();
                    emailId = edtEmail.getText().toString().trim();
                    contactNumber = spinnerValue + edtContact.getText().toString().trim();
                    UtilityClass obj = new UtilityClass();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Constants.userId, userId);
                    hashMap.put(Constants.contactno,contactNumber);
                    hashMap.put(Constants.email_id,emailId);
                    hashMap.put(Constants.namee,name);
                    hashMap.put(Constants.userNamee,userName);
                    hashMap.put(Constants.profilePicURL,obj.imageToBase64(imgUserImage));

                    Call<Object> call = RetrofitInstance.getdata().create(Api.class).editProfile(hashMap);
                    UtilityClass.showLoading(Loader,EditProfileActivity.this);
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            UtilityClass.hideLoading(Loader,EditProfileActivity.this);
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
                                        new Message().showSnack(root_lay, "No data found.");

                                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FIVE)) {
                                        new Message().showSnack(root_lay, "This account has been blocked.");


                                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_FOUR)) {
                                        new Message().showSnack(root_lay, "Username is already used by someone else. ");


                                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SIX)) {
                                        new Message().showSnack(root_lay, "All fields not sent.");

                                    }else if (result_code.equals(Constants.RESULT_CODE_MINUS_SEVEN)) {
                                        new Message().showSnack(root_lay, " Please check the request method.");
                                    }

                                    else if (result_code.equals(Constants.RESULT_CODE_ONE)) {
                                        String result_data = jsonObject.getString(Constants.RESULT_DATA);

                                        JSONObject jsonObject1 = new JSONObject(result_data);
                                        otp_id = jsonObject1.getString(Constants.otpId);

                                        if (otp_id.equals("0")) {
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
                                        }else {
                                            Intent intent=new Intent(EditProfileActivity.this,OTPVerificationActivity.class);
                                            intent.putExtra("otp_id",otp_id);
                                            intent.putExtra("isFromEdit", true);
                                            startActivityForResult(intent, 2);
                                        }

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {

                            new UtilityClass().alertBox(EditProfileActivity.this,t.getLocalizedMessage(),Constants.someissuetitle);
                            UtilityClass.hideLoading(Loader,EditProfileActivity.this);
                        }
                    });
                }




            } catch (Exception e) {
            }
        }
        else {
            new Message().showSnack(root_lay, Constants.noInternetMessage);
        }
        }


    }




