package com.codylund.onestep

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ObservableUtil {
    companion object {
        fun default(observable: Observable<Any>): Observable<Any> {
            observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
            return observable
        }
    }
}