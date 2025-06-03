package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorSearchImpl(private val trackRepository: TrackRepository) : TrackInteractorSearch {

    override fun searchTracks(text: String) : Flow<Pair<List<Track>?, ErrorType?>>{
        return trackRepository.searchTracks(text).map {result ->
            when(result){
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Empty -> {
                    Pair(listOf(), null)
                }
                is Resource.Error -> {
                    Pair(null, result.errorType)
                }
            }
        }
    }
}