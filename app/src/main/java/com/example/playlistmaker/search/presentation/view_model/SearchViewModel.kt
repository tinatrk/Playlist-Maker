package com.example.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.mapper.SearchPresenterErrorTypeMapper
import com.example.playlistmaker.search.presentation.mapper.SearchPresenterTrackMapper
import com.example.playlistmaker.search.presentation.model.ErrorTypePresenter
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.model.SearchTrackInfo

class SearchViewModel(
    private val trackInteractorSearch: TrackInteractorSearch,
    private val trackInteractorHistory: TrackInteractorHistory
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val screenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Default)
    fun getScreenStateLiveData(): LiveData<SearchScreenState> = screenStateLiveData

    private var latestSearchRequest: String = STRING_DEF_VALUE

    private val historyTrackList: MutableList<Track> =
        trackInteractorHistory.getHistory().toMutableList()

    private val responseTrackList: MutableList<Track> = mutableListOf()

    private var isOnTrackClickAllowed: Boolean = true

    private var isDisplayHistoryAllowed: Boolean = false

    private fun getTracksInfo(tracks: List<Track>): List<SearchTrackInfo> {
        return tracks.map { track ->
            SearchPresenterTrackMapper.map(track)
        }
    }

    fun onSearchLineTextChanged(newSearchRequest: String) {
        if (newSearchRequest == latestSearchRequest) return
        latestSearchRequest = newSearchRequest

        if (newSearchRequest.isEmpty() && historyTrackList.isNotEmpty() && isDisplayHistoryAllowed) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            screenStateLiveData.value = SearchScreenState.History(getTracksInfo(historyTrackList))
            return
        }
        if (newSearchRequest.isEmpty()) return

        screenStateLiveData.value = SearchScreenState.EnteringRequest
        searchDebounce(newSearchRequest)
    }

    private fun searchDebounce(newSearchRequest: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTrack(newSearchRequest) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY_MILLIS

        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    fun searchTrack(newSearchRequest: String? = null) {
        screenStateLiveData.postValue(SearchScreenState.Loading)

        trackInteractorSearch.searchTracks(
            (newSearchRequest ?: latestSearchRequest).trim(),
            object : TrackInteractorSearch.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                    processSearchResult(
                        foundTracks,
                        errorType,
                        newSearchRequest ?: latestSearchRequest
                    )
                }
            })
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
                renderState(SearchScreenState.Error(ErrorTypePresenter.EmptyResult))
            }
        } else {
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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        responseTrackList.clear()
        screenStateLiveData.value = SearchScreenState.Default
    }

    fun onTrackClick(trackId: Int) {
        if (onTrackClickDebounce()) {
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

    private fun onTrackClickDebounce(): Boolean {
        val currentState: Boolean = isOnTrackClickAllowed
        if (isOnTrackClickAllowed) {
            isOnTrackClickAllowed = false
            handler.postDelayed({ isOnTrackClickAllowed = true }, ON_TRACK_CLICK_DELAY_MILLIS)
        }
        return currentState
    }

    fun onSearchLineFocusChanged(isSearchLineInFocus: Boolean) {
        isDisplayHistoryAllowed = isSearchLineInFocus
        if (isSearchLineInFocus && latestSearchRequest.isEmpty() && historyTrackList.isNotEmpty())
            screenStateLiveData.value = SearchScreenState.History(getTracksInfo(historyTrackList))
        else if (!isSearchLineInFocus && latestSearchRequest.isEmpty())
            screenStateLiveData.value = SearchScreenState.Default
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
        private const val STRING_DEF_VALUE = ""
    }
}