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
            trackDto.trackId ?: UNKNOWN_ID,
            trackDto.artistName ?: DEFAULT_STRING,
            trackDto.collectionName ?: DEFAULT_STRING,
            trackDto.trackName ?: DEFAULT_STRING,
            trackDto.artworkUrl100 ?: DEFAULT_LINK,
            trackDto.trackTimeMillis ?: DEFAULT_INT,
            trackDto.country ?: DEFAULT_STRING,
            trackDto.primaryGenreName ?: DEFAULT_STRING,
            trackDto.releaseDate ?: DEFAULT_STRING,
            trackDto.previewUrl ?: DEFAULT_LINK
        )
    }
}