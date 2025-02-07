package com.example.playlistmaker

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Int?,
    val artistName: String?,
    val collectionName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    val trackTimeMillis: Int?,
    val country: String?,
    val primaryGenreName: String?,
    val releaseDate: String?
) : Serializable {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT)

    fun getTrackTime(): String? = if (trackTimeMillis != null) {
        SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
    } else {
        null
    }

    fun getTrackYear(): String? = if (releaseDate != null) {
        val regex = RELEASE_DATE_REGEX.toRegex()
        regex.find(releaseDate)?.value
    } else null


    private companion object {
        const val TRACK_TIME_FORMAT = "mm:ss"
        const val RELEASE_DATE_REGEX = "[1-2][0-9][0-9][0-9]"
        const val COVER_DELIMITER = '/'
        const val COVER_REPLACEMENT = "512x512bb.jpg"
    }
}
