package com.example.playlistmaker.search.domain.api.repository

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(text: String) : Flow<Resource<List<Track>>>

    fun getHistory() : Flow<List<Track>>

    suspend fun updateHistory(tracks: List<Track>)
}