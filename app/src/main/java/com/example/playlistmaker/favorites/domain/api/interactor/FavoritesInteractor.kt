package com.example.playlistmaker.favorites.domain.api.interactor

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun saveFavoriteTrack(track: Track)

    suspend fun deleteFavoriteTrack(track: Track)

    fun getAllFavoriteTracks(): Flow<List<Track>>

    //fun getTrackById(trackId: Int): Flow<Track>

    fun markFavoriteTracks(tracks: List<Track>): Flow<List<Track>>
}