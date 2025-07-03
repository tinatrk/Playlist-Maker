package com.example.playlistmaker.playlists.data.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_LINK
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.playlists.data.entity.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackInPlaylistDbMapper {
    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            artistName = track.trackName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl100 = track.artworkUrl100,
            trackTimeMillis = track.trackTimeMillis,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl,
            dataOfAppearanceInDB = getCurData()
        )
    }

    fun map(track: TrackInPlaylistEntity): Track {
        return Track(
            trackId = track.trackId,
            artistName = track.trackName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl100 = track.artworkUrl100,
            trackTimeMillis = track.trackTimeMillis,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl
        )
    }

    private fun getCurData(): String {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        val curDate = formatter.format(Date())
        return curDate
    }

    companion object {
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

        private const val DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
    }

}