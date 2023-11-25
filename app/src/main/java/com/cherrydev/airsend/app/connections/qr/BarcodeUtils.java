package com.cherrydev.airsend.app.connections.qr;

import android.graphics.Bitmap;

import net.glxn.qrgen.android.QRCode;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class BarcodeUtils {

    public static Single<Bitmap> getBitmap(String str) {
        return Single.fromCallable(() -> getBitmapInternal(str)).subscribeOn(Schedulers.io());
    }

    private static Bitmap getBitmapInternal(String str) {
        Timber.d("getBitmapInternal::thread= " + Thread.currentThread().getName());

        Bitmap myBitmap = QRCode.from(str).withSize(400, 400).bitmap();
        return myBitmap;
    }


}
