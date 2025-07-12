package com.example.playlistmaker.playlists.presentation.models

sealed class OnePlaylistScreenState {
    data class Content(val playlistDetails: OnePlaylistDetails) : OnePlaylistScreenState()
    data object Loading : OnePlaylistScreenState()
}