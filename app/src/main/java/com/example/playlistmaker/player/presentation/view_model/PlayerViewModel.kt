package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: Int,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    trackInteractorHistory: TrackInteractorHistory
) : ViewModel() {
    private val playerStateLiveData = MutableLiveData<PlayerState>()

    private val trackInfo: PlayerTrackInfo

    private var playerCurrentPosition: String = DEFAULT_CUR_POSITION

    private var timerJob: Job? = null

    init {
        val tracks = trackInteractorHistory.getHistory()
        val track: Track? = getTrackFromHistory(tracks)
        trackInfo = PlayerPresenterTrackMapper.map(track)
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.NOT_PREPARED,
            curPosition = playerCurrentPosition
        )

        if (track != null) audioPlayerInteractor.playerPrepare(trackInfo.previewUrl,
            { preparedCallback() },
            { completionCallback() })
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while ((playerStateLiveData.value?.trackPlaybackState
                    ?: PlaybackState.NOT_PREPARED) == PlaybackState.PLAYING
            ){
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
                playerCurrentPosition = progressMap(audioPlayerInteractor.getCurrentPosition())
                playerStateLiveData.postValue(
                    PlayerState(
                        isError = false,
                        trackInfo = trackInfo,
                        trackPlaybackState = PlaybackState.PLAYING,
                        curPosition = playerCurrentPosition))
            }
        }
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private fun getTrackFromHistory(tracks: List<Track>): Track? {
        tracks.forEach {
            if (it.trackId == trackId)
                return it
        }
        return null
    }

    private fun preparedCallback() {
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.PREPARED,
            curPosition = playerCurrentPosition
        )
    }

    private fun completionCallback() {
        timerJob?.cancel()
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.PREPARED,
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
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.PLAYING,
            curPosition = playerCurrentPosition
        )
        startTimer()
    }

    private fun playerPauseCallback() {
        timerJob?.cancel()
        playerStateLiveData.value = PlayerState(
            isError = false,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.PAUSED,
            curPosition = playerCurrentPosition
        )
    }

    private fun playerErrorCallback() {
        timerJob?.cancel()
        playerCurrentPosition = DEFAULT_CUR_POSITION
        playerStateLiveData.value = PlayerState(
            isError = true,
            trackInfo = trackInfo,
            trackPlaybackState = PlaybackState.NOT_PREPARED,
            curPosition = playerCurrentPosition
        )
    }

    fun playerPause() {
        if ((playerStateLiveData.value?.trackPlaybackState == PlaybackState.PLAYING)
        ) {
            audioPlayerInteractor.playerPause { playerPauseCallback() }
            playerStateLiveData.value = PlayerState(
                isError = false,
                trackInfo = trackInfo,
                trackPlaybackState = PlaybackState.PAUSED,
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

    companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
        private const val DEFAULT_CUR_POSITION = "00:00"
    }
}