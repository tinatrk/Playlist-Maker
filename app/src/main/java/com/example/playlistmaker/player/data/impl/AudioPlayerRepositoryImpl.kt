package com.example.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.dto.PlayerStateDto
import com.example.playlistmaker.player.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerState

class AudioPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : AudioPlayerRepository {
    private var playerState = PlayerStateDto.STATE_DEFAULT

    override fun playerPrepare(
        resourceUrl: String, preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        mediaPlayer.setDataSource(resourceUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            preparedCallback()
            playerState = PlayerStateDto.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            completionCallback()
            playerState = PlayerStateDto.STATE_PREPARED
        }
    }

    override fun getPlayerState(): PlayerState {
        return when (playerState) {
            PlayerStateDto.STATE_DEFAULT -> PlayerState.STATE_DEFAULT
            PlayerStateDto.STATE_PREPARED -> PlayerState.STATE_PREPARED
            PlayerStateDto.STATE_PLAYING -> PlayerState.STATE_PLAYING
            PlayerStateDto.STATE_PAUSED -> PlayerState.STATE_PAUSED
        }
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerStart() {
        mediaPlayer.start()
        playerState = PlayerStateDto.STATE_PLAYING
    }

    override fun playerPause() {
        mediaPlayer.pause()
        playerState = PlayerStateDto.STATE_PAUSED
    }

    override fun playerStop() {
        mediaPlayer.pause()
        playerState = PlayerStateDto.STATE_PREPARED
    }

    override fun playerRelease() {
        mediaPlayer.reset()
    }

}