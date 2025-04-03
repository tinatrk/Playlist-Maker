package com.example.playlistmaker.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSearch
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

    private val historyStateLiveData = MutableLiveData<List<Track>>(listOf())
    fun getHistoryStateLiveData(): LiveData<List<SearchTrackInfo>> =
        getTrackInfoLiveData(historyStateLiveData)

    private val responseTrackListLiveData = MutableLiveData<List<Track>>(listOf())

    private var isOnTrackClickAllowed: Boolean = true

    private var isDisplayHistoryAllowed: Boolean = false

    private fun getTrackInfoLiveData(trackLiveData: MutableLiveData<List<Track>>): LiveData<List<SearchTrackInfo>> {
        return trackLiveData.map { list ->
            list.map { track ->
                SearchPresenterTrackMapper.map(
                    track
                )
            }
        }
    }

    fun onSearchLineTextChanged(newSearchRequest: String) {
        if (newSearchRequest == latestSearchRequest) return
        latestSearchRequest = newSearchRequest

        if (newSearchRequest.isEmpty() && historyStateLiveData.value?.isNotEmpty() == true && isDisplayHistoryAllowed) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            screenStateLiveData.postValue(SearchScreenState.History)
            return
        }
        if (newSearchRequest.isEmpty()) return

        screenStateLiveData.postValue(SearchScreenState.EnteringRequest)
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
        responseTrackListLiveData.postValue(tracks)
        return tracks.map { SearchPresenterTrackMapper.map(it) }
    }

    private fun renderState(screenState: SearchScreenState) {
        screenStateLiveData.postValue(screenState)
    }

    fun clearHistory() {
        trackInteractorHistory.clearHistory()
        historyStateLiveData.value = listOf()
        screenStateLiveData.value = SearchScreenState.Default
    }

    fun clearSearchRequest() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        responseTrackListLiveData.value = listOf()
        screenStateLiveData.value = SearchScreenState.Default
    }

    fun getHistory() {
        historyStateLiveData.value = trackInteractorHistory.getHistory()
    }

    fun onTrackClick(trackId: Int): Boolean {
        if (onTrackClickDebounce()) {
            val track = getTrackForPlayer(trackId)
            if (track != null) {
                trackInteractorHistory.updateHistory(track)
                historyStateLiveData.value = trackInteractorHistory.getHistory()
                return true
            }
        }
        return false
    }

    private fun getTrackForPlayer(trackId: Int): Track? {
        responseTrackListLiveData.value?.forEach {
            if (it.trackId == trackId)
                return it
        }

        historyStateLiveData.value?.forEach {
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
        if (isSearchLineInFocus && latestSearchRequest.isEmpty() && historyStateLiveData.value?.isNotEmpty() == true)
            screenStateLiveData.value = SearchScreenState.History
        else if (!isSearchLineInFocus && latestSearchRequest.isEmpty())
            screenStateLiveData.value = SearchScreenState.Default
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    CreatorSearch.provideTrackInteractorSearch(),
                    CreatorHistory.provideTrackInteractorHistory()
                )
            }
        }

        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
        private const val STRING_DEF_VALUE = ""
    }
}