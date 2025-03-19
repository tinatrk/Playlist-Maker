package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackId: Int?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val artworkUrl512: String?,
    val trackTime: String?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val previewUrl: String?
) : Serializable