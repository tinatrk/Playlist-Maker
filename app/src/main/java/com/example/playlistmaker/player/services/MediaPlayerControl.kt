package com.example.playlistmaker.player.services

import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface MediaPlayerControl {
    fun getPlayerState(): StateFlow<PlayerState>

    fun startPlayer()

    fun pausePlayer()

    fun startForeground()

    fun stopForeground()
}