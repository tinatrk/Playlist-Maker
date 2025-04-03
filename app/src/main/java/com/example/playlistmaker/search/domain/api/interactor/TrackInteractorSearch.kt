package com.example.playlistmaker.search.domain.api.interactor

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractorSearch {
    fun searchTracks(text: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorType: ErrorType?)
    }
}