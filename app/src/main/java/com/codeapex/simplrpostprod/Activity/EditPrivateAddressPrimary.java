package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codeapex.simplrpostprod.AlertViews.Message;
import com.codeapex.simplrpostprod.ModelClass.ContactNumber;
import com.codeapex.simplrpostprod.ModelClass.PrivateAddressRequest;
import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.RetrofitApi.RetrofitInstance;
import com.codeapex.simplrpostprod.UtilityClass.UtilityClass;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPrivateAddressPrimary extends AppCompatActivity {

    ImageView imgBack;
    CardView crdSlide1Next, crdSlide2Next, crdSlide2Previous;
    LinearLayout llytSlide1, llytSlide2;
    EditText edtShortName, edtEmailId, edtPhoneNumber;
    ImageView imgLocationPicture;
    ConstraintLayout clytRootLayer;
    ProgressBar Loader;
    Spinner spinnerPhone;

    PrivateAddressRequest privateAddressData;
    String imageBase64, countryCode = "", phoneNumber = "", spinnerValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_private_address_primary);

        imgBack = findViewById(R.id.back_press);
        clytRootLayer = findViewById(R.id.rootLayer);
        Loader = findViewById(R.id.Loader);
        crdSlide1Next = findViewById(R.id.crdSlide1Next);
        crdSlide2Next = findViewById(R.id.crdSlide2Next);
        crdSlide2Previous = findViewById(R.id.crdSlide2Previous);
        llytSlide1 = findViewById(R.id.slide1);
        llytSlide2 = findViewById(R.id.slide2);
        edtShortName = findViewById(R.id.edtBusinessName);
        edtEmailId = findViewById(R.id.txt_pub_email);
        edtPhoneNumber = findViewById(R.id.txtvPhoneNumber);
        imgLocationPicture = findViewById(R.id.imgLocationPic);
        spinnerPhone = findViewById(R.id.spinnerPhone);



        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(adapter);


        spinnerPhone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerValue = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imgLocationPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(EditPrivateAddressPrimary.this);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,44444);
            }
        });


        if (getIntent().hasExtra("privateAddressData")) {
            privateAddressData = (PrivateAddressRequest) getIntent().getSerializableExtra("privateAddressData");
            edtShortName.setText(privateAddressData.getShortName());
            edtEmailId.setText(privateAddressData.getEmailId());
            for(int i = 0;i<privateAddressData.getContactNumber().get(0).getPhoneNumber().length();i++)
            {
                if(i<4)
                    countryCode = countryCode + privateAddressData.getContactNumber().get(0).getPhoneNumber().charAt(i);
                else
                    phoneNumber = phoneNumber + privateAddressData.getContactNumber().get(0).getPhoneNumber().charAt(i);
            }
            if (countryCode != null) {
                int spinnerPosition = adapter.getPosition(countryCode);
                spinnerPhone.setSelection(spinnerPosition);
            }
            edtPhoneNumber.setText(phoneNumber);
//            Glide.with(this).load(Constants.IMG_URL + privateAddressData.getPictureURL()).error(Glide.with(this).load(R.drawable.placeholder))
//                    .into(imgLocationPicture);


            Picasso.with(this)
                    .load(Constants.IMG_URL.concat(privateAddressData.getPictureURL()))
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.placeholder)
                    .into(imgLocationPicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });


        }




        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        crdSlide1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtShortName.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer, "Please enter business name.");
                }
                else {
                    llytSlide1.setVisibility(View.GONE);
                    llytSlide2.setVisibility(View.VISIBLE);
                }
            }
        });

        crdSlide2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmailId.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer,"Please enter an email id.");
                }
                else if (edtPhoneNumber.getText().toString().trim().equals("")) {
                    new Message().showSnack(clytRootLayer,"Please enter a phone number");
                }
                else {
                    //Code to call the API to save the primary information
                    try {
                        editPrivateAddressPrimary();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        crdSlide2Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llytSlide2.setVisibility(View.GONE);
                llytSlide1.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 44444 && resultCode == Activity.RESULT_OK) {
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
            imgLocationPicture.setImageBitmap(bmp);
            final InputStream imageStream;
            try {
                imageStream = this.getContentResolver().openInputStream(selectedImage);
                final Bitmap image = BitmapFactory.decodeStream(imageStream);
                //imageBase64 = encodeImage(image);
                imageBase64 = new UtilityClass().imageToBase64(imgLocationPicture);

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



    //API related methods

    private void editPrivateAddressPrimary() throws JSONException {
        if (UtilityClass.isNetworkConnected(EditPrivateAddressPrimary.this)) {
            PrivateAddressRequest request = new PrivateAddressRequest();
            UtilityClass typefaceUtil = new UtilityClass();

            ArrayList<ContactNumber> arrayListner = new ArrayList<ContactNumber>();
            arrayListner.add(new ContactNumber(spinnerValue + edtPhoneNumber.getText().toString().trim()));

            request.setUserId(getSharedPreferences("Sesssion", Context.MODE_PRIVATE).getString(Constants.userId,""));
            request.setAddressId(privateAddressData.getAddressId());
            request.setShortName(edtShortName.getText().toString().trim());
            request.setEmailId(edtEmailId.getText().toString().trim());
            request.setContactNumber(arrayListner);
            request.setAddressPicture(typefaceUtil.imageToBase64(imgLocationPicture));



            Call<Object> call;
            call = RetrofitInstance.getdata().create(com.codeapex.simplrpostprod.RetrofitApi.Api.class).editPrivatePrimary(request);
            UtilityClass.showLoading(Loader,EditPrivateAddressPrimary.this);
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    UtilityClass.hideLoading(Loader,EditPrivateAddressPrimary.this);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                            int result_code = jsonObject.getInt(Constants.RESULTCODE);
                            if (result_code == 1) {

                                AlertDialog.Builder alertbox = new AlertDialog.Builder(EditPrivateAddressPrimary.this);
                                alertbox.setMessage("Your address updated successfully.");
                                alertbox.setTitle("Success");

                                alertbox.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0,
                                                                int arg1) {

                                                finish();
                                            }
                                        });
                                alertbox.show();

                            }
                            else if (result_code == 0) {
                                new Message().showSnack(clytRootLayer,"This account has been deactivated.");
                            }
                            else if (result_code == -1) {
                                new Message().showSnack(clytRootLayer,"This account has been deleted.");
                            }
                            else if (result_code == -2) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }
                            else if (result_code == -3) {
                                new Message().showSnack(clytRootLayer,"No data found.");
                            }
                            else if (result_code == -5) {
                                new Message().showSnack(clytRootLayer,"This account has been blocked.");
                            }
                            else if (result_code == -6) {
                                new Message().showSnack(clytRootLayer,"Something went wrong. Please try after some time.");
                            }
                        }
                        catch (Exception e) {
                            UtilityClass.hideLoading(Loader,EditPrivateAddressPrimary.this);
                            new Message().showSnack(clytRootLayer, "Something went wrong ${e.message}.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    UtilityClass.hideLoading(Loader,EditPrivateAddressPrimary.this);
                    new Message().showSnack(clytRootLayer, "Something went wrong ${t.message}.");
                }
            });
        }
        else {
            new Message().showSnack(clytRootLayer, Constants.noInternetMessage);
        }


    }

}
