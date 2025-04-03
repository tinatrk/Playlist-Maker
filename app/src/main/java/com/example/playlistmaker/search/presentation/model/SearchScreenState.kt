package com.example.playlistmaker.search.presentation.model

sealed class SearchScreenState {
    data object Default : SearchScreenState()
    data object History : SearchScreenState()
    data object EnteringRequest : SearchScreenState()
    data class Content(val tracks: List<SearchTrackInfo>) : SearchScreenState()
    data class Error(val errorType: ErrorTypePresenter) : SearchScreenState()
    data object Loading : SearchScreenState()
}