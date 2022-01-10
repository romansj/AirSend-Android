package com.cherrydev.airsend.app.utils.mymodels;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ObservableSubject<T> {
    //Got code here https://stackoverflow.com/questions/41707043/rxjava-implement-an-observable-for-an-object-update and changed

    //behavior subject "emits the most recently emitted item and all the subsequent items of the source Observable when an observer subscribes to it."
    // https://blog.mindorks.com/understanding-rxjava-subject-publish-replay-behavior-and-async-subject-224d663d452f

    //previously used "Publish Subject. It emits all the subsequent items of the source Observable at the time of subscription.", but it did not receive the first message.

    private BehaviorSubject<T> subject = BehaviorSubject.create();


    public void setValue(T value) {
        subject.onNext(value);
    }

    public Observable<T> getObservable() {
        return subject;
    }

}
