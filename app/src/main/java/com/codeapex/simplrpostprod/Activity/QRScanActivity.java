package com.codeapex.simplrpostprod.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.codeapex.simplrpostprod.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);


        ActivityCompat.requestPermissions(QRScanActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
//        TextView text = (TextView) findViewById(R.id.someText);
//        text.setText(result.getText());
        String code = result.getText();
        Log.d(TAG, "handleResult: "+code);

        Intent intent = new Intent();
        intent.putExtra("code", result.getText());
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    Toast.makeText(QRScanActivity.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        }
//    }

}
