package com.example.playlistmaker.player.presentation.model

sealed class PlayerState(val isButtonEnabled: Boolean, val isButtonPlaying: Boolean,val progress: String) {
    class Default: PlayerState(false, false, DEFAULT_CUR_POSITION)
    class Prepared: PlayerState(true, false, DEFAULT_CUR_POSITION)
    class Playing(progress: String): PlayerState(true, true, progress)
    class Paused(progress: String): PlayerState(true, false, progress)

    companion object {
        private const val DEFAULT_CUR_POSITION = "00:00"
    }
}