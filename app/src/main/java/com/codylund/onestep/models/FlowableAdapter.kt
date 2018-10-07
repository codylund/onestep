package com.codylund.onestep.models

import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

class FlowableAdapter<T: Any>(private val observable: Flowable<T>) : ObservableAdapter<T> {
    lateinit var observer: Disposable

    override fun subscribe(observerAdapter: ObserverAdapter<T>) {
        observer = observable.subscribe(observerAdapter::onSuccess, observerAdapter::onFailure)
    }

    override fun unsubscribe() {
        observer?.dispose()
    }
}