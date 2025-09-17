package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class Track(
    val trackId: Int,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val artworkUrl100: String,
    val trackTimeMillis: Int,
    val country: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val previewUrl: String,
    val isFavorite: Boolean = false
) : Parcelable