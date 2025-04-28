package com.example.playlistmaker.library.presentation.models

sealed class PlaylistsScreenState {
    data object Content: PlaylistsScreenState()
    data object Empty: PlaylistsScreenState()
}