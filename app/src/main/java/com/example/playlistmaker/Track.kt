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
    val releaseDate: String?,
    val previewUrl: String?
) : Serializable {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT)

    fun getTrackTime(): String? = if (trackTimeMillis != null) {
        SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
    } else {
        null
    }

    fun getTrackYear(): String? {
        val parser = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
        val date = releaseDate?.let { parser.parse(it) } ?: return null
        val formatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }


    private companion object {
        const val TRACK_TIME_FORMAT = "mm:ss"
        const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val YEAR_FORMAT = "yyyy"
        const val COVER_DELIMITER = '/'
        const val COVER_REPLACEMENT = "512x512bb.jpg"
    }
}
