package com.examle.data.models

sealed class DataState<T> {

    data class Loading<T>(val isLoading: Boolean) : DataState<T>()

    data class Success<T>(val data: T) : DataState<T>()

    data class Error<T>(val error: Exception) : DataState<T>()
}

sealed class ResponseState{

    data object Loading : ResponseState()

    data class Success<T>(val data: T) : ResponseState()

    data class Error(val error: Exception) : ResponseState()
}

