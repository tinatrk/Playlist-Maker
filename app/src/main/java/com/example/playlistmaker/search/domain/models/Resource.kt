package com.example.playlistmaker.search.domain.models

sealed class Resource<T>(val data: T? = null, val errorType: ErrorType? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Empty<T>() : Resource<T>()
    class Error<T>(errorType: ErrorType, data: T? = null) : Resource<T>(data, errorType)
}