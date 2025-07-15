package com.example.playlistmaker.playlists.data.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_LINK
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.playlists.data.entity.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackInPlaylistDbMapper {
    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId = track.trackId,
            artistName = track.artistName,
            collectionName = track.collectionName,
            trackName = track.trackName,
            artworkUrl100 = track.artworkUrl100,
            trackTimeMillis = track.trackTimeMillis,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            previewUrl = track.previewUrl,
        )
    }

    fun map(track: TrackInPlaylistEntity): Track {
        return Track(
            trackId = track.trackId,
            artistName = track.artistName,
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
    }

}