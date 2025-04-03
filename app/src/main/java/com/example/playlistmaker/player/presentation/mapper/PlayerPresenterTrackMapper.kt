package com.example.playlistmaker.player.presentation.mapper

import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

object PlayerPresenterTrackMapper {
    fun map(track: Track?): PlayerTrackInfo {
        return PlayerTrackInfo(
            track?.trackId ?: DEFAULT_ID,
            track?.artistName ?: DEFAULT_CONTENT,
            track?.collectionName,
            track?.trackName ?: DEFAULT_CONTENT,
            getCoverArtwork512(track?.artworkUrl100) ?: DEFAULT_LINK,
            getTrackTimeString(track?.trackTime) ?: DEFAULT_CONTENT,
            track?.country ?: DEFAULT_CONTENT,
            track?.primaryGenreName ?: DEFAULT_CONTENT,
            getTrackYear(track?.releaseDate) ?: DEFAULT_CONTENT,
            track?.previewUrl ?: DEFAULT_LINK
        )
    }

    private fun getCoverArtwork512(artworkUrl100: String?) =
        artworkUrl100?.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT)

    private fun getTrackTimeString(trackTimeMillis: Int?): String? = if (trackTimeMillis != null) {
        SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
    } else {
        null
    }

    private fun getTrackYear(releaseDate: String?): String? {
        val parser = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
        val date = releaseDate?.let { parser.parse(it) } ?: return null
        val formatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    private const val TRACK_TIME_FORMAT = "mm:ss"
    private const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val YEAR_FORMAT = "yyyy"
    private const val COVER_DELIMITER = '/'
    private const val COVER_REPLACEMENT = "512x512bb.jpg"

    private const val DEFAULT_CONTENT = "Ничего не нашлось"
    private const val DEFAULT_ID = -1
    private const val DEFAULT_LINK = ""
}