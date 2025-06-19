package com.example.playlistmaker.favorites.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.presentation.models.FavoritesScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleEventLiveData
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<FavoritesScreenState>()
    fun observeScreenState(): LiveData<FavoritesScreenState> = screenStateLiveData

    private val onTrackClickedLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickedLiveData(): LiveData<Track> = onTrackClickedLiveData

    private val onTrackClickDebounce: (Track) -> Unit =
        debounce<Track>(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            onTrackClickedLiveData.value = track
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
        screenStateLiveData.postValue(state)
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