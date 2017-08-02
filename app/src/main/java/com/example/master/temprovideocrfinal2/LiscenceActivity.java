package com.example.master.temprovideocrfinal2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.master.temprovideocrfinal2.Extras.ValidationUtility;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class LiscenceActivity extends AppCompatActivity {

    private static final String TAG ="Liscence Activity" ;
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    TextView verificationOverlayGreen;
    final int RequestCameraPermissionID = 1001;
    String expiryDate = null, licenceNumber = null;

    /*
    Requesting Camera Permission And Starting camera Source
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case RequestCameraPermissionID:
            {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liscence);



        initViews();


        final TextRecognizer textRecognizer = new TextRecognizer.Builder(LiscenceActivity.this).build();

        if (!textRecognizer.isOperational()) {
            Log.w("Main", "Detector Dependecy are not yet available");
        }

        else
        {
            recognizeTextFromOCR(textRecognizer);
        }
    }


    /*
    Initialize Views
     */
    private void initViews()
    {
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        textView = (TextView) findViewById(R.id.txtView);
        verificationOverlayGreen=(TextView)findViewById(R.id.verificationOverlayGreen);
    }



    /*
    Recognize Text From Image using google cloud text Recognizer
     */
    public void recognizeTextFromOCR(TextRecognizer textRecognizer)
    {
        cameraSource = new CameraSource.Builder(LiscenceActivity.this, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LiscenceActivity.this,
                                new String []{Manifest.permission.CAMERA},RequestCameraPermissionID);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                final SparseArray<TextBlock> items =detections.getDetectedItems();
                if(items.size()!=0)
                {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder =new StringBuilder();
                            for(int i=0;i<items.size();++i)
                            {
                                TextBlock item=items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");

                            }

                            if(passItToNextScreen(stringBuilder.toString()))
                            {
                                textView.setBackgroundResource(R.drawable.border_style_after_scan);
                                verificationOverlayGreen.setBackgroundResource(R.drawable.border_style_after_scan)  ;
                                cameraSource.stop();

                                nextscreen();

                            }
                        }
                    });
                }

            }
        });
    }



    /*
    Pass Liscence Number and ExpiryDate to next Screen
     */
    private boolean passItToNextScreen(String text) {
        String[] strings = text.split("\n");

        if (strings !=null)
        {
            for (int i = 0; i < strings.length; i++) {
                String content = strings[i];
                if (ValidationUtility.licenceNumberValidation(content, true)) {
                    licenceNumber = content;
                }
                if (ValidationUtility.isThisDateValid(content)) {
                    expiryDate = content;
                }
            }
        }
        //check for true
        if(expiryDate!=null && licenceNumber!=null)
        {
            Log.d(TAG, "passItToNextScreen: Validation : true");
            //nextscreen();
            return true;
        }
        return false;
    }


    /*
    Function to create new intent and start new activity
     */

    private void nextscreen()
    {
        Intent intent = new Intent(this,RegistrationActivity.class);
        intent.putExtra("expiryDate",expiryDate);
        intent.putExtra("licenceNumber",licenceNumber);
        startActivity(intent);
    }

    /*
    Release Camera Source on Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }
}
