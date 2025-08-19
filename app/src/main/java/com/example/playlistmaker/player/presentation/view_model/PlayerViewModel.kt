package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.model.PlaylistsState
import com.example.playlistmaker.player.services.MediaPlayerControl
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.AddingTrackToPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private var track: Track,
    trackMapper: PlayerPresenterTrackMapper,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var trackInfo: PlayerTrackInfo = trackMapper.map(track)

    private val _playerScreenStateFlow = MutableStateFlow<PlayerScreenState>(
        PlayerScreenState(
            isError = false,
            trackInfo = trackInfo,
            playerState = PlayerState.Default()
        )
    )
    val playerScreenStateFlow = _playerScreenStateFlow.asStateFlow()

    private val addingTrackToPlaylistState = SingleEventLiveData<AddingTrackToPlaylistState>()
    fun observeAddingTrackToPlaylistState(): LiveData<AddingTrackToPlaylistState> =
        addingTrackToPlaylistState

    private var mediaPlayerControl: MediaPlayerControl? = null
    private var mediaPlayerControlJob: Job? = null

    fun setMediaPlayerControl(mediaPlayerControl: MediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl
        mediaPlayerControlJob = viewModelScope.launch {
            mediaPlayerControl.getPlayerState().collect { newPlayerState ->
                _playerScreenStateFlow.update {
                    it.copy(
                        isError = false,
                        playerState = newPlayerState
                    )
                }
            }
        }
    }

    fun removeMediaPlayerControl() {
        mediaPlayerControlJob?.cancel()
        mediaPlayerControl = null
    }

    fun playerControl() {
        when (playerScreenStateFlow.value.playerState) {
            is PlayerState.Prepared, is PlayerState.Paused -> {
                mediaPlayerControl?.startPlayer()
            }

            is PlayerState.Playing -> {
                mediaPlayerControl?.pausePlayer()
            }

            else -> {}
        }
    }

    fun onComponentStop() {
        if (playerScreenStateFlow.value.playerState is PlayerState.Playing)
            mediaPlayerControl?.startForeground()
    }

    fun onComponentStart() {
        stopForeground()
    }

    private fun stopForeground() {
        if (playerScreenStateFlow.value.playerState is PlayerState.Playing)
            mediaPlayerControl?.stopForeground()
    }

    override fun onCleared() {
        super.onCleared()
        stopForeground()
        mediaPlayerControl = null
    }

    fun onFavoriteClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            if (track.isFavorite) {
                favoritesInteractor.deleteFavoriteTrack(track)
            } else {
                favoritesInteractor.saveFavoriteTrack(track)
            }
            track = track.copy(isFavorite = !track.isFavorite)
            trackInfo = trackInfo.copy(isFavorite = track.isFavorite)

            _playerScreenStateFlow.update {
                it.copy(
                    isError = false,
                    trackInfo = trackInfo
                )
            }
        }
    }

    fun btnAddTrackToPlaylistClicked() {
        _playerScreenStateFlow.update {
            it.copy(playlistsState = PlaylistsState.Loading)
        }
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) _playerScreenStateFlow.update {
                    it.copy(playlistsState = PlaylistsState.Empty)
                } else _playerScreenStateFlow.update {
                    it.copy(playlistsState = PlaylistsState.Content(playlists))
                }
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.tracksIds.contains(track.trackId)) {
            addingTrackToPlaylistState.value =
                AddingTrackToPlaylistState.AlreadyExists(playlist.title)
        } else {
            viewModelScope.launch {
                playlistInteractor.addNewTrackToPlaylist(playlist, track)
                addingTrackToPlaylistState.value =
                    AddingTrackToPlaylistState.SuccessAdding(playlist.title, playlist.coverPath)
            }
        }
    }
}