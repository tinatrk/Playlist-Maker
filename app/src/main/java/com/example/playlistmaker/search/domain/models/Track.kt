package com.example.playlistmaker.search.domain.models

data class Track(
    val trackId: Int?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTime: Int?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?
)