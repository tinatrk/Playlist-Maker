package com.example.playlistmaker.search.presentation.model

sealed class ErrorTypePresenter(message: String? = null) {
    data object NoNetworkConnection : ErrorTypePresenter()
    data class BadRequest(val message: String? = null) : ErrorTypePresenter(message)
    data class InternalServerError(val message: String? = null) : ErrorTypePresenter(message)
    data object EmptyResult : ErrorTypePresenter()
}