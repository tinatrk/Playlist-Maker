package com.example.playlistmaker.search.presentation.model

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track

sealed class SearchScreenState {
    data object Default : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()
    data object EnteringRequest : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    data class Error(val errorType: ErrorType) : SearchScreenState()
    data object Loading : SearchScreenState()
    data class OnTrackClickedEvent(val track: Track) : SearchScreenState()
}