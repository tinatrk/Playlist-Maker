package com.example.playlistmaker.playlists.presentation.models

import com.example.playlistmaker.playlists.domain.models.Playlist

sealed class PlaylistsScreenState {
    data object Loading : PlaylistsScreenState()
    data object Empty : PlaylistsScreenState()
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState()
}