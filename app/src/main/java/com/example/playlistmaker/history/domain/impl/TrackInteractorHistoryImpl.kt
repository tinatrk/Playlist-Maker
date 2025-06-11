package com.example.playlistmaker.history.domain.impl

import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TrackInteractorHistoryImpl(private val trackRepository: TrackRepository) :
    TrackInteractorHistory {
    override fun getHistory(): Flow<List<Track>> {
        return trackRepository.getHistory()
    }

    override suspend fun updateHistory(newTrack: Track) {
        getHistory().collect { historyTracks ->
            var tracks = historyTracks.toMutableList()
            val index = indexOfTheSame(tracks, newTrack)
            if (index != UNKNOWN_INDEX) {
                tracks.removeAt(index)
            }
            tracks.add(0, newTrack)
            if (tracks.size > MAX_COUNT_TRACKS)
                tracks = tracks.take(MAX_COUNT_TRACKS).toMutableList()

            trackRepository.updateHistory(tracks)
        }
    }

    override suspend fun clearHistory() {
        trackRepository.updateHistory(listOf())
    }

    private fun indexOfTheSame(tracks: List<Track>, track: Track): Int {
        tracks.forEachIndexed { index, element ->
            if (element.trackId != UNKNOWN_ID && track.trackId != UNKNOWN_ID) {
                if (element.trackId == track.trackId)
                    return index
            } else if (element == track)
                return index
        }
        return UNKNOWN_INDEX
    }

    companion object {
        const val MAX_COUNT_TRACKS = 10
        const val UNKNOWN_INDEX = -1
    }
}