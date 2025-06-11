package com.example.playlistmaker.player.presentation.model

import com.example.playlistmaker.app.App.Companion.DEFAULT_LINK
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID

data class PlayerTrackInfo(
    val trackId: Int,
    val artistName: String,
    val collectionName: String,
    val trackName: String,
    val artworkUrl: String,
    val trackTime: String,
    val country: String,
    val genre: String,
    val releaseDate: String,
    val previewUrl: String,
    var isFavorite: Boolean
) {
    companion object {
        fun empty(): PlayerTrackInfo {
            return PlayerTrackInfo(
                trackId = UNKNOWN_ID,
                artistName = DEFAULT_STRING,
                collectionName = DEFAULT_STRING,
                trackName = DEFAULT_STRING,
                artworkUrl = DEFAULT_LINK,
                trackTime = DEFAULT_STRING,
                country = DEFAULT_STRING,
                genre = DEFAULT_STRING,
                releaseDate = DEFAULT_STRING,
                previewUrl = DEFAULT_LINK,
                isFavorite = false

            )
        }
    }
}
