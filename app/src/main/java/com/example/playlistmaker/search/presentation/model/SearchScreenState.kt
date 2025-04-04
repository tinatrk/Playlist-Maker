package com.example.playlistmaker.search.presentation.model

sealed class SearchScreenState {
    data object Default : SearchScreenState()
    data class History(val tracks: List<SearchTrackInfo>) : SearchScreenState()
    data object EnteringRequest : SearchScreenState()
    data class Content(val tracks: List<SearchTrackInfo>) : SearchScreenState()
    data class Error(val errorType: ErrorTypePresenter) : SearchScreenState()
    data object Loading : SearchScreenState()
    data class OnTrackClickedEvent(val trackId: Int) : SearchScreenState()
}