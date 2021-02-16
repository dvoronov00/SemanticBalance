package com.dvoronov00.semanticbalance.domain.model

sealed class DataState<out T>{
    class Loading<out T> : DataState<T>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Failure<out T>(val error: Throwable) : DataState<T>()
}