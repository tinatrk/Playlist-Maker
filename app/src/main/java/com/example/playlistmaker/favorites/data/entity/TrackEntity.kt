package com.example.playlistmaker.favorites.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track_table")
data class TrackEntity(
    @PrimaryKey
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
    val dataOfAppearanceInDB: String
)