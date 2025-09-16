package com.example.playlistmaker.search.presentation.model

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track

sealed class SearchScreenState(val searchText: String = "") {
    data object Default : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()
    data class EnteringRequest(val text: String) : SearchScreenState(text)
    data class Content(val text: String, val tracks: List<Track>) : SearchScreenState(text)
    data class Error(val text: String, val errorType: ErrorType) : SearchScreenState(text)
    data class Loading(val text: String) : SearchScreenState(text)
}