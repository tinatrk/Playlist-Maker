package com.example.playlistmaker.playlists.presentation.models

sealed class AddingTrackToPlaylistState {
    data class SuccessAdding(val playlistTitle: String, val coverUri: String) :
        AddingTrackToPlaylistState()

    data class AlreadyExists(val playlistTitle: String) : AddingTrackToPlaylistState()
}