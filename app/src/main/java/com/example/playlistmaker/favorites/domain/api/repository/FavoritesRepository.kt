package com.example.playlistmaker.favorites.domain.api.repository

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun saveFavoriteTrack(track: Track)

    suspend fun deleteFavoriteTrack(track: Track)

    fun getAllFavoriteTracks(): Flow<List<Track>>

    //fun getTrackById(trackId: Int): Flow<Track>

    //suspend fun markFavoriteTracks(tracks: List<Track>): List<Track>
    fun markFavoriteTracks(tracks: List<Track>): Flow<List<Track>>
}