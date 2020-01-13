package com.codeapex.simplrpostprod.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.codeapex.simplrpostprod.R;
import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiClient;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.ApiInterface;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Parser;
import com.codeapex.simplrpostprod.UtilityClass.Api_utils.Response_Call_Back;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class Activity_QrCode extends Activity {

    private String public_or_private,address_id,direction_txt,qr_code_img,plus_code,address_unique_link,address ;
    String is_owner;
    private ImageView imgBarcode;
    private TextView tvPlusCode,tvAddressLink,tvDEscription,tvAddress;
    private ImageButton btn_back,btn_download;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_qr_code);

        findView();
        getIntentFromActivity();
    }

    private void findView() {
        tvPlusCode = findViewById(R.id.tvPlusCode);
        tvAddressLink = findViewById(R.id.tvAddressLink);
        tvDEscription = findViewById(R.id.tvDEscription);
        tvAddress = findViewById(R.id.tvAddress);
        imgBarcode = findViewById(R.id.imgBarcode);
        btn_back = findViewById(R.id.btn_back);
        btn_download = findViewById(R.id.btn_download);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });

        tvAddressLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvAddressLink!=null || !tvAddressLink.getText().toString().equals("")){
                    getAddress(tvAddressLink.getText().toString().trim());
                }
            }
        });
    }

    public void getAddress(String address){
        Parser.callApi(Activity_QrCode.this, "Please wait...", true, ApiClient.getClientPlusCode().create(ApiInterface.class).getAddressesByLink(address), new Response_Call_Back() {
            @Override
            public void getResponseFromServer(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getIntentFromActivity() {

        direction_txt = this.getIntent().getStringExtra("direction_txt");
        plus_code = this.getIntent().getStringExtra("plus_code");
        qr_code_img = this.getIntent().getStringExtra("qr_code_img");
        address_unique_link = this.getIntent().getStringExtra("address_unique_link");
        address = this.getIntent().getStringExtra("address");
        is_owner = this.getIntent().getStringExtra("is_owner");
        public_or_private = this.getIntent().getStringExtra("is_private");
        address_id = this.getIntent().getStringExtra("address_id");


        if (!qr_code_img.equals("")){
            Picasso.with(Activity_QrCode.this).load(Constants.IMG_URL + qr_code_img.replace("uploads/", "")).into(imgBarcode);
        }

        if (is_owner.equals("false")){

            if (public_or_private.equals("public")){
                btn_download.setVisibility(View.VISIBLE);
            }
        }



        tvPlusCode.setText("Plus code : "+plus_code);
        tvAddressLink.setText(address_unique_link);
        tvDEscription.setText("Text direction : "+direction_txt);
        tvAddress.setText("Address : "+address);
    }

    private void takeScreenshot() {

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString()+"/Download/"  + now + ".jpg";
            Log.e("LOCAL PATH:",""+mPath);
            /*String mPath = Environment.getExternalStorageDirectory().toString();
            new File(mPath + "/" + getString(R.string.app_name)).mkdirs();
*/
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);*/
        Toast.makeText(Activity_QrCode.this,"Downloaded successfully.",Toast.LENGTH_SHORT).show();
    }
}
