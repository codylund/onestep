package com.codylund.onestep.models

interface ObservableAdapter<T: Any> {
    fun subscribe(observerAdapter: ObserverAdapter<T>)
}