package com.example.playlistmaker.playlists.presentation.models

sealed class PlaylistModificationState {
    data class SuccessCreated(
        val playlistTitle: String,
        val coverUri: String,
        val filePath: String,
        val coverModification: PlaylistCoverModificationType
    ) : PlaylistModificationState()

    data class AlreadyExists(val playlistTitle: String) : PlaylistModificationState()

    data class SuccessUpdated(
        val oldTitle: String,
        val newTitle: String,
        val coverUri: String,
        val filePath: String,
        val coverModification: PlaylistCoverModificationType
    ) : PlaylistModificationState()

    data object NothingChanged : PlaylistModificationState()
}