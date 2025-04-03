package com.example.playlistmaker.player.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.CreatorAudioPlayer
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: Int,
    private val playerInteractor: AudioPlayerInteractor,
    historyInteractor: TrackInteractorHistory
) : ViewModel() {
    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState.NotPrepared)

    private val contentStateLiveData = MutableLiveData<PlayerTrackInfo>()

    private val handler = Handler(Looper.getMainLooper())
    private var playerCurrentPosition: String = STRING_DEF_VALUE
    private val getCurrentPosition = object : Runnable {
        override fun run() {
            playerCurrentPosition = progressMap(playerInteractor.getCurrentPosition())
            playerStateLiveData.postValue(PlayerState.Progress(playerCurrentPosition))
            handler.postDelayed(this, SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
        }
    }

    init {
        val tracks = historyInteractor.getHistory()
        val track: Track? = getTrackFromHistory(tracks)
        val trackInfo: PlayerTrackInfo = PlayerPresenterTrackMapper.map(track)
        contentStateLiveData.value = trackInfo

        if (track != null) playerInteractor.playerPrepare(trackInfo.previewUrl,
            { preparedCallback() },
            { completionCallback() })
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    fun getContentState(): LiveData<PlayerTrackInfo> = contentStateLiveData

    private fun getTrackFromHistory(tracks: List<Track>): Track? {
        tracks.forEach {
            if (it.trackId == trackId)
                return it
        }
        return null
    }

    private fun preparedCallback() {
        playerStateLiveData.value = PlayerState.Prepared
    }

    private fun completionCallback() {
        handler.removeCallbacks(getCurrentPosition)
        playerStateLiveData.value = PlayerState.Prepared
    }

    fun playerControl() {
        playerInteractor.playerControl(
            { playerStartCallback() },
            { playerPauseCallback() },
            { playerErrorCallback() }
        )
    }

    private fun playerStartCallback() {
        handler.removeCallbacks(getCurrentPosition)
        playerStateLiveData.value = PlayerState.Playing
        handler.post(getCurrentPosition)
    }

    private fun playerPauseCallback() {
        handler.removeCallbacks(getCurrentPosition)
        playerStateLiveData.value = PlayerState.Paused(playerCurrentPosition)
    }

    private fun playerErrorCallback() {
        handler.removeCallbacks(getCurrentPosition)
        playerStateLiveData.value = PlayerState.Error
    }

    fun playerPause() {
        handler.removeCallbacks(getCurrentPosition)
        playerInteractor.playerPause { playerPauseCallback() }
        playerStateLiveData.value = PlayerState.Paused(playerCurrentPosition)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(getCurrentPosition)
        playerInteractor.playerRelease()
    }

    private fun progressMap(progress: Int): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(progress)
    }

    companion object {
        fun getViewModelFactory(trackId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    trackId,
                    CreatorAudioPlayer.provideAudioPlayerInteractor(),
                    CreatorHistory.provideTrackInteractorHistory()
                )
            }
        }

        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 500L
        private const val STRING_DEF_VALUE = ""
    }
}