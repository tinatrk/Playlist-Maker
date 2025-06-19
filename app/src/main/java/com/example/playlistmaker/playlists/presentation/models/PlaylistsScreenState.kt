package com.example.playlistmaker.playlists.presentation.models

sealed class PlaylistsScreenState {
    data object Content: PlaylistsScreenState()
    data object Empty: PlaylistsScreenState()
}