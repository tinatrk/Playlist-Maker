package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.Resource
import java.util.concurrent.Executors

class TrackInteractorSearchImpl(private val trackRepository: TrackRepository) : TrackInteractorSearch {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TrackInteractorSearch.TrackConsumer) {
        executor.execute {
            when (val resource = trackRepository.searchTracks(text)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Empty -> {
                    consumer.consume(listOf(), null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.errorType)
                }
            }
        }
    }
}