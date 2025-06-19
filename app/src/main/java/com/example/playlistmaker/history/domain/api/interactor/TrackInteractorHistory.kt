package com.example.playlistmaker.history.domain.api.interactor

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractorHistory {
    fun getHistory(): Flow<List<Track>>

    suspend fun updateHistory(newTrack: Track)

    suspend fun clearHistory()
}