package com.cherrydev.airsendcore.utils;

import com.cherrydev.common.MyResult;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxHelper {

    public static <T> Single<MyResult<T>> submitToRx(Callable<MyResult<T>> callable) {
        Single<MyResult<T>> single = Single.fromCallable(callable);

        single.subscribeOn(Schedulers.io()).subscribe(s -> {
            //Timber.d("result " + s);
        });

        return single;
    }
}
