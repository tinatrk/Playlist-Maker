package com.example.playlistmaker.favorites.data.impl

import com.example.playlistmaker.favorites.data.AppDatabase
import com.example.playlistmaker.favorites.data.entity.TrackEntity
import com.example.playlistmaker.favorites.data.mapper.TrackDbMapper
import com.example.playlistmaker.favorites.domain.api.repository.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
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

    override fun getTrackById(trackId: Int): Flow<Track> {
        return appDatabase.trackDao().getTrackById(trackId).map { track ->
            if (track != null) trackMapper.map(track)
            else TrackDbMapper.empty()
        }
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getAllTracks().map { convertFromTrackEntity(it) }
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

    override suspend fun markFavoriteTracks(tracks: List<Track>): List<Track> {
        val favoriteIds = appDatabase.trackDao().getAllTrackId()
        if (favoriteIds.isEmpty())
            return (tracks)
        val markedTracks: MutableList<Track> = mutableListOf()
        for (i in tracks.indices) {
            markedTracks.add(tracks[i].copy(isFavorite = favoriteIds.contains(tracks[i].trackId)))
        }
        return (markedTracks)
    }
}