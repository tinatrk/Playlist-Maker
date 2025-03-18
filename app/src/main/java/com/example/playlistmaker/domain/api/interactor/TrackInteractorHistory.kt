package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.Track

interface TrackInteractorHistory {
    fun getHistory(): List<Track>

    fun updateHistory(newTrack: Track)

    fun clearHistory()
}