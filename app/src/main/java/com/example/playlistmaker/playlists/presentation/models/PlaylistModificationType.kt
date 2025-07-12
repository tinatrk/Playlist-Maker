package com.example.playlistmaker.playlists.presentation.models

sealed class PlaylistCoverModificationType {
    data object NotChanged : PlaylistCoverModificationType()
    data class Renamed(val oldName: String, val newName: String) : PlaylistCoverModificationType()
    data class ChangedContent(val newContentUri: String, val coverName: String) :
        PlaylistCoverModificationType()

    data class RenamedAndChangedContent(
        val oldName: String,
        val newName: String,
        val newContentUri: String
    ) : PlaylistCoverModificationType()
}