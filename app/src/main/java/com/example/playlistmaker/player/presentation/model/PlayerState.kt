package com.example.playlistmaker.player.presentation.model

data class PlayerState(
    val isError: Boolean = false,
    val trackInfo: PlayerTrackInfo = PlayerTrackInfo.empty(),
    val trackPlaybackState: PlaybackState = PlaybackState.NOT_PREPARED,
    val curPosition: String = DEFAULT_CUR_POSITION
) {
    companion object {
        private const val DEFAULT_CUR_POSITION = "00:00"
    }
}