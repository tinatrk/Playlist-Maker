package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track

object SearchRepositoryTrackMapper {
    fun map(trackDto: TrackDto) : Track{
        return Track(
            trackDto.trackId,
            trackDto.artistName,
            trackDto.collectionName,
            trackDto.trackName,
            trackDto.artworkUrl100,
            trackDto.trackTimeMillis,
            trackDto.country,
            trackDto.primaryGenreName,
            trackDto.releaseDate,
            trackDto.previewUrl
        )
    }
}