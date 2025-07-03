package com.example.playlistmaker.playlists.presentation.models

data class PlaylistInfo(
    val id: Int,
    val title: String,
    val coverPath: String,
    val tracksCount: Int
)