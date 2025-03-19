package com.example.playlistmaker.data.audioPlayer

import android.media.MediaPlayer
import com.example.playlistmaker.data.dto.PlayerStateDto
import com.example.playlistmaker.domain.PlayerState
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private val player: MediaPlayer) : AudioPlayerRepository {
    private var playerState = PlayerStateDto.STATE_DEFAULT

    override fun playerPrepare(
        resourceUrl: String, preparedCallback: () -> Unit,
        completionCallback: () -> Unit
    ) {
        player.setDataSource(resourceUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            preparedCallback()
            playerState = PlayerStateDto.STATE_PREPARED
        }
        player.setOnCompletionListener {
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
        return player.currentPosition
    }

    override fun playerStart() {
        player.start()
        playerState = PlayerStateDto.STATE_PLAYING
    }

    override fun playerPause() {
        player.pause()
        playerState = PlayerStateDto.STATE_PAUSED
    }

    override fun playerStop() {
        player.pause()
        playerState = PlayerStateDto.STATE_PREPARED
    }

    override fun playerRelease() {
        player.release()
    }

}