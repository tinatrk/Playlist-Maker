package com.example.playlistmaker.playlists.presentation.models

sealed class CreatePlaylistsScreenState {
    data class Content(val coverUri: String) : CreatePlaylistsScreenState()
    data object Empty : CreatePlaylistsScreenState()
}