package com.example.playlistmaker.favorites.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.presentation.models.FavoritesScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleEventLiveData
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val _screenStateFlow = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    //private val screenStateLiveData = MutableLiveData<FavoritesScreenState>()
    //fun observeScreenState(): LiveData<FavoritesScreenState> = screenStateLiveData

    private val _onTrackClickedLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickedLiveData(): LiveData<Track> = _onTrackClickedLiveData

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce<Track>(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            _onTrackClickedLiveData.value = track
        }

    fun updateFavoriteTracks() {
        renderState(FavoritesScreenState.Loading)

        viewModelScope.launch {
            favoritesInteractor.getAllFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun renderState(state: FavoritesScreenState) {
        _screenStateFlow.update {
            state
        }
        //screenStateLiveData.postValue(state)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) renderState(FavoritesScreenState.Empty)
        else renderState(FavoritesScreenState.Content(tracks))
    }

    fun onTrackClicked(track: Track) {
        onTrackClickDebounce(track)
    }

    companion object {
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
    }
}