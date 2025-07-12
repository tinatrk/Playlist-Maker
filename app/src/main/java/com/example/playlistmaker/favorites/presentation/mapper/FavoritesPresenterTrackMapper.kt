package com.example.playlistmaker.favorites.presentation.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.favorites.presentation.models.FavoritesTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritesPresenterTrackMapper {
    fun map(track: Track): FavoritesTrackInfo {
        return FavoritesTrackInfo(
            trackId = track.trackId,
            artistName = track.artistName,
            trackName = track.trackName,
            artworkUrl = track.artworkUrl100,
            trackTime = getTrackTimeString(track.trackTimeMillis),
        )
    }

    private fun getTrackTimeString(trackTimeMillis: Int): String =
        if (trackTimeMillis != DEFAULT_INT) {
            SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
        } else {
            DEFAULT_STRING
        }

    companion object {
        private const val TRACK_TIME_FORMAT = "mm:ss"
    }
}