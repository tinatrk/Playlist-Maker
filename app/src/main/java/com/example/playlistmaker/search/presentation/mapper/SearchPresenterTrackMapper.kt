package com.example.playlistmaker.search.presentation.mapper

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.model.SearchTrackInfo
import java.text.SimpleDateFormat
import java.util.Locale

object SearchPresenterTrackMapper {
    fun map(track: Track): SearchTrackInfo {
        return SearchTrackInfo(
            track.trackId ?: DEFAULT_ID,
            track.artistName ?: DEFAULT_CONTENT,
            track.trackName ?: DEFAULT_CONTENT,
            track.artworkUrl100 ?: DEFAULT_LINK,
            getTrackTimeString(track.trackTime) ?: DEFAULT_CONTENT,
        )
    }

    private fun getTrackTimeString(trackTimeMillis: Int?): String? = if (trackTimeMillis != null) {
        SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
    } else {
        null
    }

    private const val TRACK_TIME_FORMAT = "mm:ss"

    private const val DEFAULT_CONTENT = "Ничего не нашлось"
    private const val DEFAULT_ID = -1
    private const val DEFAULT_LINK = ""
}