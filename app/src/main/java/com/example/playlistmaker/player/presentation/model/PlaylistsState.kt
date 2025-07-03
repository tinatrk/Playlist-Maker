package com.example.playlistmaker.player.presentation.model

import com.example.playlistmaker.playlists.domain.models.Playlist

sealed class PlaylistsState {
    data object Idle : PlaylistsState()
    data object Loading : PlaylistsState()
    data object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}