package com.example.playlistmaker.playlists.presentation.models

import com.example.playlistmaker.app.App.Companion.EMPTY_STRING

sealed class ModifyPlaylistScreenState {
    data class Init(
        val coverUri: String = EMPTY_STRING,
        val title: String = EMPTY_STRING,
        val description: String = EMPTY_STRING
    ) : ModifyPlaylistScreenState()

    data class CoverContent(val coverUri: String) : ModifyPlaylistScreenState()
}