package com.example.playlistmaker.search.domain.models

sealed class ErrorType(message: String? = null) {
    data object NoNetworkConnection : ErrorType()
    data class BadRequest(val message: String? = null) : ErrorType(message)
    data class InternalServerError(val message: String? = null) : ErrorType(message)
    data object EmptyResult : ErrorType()
}