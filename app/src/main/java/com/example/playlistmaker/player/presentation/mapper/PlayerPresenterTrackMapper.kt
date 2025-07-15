package com.example.playlistmaker.player.presentation.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_LINK
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerPresenterTrackMapper {
    fun map(track: Track?): PlayerTrackInfo {
        return if (track == null) {
            getDefaultTrack()
        } else PlayerTrackInfo(
            trackId = track.trackId,
            artistName = track.artistName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl = getCoverArtwork512(track.artworkUrl100),
            trackTime = getTrackTimeString(track.trackTimeMillis),
            country = track.country,
            genre = track.primaryGenreName,
            releaseDate = getTrackYear(track.releaseDate),
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun map(trackInfo: PlayerTrackInfo): Track {
        return Track(
            trackId = trackInfo.trackId,
            artistName = trackInfo.artistName,
            collectionName = trackInfo.trackName,
            trackName = trackInfo.trackName,
            artworkUrl100 = getCoverArtwork100(trackInfo.artworkUrl),
            trackTimeMillis = getTrackTimeMillis(trackInfo.trackTime),
            country = trackInfo.country,
            primaryGenreName = trackInfo.genre,
            releaseDate = trackInfo.releaseDate,
            previewUrl = trackInfo.previewUrl
        )
    }

    private fun getCoverArtwork512(artworkUrl100: String) =
        if (artworkUrl100 != DEFAULT_STRING)
            artworkUrl100.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT_512)
        else DEFAULT_STRING

    private fun getCoverArtwork100(artworkUrl512: String) =
        if (artworkUrl512 != DEFAULT_STRING)
            artworkUrl512.replaceAfterLast(COVER_DELIMITER, COVER_REPLACEMENT_100)
        else DEFAULT_STRING

    private fun getTrackTimeString(trackTimeMillis: Int): String =
        if (trackTimeMillis != DEFAULT_INT) {
            SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
        } else {
            DEFAULT_STRING
        }

    private fun getTrackTimeMillis(trackTime: String): Int {
        val format = SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault())
        val time = format.parse(trackTime)
        return time?.time?.toInt() ?: 0
    }

    private fun getTrackYear(releaseDate: String): String {
        val parser = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
        val date = releaseDate.let { parser.parse(it) } ?: return DEFAULT_STRING
        val formatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    private fun getDefaultTrack(): PlayerTrackInfo {
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

    companion object {
        private const val TRACK_TIME_FORMAT = "mm:ss"
        private const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private const val YEAR_FORMAT = "yyyy"
        private const val COVER_DELIMITER = '/'
        private const val COVER_REPLACEMENT_512 = "512x512bb.jpg"
        private const val COVER_REPLACEMENT_100 = "100x100bb.jpg"

        fun empty(): Track {
            return Track(
                trackId = UNKNOWN_ID,
                artistName = DEFAULT_STRING,
                collectionName = DEFAULT_STRING,
                trackName = DEFAULT_STRING,
                artworkUrl100 = DEFAULT_LINK,
                trackTimeMillis = DEFAULT_INT,
                country = DEFAULT_STRING,
                primaryGenreName = DEFAULT_STRING,
                releaseDate = DEFAULT_STRING,
                previewUrl = DEFAULT_LINK
            )
        }
    }
}