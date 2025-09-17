package com.example.playlistmaker.playlists.presentation.models

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.playlists.domain.models.Playlist

@Immutable
sealed class PlaylistsScreenState {
    data object Loading : PlaylistsScreenState()
    data object Empty : PlaylistsScreenState()
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState()
}