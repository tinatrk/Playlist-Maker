package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    trackMapper: PlayerPresenterTrackMapper,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val trackInfo: PlayerTrackInfo = trackMapper.map(track)

    private var playerCurrentPosition: String = DEFAULT_CUR_POSITION

    private var playerCurrentState: PlaybackState = PlaybackState.NOT_PREPARED

    private var timerJob: Job? = null

    init {
        playerStateLiveData.value =
            PlayerState(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = playerCurrentState,
                curPosition = playerCurrentPosition,
            )

        if (trackInfo.trackId != UNKNOWN_ID) audioPlayerInteractor.playerPrepare(
            trackInfo.previewUrl,
            { preparedCallback() },
            { completionCallback() })
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while ((playerStateLiveData.value?.trackPlaybackState
                    ?: PlaybackState.NOT_PREPARED) == PlaybackState.PLAYING
            ) {
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
                playerCurrentPosition = progressMap(audioPlayerInteractor.getCurrentPosition())
                playerCurrentState = PlaybackState.PLAYING
                playerStateLiveData.value =
                    PlayerState(
                        isError = false,
                        trackInfo = trackInfo,
                        trackPlaybackState = playerCurrentState,
                        curPosition = playerCurrentPosition
                    )

            }
        }
    }

    private fun preparedCallback() {
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerCurrentState = PlaybackState.PREPARED
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = playerCurrentState,
            curPosition = playerCurrentPosition
        )
    }

    private fun completionCallback() {
        timerJob?.cancel()
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerCurrentState = PlaybackState.PREPARED
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = playerCurrentState,
            curPosition = playerCurrentPosition
        )
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
        playerCurrentState = PlaybackState.PLAYING
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = playerCurrentState,
            curPosition = playerCurrentPosition
        )
        startTimer()
    }

    private fun playerPauseCallback() {
        timerJob?.cancel()
        playerCurrentState = PlaybackState.PAUSED
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = playerCurrentState,
            curPosition = playerCurrentPosition
        )
    }

    private fun playerErrorCallback() {
        timerJob?.cancel()
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerCurrentState = PlaybackState.NOT_PREPARED
        playerStateLiveData.value = PlayerState(
            isError = true,
            trackInfo = trackInfo,
            trackPlaybackState = playerCurrentState,
            curPosition = playerCurrentPosition
        )
    }

    fun playerPause() {
        if ((playerStateLiveData.value?.trackPlaybackState == PlaybackState.PLAYING)
        ) {
            audioPlayerInteractor.playerPause { playerPauseCallback() }
            playerCurrentState = PlaybackState.PAUSED
            playerStateLiveData.value = PlayerState(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = playerCurrentState,
                curPosition = playerCurrentPosition
            )
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
            track.isFavorite = !track.isFavorite
            trackInfo.isFavorite = track.isFavorite

            playerStateLiveData.postValue(
                PlayerState(
                    isError = false,
                    trackInfo = trackInfo,
                    trackPlaybackState = playerCurrentState,
                    curPosition = playerCurrentPosition
                )
            )
        }
    }

    companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
        private const val DEFAULT_CUR_POSITION = "00:00"
    }
}