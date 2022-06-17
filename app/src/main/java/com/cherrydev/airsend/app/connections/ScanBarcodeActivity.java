package com.cherrydev.airsend.app.connections;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsendcore.utils.ObservableSubject;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

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

        });


        //to get camera to show after onResume of fragment recreation. Without it "surfaceCreated" is never called.
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);


    }

    private void startCamera(SurfaceHolder holder) {
        CameraThread cameraThread = new CameraThread(text -> runOnUiThread(() -> {
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

    public enum ResultType {TEXT, START}

    public class CameraResult {
        String text;
        ResultType resultType;

        public CameraResult(String text, ResultType resultType) {
            this.text = text;
            this.resultType = resultType;
        }

        public CameraResult(String text) {
            this.text = text;
            this.resultType = ResultType.TEXT;
        }

        public String getText() {
            return text;
        }

        public ResultType getResultType() {
            return resultType;
        }
    }

    public interface CameraCallback {
        void sendData(CameraResult result);
    }

    public class CameraThread extends Thread {
        CameraCallback cameraCallback;
        Disposable disposable;


        public CameraThread(CameraCallback cameraCallback) {
            this.cameraCallback = cameraCallback;
        }

        private BarcodeDetector barcodeDetector;
        private CameraSource cameraSource;

        @Override
        public void run() {
            initialiseDetectorsAndSources();


            disposable = observableMessage.getObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(message -> {
                Timber.d("subscribe, new mssg " + message.getMessage());

                switch (message.getType()) {
                    case START:
                        if (ActivityCompat.checkSelfPermission(MyApplication.getInstance().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return;
                        cameraSource.start(message.getHolder());
                        break;

                    case PAUSE:
                        break;

                    case STOP:
                        doDisposal();
                        if (cameraSource != null) cameraSource.release();

                        break;

                    default:
                        break;
                }


            }, error -> {
                error.printStackTrace();
            });
        }

        void doDisposal() {
            if (disposable != null) disposable.dispose();
        }

        private void initialiseDetectorsAndSources() {
            Timber.d("Barcode scanner started");


            barcodeDetector = new BarcodeDetector.Builder(MyApplication.getInstance().getApplicationContext())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

            cameraSource = new CameraSource.Builder(MyApplication.getInstance().getApplicationContext(), barcodeDetector)
                    .setRequestedPreviewSize(1000, 1000)
                    .setAutoFocusEnabled(true)
                    .build();


            barcodeDetector.setProcessor(new Detector.Processor<>() {
                @Override
                public void release() {
                    Timber.d("Released barcode scanner resources");
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {

                        String intentData = barcodes.valueAt(0).displayValue;
                        cameraCallback.sendData(new CameraResult(intentData));
                    }
                }
            });
        }
    }
}
