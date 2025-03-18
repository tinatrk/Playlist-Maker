package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.PlayerState
import com.example.playlistmaker.domain.api.interactor.AudioPlayerInterator
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInterator {
    override fun playerPrepare(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        repository.playerPrepare(resourceUrl, preparedCallback, completionCallback)
    }

    override fun playerControl(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        errorCallback: () -> Unit
    ) {
        val playerState = repository.getPlayerState()
        when (playerState) {
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                repository.playerStart()
                startCallback()
            }

            PlayerState.STATE_PLAYING -> {
                repository.playerPause()
                pauseCallback()
            }

            else -> errorCallback()
        }
    }

    override fun playerStart(startCallback: () -> Unit, errorCallback: () -> Unit) {
        val playerState = repository.getPlayerState()
        if  (playerState !=  PlayerState.STATE_DEFAULT) {
            repository.playerStart()
            startCallback()
        }
    }

    override fun playerPause(pauseCallback: () -> Unit, errorCallback: () -> Unit) {
        val playerState = repository.getPlayerState()
        if  (playerState !=  PlayerState.STATE_DEFAULT) {
            repository.playerPause()
            pauseCallback()
        }
    }

    override fun playerRelease() {
        repository.playerRelease()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(repository.getCurrentPosition())
    }

    companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
    }
}