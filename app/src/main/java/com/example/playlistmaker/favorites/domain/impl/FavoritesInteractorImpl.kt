package com.example.playlistmaker.favorites.domain.impl

import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.api.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getAllFavoriteTracks()
    }

    override suspend fun saveFavoriteTrack(track: Track) {
        favoritesRepository.saveFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoritesRepository.deleteFavoriteTrack(track)
    }

    override fun getTrackById(trackId: Int): Flow<Track> {
        return favoritesRepository.getTrackById(trackId)
    }

    override fun markFavoriteTracks(tracks: List<Track>): Flow<List<Track>> = flow {
        emit(favoritesRepository.markFavoriteTracks(tracks))
    }
}