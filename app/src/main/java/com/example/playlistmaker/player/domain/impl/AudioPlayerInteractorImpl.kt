package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerState

class AudioPlayerInteractorImpl(private val audioPlayerRepository: AudioPlayerRepository) :
    AudioPlayerInteractor {
    override fun playerPrepare(
        resourceUrl: String,
        preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        audioPlayerRepository.playerPrepare(resourceUrl, preparedCallback, completionCallback)
    }

    override fun playerControl(
        startCallback: () -> Unit,
        pauseCallback: () -> Unit,
        errorCallback: () -> Unit
    ) {
        val playerState = audioPlayerRepository.getPlayerState()
        when (playerState) {
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                audioPlayerRepository.playerStart()
                startCallback()
            }

            PlayerState.STATE_PLAYING -> {
                audioPlayerRepository.playerPause()
                pauseCallback()
            }

            else -> {
                errorCallback()
            }
        }
    }

    override fun playerStart(startCallback: () -> Unit) {
        val playerState = audioPlayerRepository.getPlayerState()
        if (playerState != PlayerState.STATE_DEFAULT) {
            audioPlayerRepository.playerStart()
            startCallback()
        }
    }

    override fun playerPause(pauseCallback: () -> Unit) {
        val playerState = audioPlayerRepository.getPlayerState()
        if (playerState != PlayerState.STATE_DEFAULT) {
            audioPlayerRepository.playerPause()
            pauseCallback()
        }
    }

    override fun playerRelease() {
        audioPlayerRepository.playerRelease()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerRepository.getCurrentPosition()
    }
}