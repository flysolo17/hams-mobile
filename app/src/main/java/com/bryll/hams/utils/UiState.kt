package com.bryll.hams.utils

sealed class  UiState<out T> {
    object onLoading : UiState<Nothing>()
    data class onSuccess<out T>(val data:  T) : UiState<T>()
    data class onFailed(val message : String) : UiState<Nothing>()
}