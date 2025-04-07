package com.example.playlistmaker.history.domain.impl

import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.Track

class TrackInteractorHistoryImpl(private val repository: TrackRepository) : TrackInteractorHistory {
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun updateHistory(newTrack: Track) {
        var tracks: MutableList<Track> = getHistory().toMutableList()

        val index = indexOfTheSame(tracks, newTrack)
        if (index != UNKNOWN_INDEX) {
            tracks.remove(newTrack)
        }
        tracks.add(0, newTrack)
        if (tracks.size > MAX_COUNT_TRACKS)
            tracks = tracks.take(MAX_COUNT_TRACKS).toMutableList()

        repository.updateHistory(tracks)
    }

    override fun clearHistory() {
        repository.updateHistory(listOf())
    }

    private fun indexOfTheSame(tracks: List<Track>, track: Track): Int {
        tracks.forEachIndexed { index, element ->
            if (element.trackId != null && track.trackId != null) {
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