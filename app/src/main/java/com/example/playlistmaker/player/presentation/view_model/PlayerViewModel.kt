package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.model.PlaylistsState
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.AddingTrackToPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private var track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    trackMapper: PlayerPresenterTrackMapper,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var trackInfo: PlayerTrackInfo = trackMapper.map(track)

    private val _playerScreenStateFlow = MutableStateFlow<PlayerScreenState>(
        PlayerScreenState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.NOT_PREPARED,
            curPosition = DEFAULT_CUR_POSITION,
        )
    )
    val playerScreenStateFlow = _playerScreenStateFlow.asStateFlow()

    private val addingTrackToPlaylistState = SingleEventLiveData<AddingTrackToPlaylistState>()
    fun observeAddingTrackToPlaylistState(): LiveData<AddingTrackToPlaylistState> =
        addingTrackToPlaylistState

    private var timerJob: Job? = null

    init {
        if (trackInfo.trackId != UNKNOWN_ID) audioPlayerInteractor.playerPrepare(
            trackInfo.previewUrl,
            { preparedCallback() },
            { completionCallback() })
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while ((_playerScreenStateFlow.value.trackPlaybackState) == PlaybackState.PLAYING
            ) {
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
                _playerScreenStateFlow.update {
                    PlayerScreenState(
                        isError = false,
                        trackInfo = trackInfo,
                        trackPlaybackState = PlaybackState.PLAYING,
                        curPosition = progressMap(audioPlayerInteractor.getCurrentPosition())
                    )
                }
            }
        }
    }

    private fun preparedCallback() {
        _playerScreenStateFlow.update {
            PlayerScreenState(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.PREPARED,
                curPosition = DEFAULT_CUR_POSITION
            )
        }
    }

    private fun completionCallback() {
        timerJob?.cancel()
        _playerScreenStateFlow.update {
            PlayerScreenState(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.PREPARED,
                curPosition = DEFAULT_CUR_POSITION
            )
        }
    }

    fun playerControl() {
        audioPlayerInteractor.playerControl(
            { playerStartCallback() },
            { playerPauseCallback() },
            { playerErrorCallback() }
        )
    }

    private fun playerStartCallback() {
        timerJob?.cancel()
        _playerScreenStateFlow.update {
            it.copy(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.PLAYING
            )
        }
        startTimer()
    }

    private fun playerPauseCallback() {
        timerJob?.cancel()
        _playerScreenStateFlow.update {
            it.copy(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.PAUSED
            )
        }
    }

    private fun playerErrorCallback() {
        timerJob?.cancel()
        _playerScreenStateFlow.update {
            PlayerScreenState(
                isError = true,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.NOT_PREPARED,
                curPosition = DEFAULT_CUR_POSITION
            )
        }
    }

    fun playerPause() {
        if ((_playerScreenStateFlow.value.trackPlaybackState == PlaybackState.PLAYING)
        ) {
            audioPlayerInteractor.playerPause { playerPauseCallback() }
            _playerScreenStateFlow.update {
                it.copy(
                    isError = false,
                    trackInfo = trackInfo,
                    trackPlaybackState = PlaybackState.PAUSED
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timerJob = null
        audioPlayerInteractor.playerRelease()
    }

    private fun progressMap(progress: Int): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(progress)
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

    companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
        private const val DEFAULT_CUR_POSITION = "00:00"
    }
}