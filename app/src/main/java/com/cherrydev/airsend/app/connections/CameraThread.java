package com.cherrydev.airsend.app.connections;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import androidx.core.app.ActivityCompat;

import com.cherrydev.airsend.app.MyApplication;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class CameraThread extends Thread {
    private CameraCallback cameraCallback;
    private Disposable disposable;
    private Observable<CameraMessage> observable;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    public CameraThread(Observable<CameraMessage> observable, CameraCallback cameraCallback) {
        this.cameraCallback = cameraCallback;
        this.observable = observable;
    }


    @Override
    public void run() {
        initialiseDetectorsAndSources();


        disposable = observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(message -> {
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
