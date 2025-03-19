package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.domain.api.repository.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorSearchImpl(private val repository: TrackRepository) : TrackInteractorSearch {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TrackInteractorSearch.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(text))
        }
    }
}