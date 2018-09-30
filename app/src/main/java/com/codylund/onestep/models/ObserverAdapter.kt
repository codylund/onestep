package com.codylund.onestep.models

interface ObserverAdapter<T: Any> {
    fun onSuccess(result: T)
    fun onFailure(throwable: Throwable)
}