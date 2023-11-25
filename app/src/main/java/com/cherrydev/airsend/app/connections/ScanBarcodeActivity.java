package com.cherrydev.airsend.app.connections;

import static com.cherrydev.airsend.app.connections.CameraMessageType.START;
import static com.cherrydev.airsend.app.connections.CameraMessageType.STOP;
import static com.cherrydev.airsend.app.connections.ResultType.START_CAMERA;
import static com.cherrydev.airsend.app.utils.permission.PermissionUtils.cameraRequest;
import static com.cherrydev.airsend.app.utils.permission.PermissionUtils.permissionGranted;

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


    private ObservableSubject<CameraMessage> observableMessage = new ObservableSubject<>();
    private SurfaceView surfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        initViews();
        checkPermissions();


        var holder = surfaceView.getHolder();
        startCamera(holder);
    }

    private void checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                observableMessage.setValue(new CameraMessage(START, surfaceView.getHolder()));
            } else {
                ActivityCompat.requestPermissions(ScanBarcodeActivity.this, cameraRequest.getPermissions(), cameraRequest.getCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (cameraRequest.getCode() == requestCode && permissionGranted(grantResults)) {
            observableMessage.setValue(new CameraMessage(START, surfaceView.getHolder()));
        }
    }


    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);


        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Timber.d("surfaceDestroyed");
                observableMessage.setValue(new CameraMessage(STOP));
            }

        };

        surfaceView.getHolder().addCallback(callback);


        //to get camera to show after onResume of fragment recreation. Without it "surfaceCreated" is never called.
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    private void startCamera(SurfaceHolder holder) {
        CameraThread cameraThread = new CameraThread(observableMessage.getObservable(), cameraResult ->
                runOnUiThread(() -> handleCameraResult(holder, cameraResult))
        );

        cameraThread.start();
    }

    private void handleCameraResult(SurfaceHolder holder, CameraResult cameraResult) {
        if (START_CAMERA == cameraResult.getResultType()) {
            holder.setFormat(PixelFormat.TRANSPARENT);

        } else {
            Intent intent = new Intent();
            intent.putExtra("RESULT", cameraResult.getText());
            setResult(Activity.RESULT_OK, intent);


            observableMessage.setValue(new CameraMessage(STOP));

            finish();
        }
    }

}
