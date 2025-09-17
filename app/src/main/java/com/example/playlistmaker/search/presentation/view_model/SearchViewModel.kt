package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.model.NavigationEvent
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractorSearch: TrackInteractorSearch,
    private val trackInteractorHistory: TrackInteractorHistory,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _screenStateFlow = MutableStateFlow<SearchScreenState>(SearchScreenState.Default)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var latestSearchRequest: String = STRING_DEF_VALUE

    private val historyTrackList: MutableList<Track> = mutableListOf()

    private val responseTrackList: MutableList<Track> = mutableListOf()

    private var isDisplayHistoryAllowed: Boolean = false

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce<Track>(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            viewModelScope.launch(Dispatchers.IO) {
                trackInteractorHistory.updateHistory(track)
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.OpenAudioPlayer(track))
                }
            }
        }

    private var searchJob: Job? = null

    fun onSearchLineTextChanged(newSearchRequest: String) {
        val curSearchRequest = newSearchRequest.trim()
        if (curSearchRequest == latestSearchRequest) return
        latestSearchRequest = curSearchRequest

        searchJob?.cancel()

        if (curSearchRequest.isEmpty() && historyTrackList.isNotEmpty() && isDisplayHistoryAllowed) {
            _screenStateFlow.update { SearchScreenState.History(tracks = historyTrackList) }
            return
        }
        if (curSearchRequest.isEmpty()) return

        _screenStateFlow.update { SearchScreenState.EnteringRequest(latestSearchRequest) }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            searchTrack(curSearchRequest)
        }
    }

    fun searchTrack(newSearchRequest: String? = null) {
        _screenStateFlow.update { SearchScreenState.Loading(text = newSearchRequest ?: "") }
        val curSearchText = (newSearchRequest ?: latestSearchRequest)

        viewModelScope.launch {
            trackInteractorSearch.searchTracks(curSearchText)
                .collect { pair ->
                    processSearchResult(
                        foundTracks = pair.first,
                        errorType = pair.second,
                        curSearchText
                    )
                }
        }
    }

    private fun processSearchResult(
        foundTracks: List<Track>?,
        errorType: ErrorType?,
        newSearchRequest: String
    ) {
        if (newSearchRequest != latestSearchRequest) return

        if (foundTracks != null) {
            if (foundTracks.isNotEmpty()) {
                renderState(
                    SearchScreenState.Content(
                        tracks = setContent(foundTracks),
                        text = latestSearchRequest
                    )
                )
            } else {
                responseTrackList.clear()
                renderState(
                    SearchScreenState.Error(
                        errorType = ErrorType.EmptyResult,
                        text = latestSearchRequest
                    )
                )
            }
        } else {
            responseTrackList.clear()
            renderState(
                SearchScreenState.Error(
                    errorType = errorType ?: ErrorType.BadRequest(),
                    text = latestSearchRequest
                )
            )
        }
    }

    private fun setContent(tracks: List<Track>): List<Track> {
        responseTrackList.clear()
        responseTrackList.addAll(tracks)
        return tracks
    }

    private fun renderState(screenState: SearchScreenState) {
        _screenStateFlow.update { screenState }
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            trackInteractorHistory.clearHistory()
            historyTrackList.clear()
            _screenStateFlow.update { SearchScreenState.Default }
        }
    }

    fun clearSearchRequest() {
        searchJob?.cancel()
        responseTrackList.clear()
        latestSearchRequest = ""
        _screenStateFlow.update { SearchScreenState.Default }
    }

    fun onTrackClicked(track: Track) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.Default)
        }
        onTrackClickDebounce(track)
    }

    fun onSearchLineFocusChanged(isSearchLineInFocus: Boolean) {
        isDisplayHistoryAllowed = isSearchLineInFocus

        if (isSearchLineInFocus && latestSearchRequest.isEmpty() && historyTrackList.isNotEmpty()) {
            _screenStateFlow.update { SearchScreenState.History(tracks = historyTrackList) }
        } else if (!isSearchLineInFocus && latestSearchRequest.isEmpty()) {
            _screenStateFlow.update { SearchScreenState.Default }
        }
    }

    fun updateHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            trackInteractorHistory.getHistory().collect { tracks ->
                historyTrackList.clear()
                historyTrackList.addAll(tracks)
            }
        }
    }

    fun updateSearchResults() {
        markFavoriteSearchTracks()
    }

    private fun markFavoriteSearchTracks() {
        if (_screenStateFlow.value is SearchScreenState.Content) {
            viewModelScope.launch(Dispatchers.IO) {
                favoritesInteractor.markFavoriteTracks(
                    responseTrackList
                ).collect { markedTracks ->
                    renderState(
                        SearchScreenState.Content(
                            tracks = markedTracks,
                            text = latestSearchRequest
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
        private const val STRING_DEF_VALUE = ""
    }
}