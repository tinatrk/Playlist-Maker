package com.example.playlistmaker.playlists.presentation.models

import java.io.File

sealed class PlaylistCreationState {
    data class SuccessCreated(
        val playlistTitle: String,
        val coverUri: String,
        val filePath: File?
    ) : PlaylistCreationState()

    data class AlreadyExists(val playlistTitle: String) : PlaylistCreationState()
}