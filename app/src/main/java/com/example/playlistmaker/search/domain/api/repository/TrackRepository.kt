package com.example.playlistmaker.search.domain.api.repository

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

interface TrackRepository {
    fun searchTracks(text: String): Resource<List<Track>>

    fun getHistory() : List<Track>

    fun updateHistory(tracks: List<Track>)
}