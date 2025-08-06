package com.example.data.models

sealed class DataState<T> {

    data class Loading<T>(val isLoading: Boolean) : DataState<T>()

    data class Success<T>(val data: T) : DataState<T>()

    data class Error<T>(val error: Exception) : DataState<T>()
}