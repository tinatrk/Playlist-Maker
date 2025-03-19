package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.Track

interface TrackInteractorSearch {
    fun searchTracks(text: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}