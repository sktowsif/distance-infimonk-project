package com.project.distance.ext

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import com.project.distance.data.Outcome

inline fun <T> MutableLiveData<Outcome<T>>.loading() {
    value = Outcome.loading(true)
}

inline fun <T> MutableLiveData<Outcome<T>>.success(data: T) {
    value = Outcome.loading(false)
    value = Outcome.success(data)
}

inline fun <T> MutableLiveData<Outcome<T>>.failure(ex: Throwable) {
    value = Outcome.loading(false)
    value = Outcome.failure(ex)
}

suspend inline fun <T> LiveDataScope<Outcome<T>>.emitLoading() {
    emit(Outcome.loading(true))
}

suspend inline fun <T> LiveDataScope<Outcome<T>>.emitSuccess(data: T) {
    emit(Outcome.loading(false))
    emit(Outcome.success(data))
}

suspend inline fun <T> LiveDataScope<Outcome<T>>.emitFailure(ex: Throwable) {
    emit(Outcome.loading(false))
    emit(Outcome.failure(ex))
}