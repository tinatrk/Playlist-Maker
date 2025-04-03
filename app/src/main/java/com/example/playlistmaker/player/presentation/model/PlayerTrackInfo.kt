package com.example.playlistmaker.player.presentation.model

data class PlayerTrackInfo(
    val trackId: Int,
    val artistName: String,
    val collectionName: String?,
    val trackName: String,
    val artworkUrl: String,
    val trackTime: String,
    val country: String,
    val genre: String,
    val releaseDate: String,
    val previewUrl: String
)
