package com.example.playlistmaker.search.data.dto

data class TrackDto(
    val trackId: Int?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTimeMillis: Int?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?
)
