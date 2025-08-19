package com.example.playlistmaker.player.presentation.model

data class PlayerScreenState(
    val isError: Boolean = false,
    val trackInfo: PlayerTrackInfo = PlayerTrackInfo.empty(),
    val playerState: PlayerState = PlayerState.Default(),
    val playlistsState: PlaylistsState = PlaylistsState.Idle,
)
