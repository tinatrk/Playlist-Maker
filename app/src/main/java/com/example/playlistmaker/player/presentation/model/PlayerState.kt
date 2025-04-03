package com.example.playlistmaker.player.presentation.model

sealed class PlayerState {
    data object NotPrepared : PlayerState()
    data object Prepared : PlayerState()
    data object Playing : PlayerState()
    data class Paused(val curPosition: String) : PlayerState()
    data class Progress(val progress: String) : PlayerState()
    data object Error : PlayerState()
}