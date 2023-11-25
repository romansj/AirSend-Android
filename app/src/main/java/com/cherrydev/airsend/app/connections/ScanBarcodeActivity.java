package com.cherrydev.airsend.app.connections;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cherrydev.airsend.R;

import io.github.romansj.utils.ObservableSubject;

import timber.log.Timber;

public class ScanBarcodeActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ObservableSubject<CameraMessage> observableMessage = new ObservableSubject<>();
    private String[] cameraStringArray = new String[]{Manifest.permission.CAMERA};
    private SurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        initViews();

        try {
            if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                observableMessage.setValue(new CameraMessage(CameraMessage.Type.START, surfaceView.getHolder()));
            } else {
                ActivityCompat.requestPermissions(ScanBarcodeActivity.this, cameraStringArray, REQUEST_CAMERA_PERMISSION);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        var holder = surfaceView.getHolder();
        startCamera(holder);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CAMERA_PERMISSION == requestCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            observableMessage.setValue(new CameraMessage(CameraMessage.Type.START, surfaceView.getHolder()));
        }
    }


    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);


        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Timber.d("surfaceDestroyed");
                observableMessage.setValue(new CameraMessage(CameraMessage.Type.STOP));
            }

        };

        surfaceView.getHolder().addCallback(callback);


        //to get camera to show after onResume of fragment recreation. Without it "surfaceCreated" is never called.
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    private void startCamera(SurfaceHolder holder) {
        CameraThread cameraThread = new CameraThread(observableMessage.getObservable(), text -> runOnUiThread(() -> {
            //Timber.d("camera callback text: " + text);
            if (text.getResultType() == ResultType.START) {
                holder.setFormat(PixelFormat.TRANSPARENT);

            } else {
                Intent intent = new Intent();
                intent.putExtra("RESULT", text.getText());
                setResult(Activity.RESULT_OK, intent);


                observableMessage.setValue(new CameraMessage(CameraMessage.Type.STOP));

                finish();
            }
        }));


        cameraThread.start();
    }

}
