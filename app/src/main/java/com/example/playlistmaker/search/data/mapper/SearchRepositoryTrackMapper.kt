package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_LINK
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

class SearchRepositoryTrackMapper {
    fun map(trackDto: TrackDto): Track {
        return Track(
            trackId = trackDto.trackId ?: UNKNOWN_ID,
            artistName = trackDto.artistName ?: DEFAULT_STRING,
            collectionName = trackDto.collectionName ?: DEFAULT_STRING,
            trackName = trackDto.trackName ?: DEFAULT_STRING,
            artworkUrl100 = trackDto.artworkUrl100 ?: DEFAULT_LINK,
            trackTimeMillis = trackDto.trackTimeMillis ?: DEFAULT_INT,
            country = trackDto.country ?: DEFAULT_STRING,
            primaryGenreName = trackDto.primaryGenreName ?: DEFAULT_STRING,
            releaseDate = trackDto.releaseDate ?: DEFAULT_STRING,
            previewUrl = trackDto.previewUrl ?: DEFAULT_LINK
        )
    }
}