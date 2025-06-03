package com.example.playlistmaker.search.domain.api.interactor

import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractorSearch {
    fun searchTracks(text: String) : Flow<Pair<List<Track>?, ErrorType?>>
}