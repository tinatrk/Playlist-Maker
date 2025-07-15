package com.example.playlistmaker.playlists.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.presentation.models.PlaylistsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _screenStateFlow =
        MutableStateFlow<PlaylistsScreenState>(PlaylistsScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    fun updatePlaylists() {
        renderState(PlaylistsScreenState.Loading)

        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        renderState(PlaylistsScreenState.Empty)
                    } else {
                        renderState(PlaylistsScreenState.Content(playlists))
                    }
                }
        }
    }

    private fun renderState(state: PlaylistsScreenState) {
        _screenStateFlow.update {
            state
        }
    }
}