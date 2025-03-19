package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.PlayerState

interface AudioPlayerRepository {
    fun playerPrepare(resourceUrl: String, preparedCallback: () -> Unit,
                      completionCallback: () -> Unit)

    fun getPlayerState() : PlayerState

    fun getCurrentPosition() : Int

    fun playerStart()

    fun playerPause()

    fun playerStop()

    fun playerRelease()
}