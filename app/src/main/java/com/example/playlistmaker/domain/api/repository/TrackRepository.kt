package com.example.playlistmaker.domain.api.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(text: String): List<Track>?

    fun getHistory() : List<Track>

    fun updateHistory(tracks: List<Track>)
}