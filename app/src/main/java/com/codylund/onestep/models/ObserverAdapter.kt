package com.codylund.onestep.models

interface ObserverAdapter<T: Any> {
    fun onSubscribe(observable: ObservableAdapter<T>)
    fun onSuccess(result: T)
    fun onFailure(throwable: Throwable)
}