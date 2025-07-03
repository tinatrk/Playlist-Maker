package com.example.playlistmaker.favorites.data.impl

import com.example.playlistmaker.favorites.data.AppDatabase
import com.example.playlistmaker.favorites.data.entity.TrackEntity
import com.example.playlistmaker.favorites.data.mapper.TrackDbMapper
import com.example.playlistmaker.favorites.domain.api.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackMapper: TrackDbMapper
) : FavoritesRepository {

    override suspend fun saveFavoriteTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackMapper.map(track))
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackMapper.map(track))
    }

    /*override fun getTrackById(trackId: Int): Flow<Track> = flow {
        val track = appDatabase.trackDao().getTrackById(trackId)
        if (track != null)
            emit(trackMapper.map(track))
        else emit(TrackDbMapper.empty())


    }*/

    override fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        emit (convertFromTrackEntity(appDatabase.trackDao().getAllTracks()))
    }

    private fun convertFromTrackEntity(entities: List<TrackEntity>): List<Track> {
        val tracks = entities.sortedByDescending { it.dataOfAppearanceInDB }
            .map { entity -> trackMapper.map(entity) }
        val markedTracks: MutableList<Track> = mutableListOf()
        for (i in tracks.indices) {
            markedTracks.add(tracks[i].copy(isFavorite = true))
        }
        return markedTracks
    }

    override fun markFavoriteTracks(tracks: List<Track>): Flow<List<Track>> = flow {
        val favoriteIds = appDatabase.trackDao().getAllTrackId()
        if (favoriteIds.isEmpty())
            emit (tracks)
        val markedTracks: MutableList<Track> = mutableListOf()
        for (i in tracks.indices) {
            markedTracks.add(tracks[i].copy(isFavorite = favoriteIds.contains(tracks[i].trackId)))
        }
        emit (markedTracks)
    }
}