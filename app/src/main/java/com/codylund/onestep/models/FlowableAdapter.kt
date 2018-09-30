package com.codylund.onestep.models

import io.reactivex.Flowable

class FlowableAdapter<T: Any>(private val observable: Flowable<T>) : ObservableAdapter<T> {
    override fun subscribe(observerAdapter: ObserverAdapter<T>) {
        observable.subscribe(observerAdapter::onSuccess, observerAdapter::onFailure)
    }
}