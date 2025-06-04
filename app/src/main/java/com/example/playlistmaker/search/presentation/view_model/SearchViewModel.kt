package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.mapper.SearchPresenterErrorTypeMapper
import com.example.playlistmaker.search.presentation.mapper.SearchPresenterTrackMapper
import com.example.playlistmaker.search.presentation.model.ErrorTypePresenter
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.model.SearchTrackInfo
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractorSearch: TrackInteractorSearch,
    private val trackInteractorHistory: TrackInteractorHistory
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Default)
    fun getScreenStateLiveData(): LiveData<SearchScreenState> = screenStateLiveData

    private var latestSearchRequest: String = STRING_DEF_VALUE

    private val historyTrackList: MutableList<Track> =
        trackInteractorHistory.getHistory().toMutableList()

    private val responseTrackList: MutableList<Track> = mutableListOf()

    private var isDisplayHistoryAllowed: Boolean = false

    private val onTrackClickDebounce: (Int) -> Unit

    private var searchJob: Job? = null

    init {
        onTrackClickDebounce =
            debounce<Int>(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { trackId ->
                val track = getTrackForPlayer(trackId)
                if (track != null) {
                    trackInteractorHistory.updateHistory(track)
                    historyTrackList.clear()
                    historyTrackList.addAll(trackInteractorHistory.getHistory())
                    screenStateLiveData.value =
                        SearchScreenState.OnTrackClickedEvent(trackId)
                }
            }
    }

    private fun getTracksInfo(tracks: List<Track>): List<SearchTrackInfo> {
        return tracks.map { track ->
            SearchPresenterTrackMapper.map(track)
        }
    }

    fun onSearchLineTextChanged(newSearchRequest: String) {
        val curSearchRequest = newSearchRequest.trim()
        if (curSearchRequest == latestSearchRequest) return
        latestSearchRequest = curSearchRequest

        searchJob?.cancel()

        if (curSearchRequest.isEmpty() && historyTrackList.isNotEmpty() && isDisplayHistoryAllowed) {
            screenStateLiveData.value = SearchScreenState.History(getTracksInfo(historyTrackList))
            return
        }
        if (curSearchRequest.isEmpty()) return

        screenStateLiveData.value = SearchScreenState.EnteringRequest
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            searchTrack(curSearchRequest)
        }
    }

    fun searchTrack(newSearchRequest: String? = null) {
        screenStateLiveData.postValue(SearchScreenState.Loading)
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
                    SearchScreenState.Content(setContent(foundTracks))
                )
            } else {
                responseTrackList.clear()
                renderState(SearchScreenState.Error(ErrorTypePresenter.EmptyResult))
            }
        } else {
            responseTrackList.clear()
            renderState(SearchScreenState.Error(SearchPresenterErrorTypeMapper.map(errorType)))
        }
    }

    private fun setContent(tracks: List<Track>): List<SearchTrackInfo> {
        responseTrackList.clear()
        responseTrackList.addAll(tracks)
        return tracks.map { SearchPresenterTrackMapper.map(it) }
    }

    private fun renderState(screenState: SearchScreenState) {
        screenStateLiveData.postValue(screenState)
    }

    fun clearHistory() {
        trackInteractorHistory.clearHistory()
        historyTrackList.clear()
        screenStateLiveData.value = SearchScreenState.Default
    }

    fun clearSearchRequest() {
        searchJob?.cancel()
        responseTrackList.clear()
        screenStateLiveData.value = SearchScreenState.Default
    }

    fun onTrackClick(trackId: Int) {
        onTrackClickDebounce(trackId)
    }

    private fun getTrackForPlayer(trackId: Int): Track? {
        responseTrackList.forEach {
            if (it.trackId == trackId)
                return it
        }

        historyTrackList.forEach {
            if (it.trackId == trackId) {
                return it
            }
        }
        return null
    }

    fun onSearchLineFocusChanged(isSearchLineInFocus: Boolean) {
        isDisplayHistoryAllowed = isSearchLineInFocus
        if (isSearchLineInFocus && latestSearchRequest.isEmpty() && historyTrackList.isNotEmpty()) {
            screenStateLiveData.value = SearchScreenState.History(getTracksInfo(historyTrackList))
        } else if (!isSearchLineInFocus && latestSearchRequest.isEmpty()) {
            screenStateLiveData.value = SearchScreenState.Default
        }
    }

    fun saveContentStateBeforeOpenPlayer() {
        if (latestSearchRequest.isNotEmpty() && responseTrackList.isNotEmpty()) {
            screenStateLiveData.value =
                SearchScreenState.Content(responseTrackList.map { SearchPresenterTrackMapper.map(it) })
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
        private const val STRING_DEF_VALUE = ""
    }
}