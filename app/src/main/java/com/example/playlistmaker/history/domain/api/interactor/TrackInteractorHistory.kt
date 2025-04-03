package com.example.playlistmaker.history.domain.api.interactor

import com.example.playlistmaker.search.domain.models.Track

interface TrackInteractorHistory {
    fun getHistory(): List<Track>

    fun updateHistory(newTrack: Track)

    fun clearHistory()
}