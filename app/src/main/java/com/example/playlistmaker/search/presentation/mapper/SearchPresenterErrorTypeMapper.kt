package com.example.playlistmaker.search.presentation.mapper

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.presentation.model.ErrorTypePresenter

object SearchPresenterErrorTypeMapper {
    fun map(errorType: ErrorType?): ErrorTypePresenter {
        if (errorType == null) return ErrorTypePresenter.BadRequest()

        return when (errorType) {
            is ErrorType.NoNetworkConnection -> ErrorTypePresenter.NoNetworkConnection
            is ErrorType.EmptyResult -> ErrorTypePresenter.EmptyResult
            is ErrorType.BadRequest -> ErrorTypePresenter.BadRequest(errorType.message)
            is ErrorType.InternalServerError -> ErrorTypePresenter.InternalServerError(errorType.message)
        }
    }
}