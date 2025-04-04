package com.example.playlistmaker.player.presentation.model

sealed class PlayerState() {
    data class NotPrepared(val trackInfo: PlayerTrackInfo) : PlayerState()
    data class Prepared(val trackInfo: PlayerTrackInfo)  : PlayerState()
    data object Playing : PlayerState()
    data class Paused(val curPosition: String, val trackInfo: PlayerTrackInfo) : PlayerState()
    data class Progress(val progress: String) : PlayerState()
    data class Error(val trackInfo: PlayerTrackInfo)  : PlayerState()
}