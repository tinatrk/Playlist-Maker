package com.example.playlistmaker.playlists.data.impl

import com.example.playlistmaker.favorites.data.AppDatabase
import com.example.playlistmaker.playlists.data.entity.TrackInPlaylistEntity
import com.example.playlistmaker.playlists.data.mapper.PlaylistDbMapper
import com.example.playlistmaker.playlists.data.mapper.TrackInPlaylistDbMapper
import com.example.playlistmaker.playlists.domain.api.repository.PlaylistRepository
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistMapper: PlaylistDbMapper,
    private val trackMapper: TrackInPlaylistDbMapper
) : PlaylistRepository {
    override suspend fun createNewPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistMapper.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        for (trackId in playlist.tracksIds) {
            if (checkOnlyOnePlaylistHasTrack(playlist.id, trackId))
                appDatabase.trackInPlaylistDao().deleteTrackInPlaylist(trackId)
        }

        appDatabase.playlistDao().deletePlaylist(playlistMapper.map(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(playlistMapper.mapList(playlists))
    }

    override suspend fun addNewTrackToPlaylist(playlist: Playlist, track: Track) {
        val newTracksIds: MutableList<Int> = mutableListOf()
        newTracksIds.addAll(playlist.tracksIds)
        newTracksIds.add(track.trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTracksIds, tracksCount = newTracksIds.size)

        appDatabase.playlistDao().updatePlaylist(playlistMapper.map(updatedPlaylist))

        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(trackMapper.map(track))
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Int) {
        val newTracksIds: MutableList<Int> = mutableListOf()
        newTracksIds.addAll(playlist.tracksIds)
        newTracksIds.remove(trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTracksIds, tracksCount = newTracksIds.size)

        appDatabase.playlistDao().updatePlaylist(playlistMapper.map(updatedPlaylist))

        if (checkOnlyOnePlaylistHasTrack(playlist.id, trackId))
            appDatabase.trackInPlaylistDao().deleteTrackInPlaylist(trackId)
    }

    override fun checkPlaylistExistence(playlistTitle: String, playlistId: Int): Flow<Boolean> =
        flow {
            val playlistsTitles =
                appDatabase.playlistDao().getAllPlaylistsTitlesExceptOne(playlistId)
            emit(playlistsTitles.contains(playlistTitle))
        }

    private suspend fun checkOnlyOnePlaylistHasTrack(playlistId: Int, trackId: Int): Boolean {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        var isOnlyOnePlaylistHasTrack = true
        for (playlist in playlists) {
            if (playlist.id != playlistId) {
                val tracksIds =
                    playlistMapper.convertStringToInts(playlist.tracksIds).toMutableList()
                isOnlyOnePlaylistHasTrack = !tracksIds.contains(trackId)
            }
            if (!isOnlyOnePlaylistHasTrack) break
        }
        return isOnlyOnePlaylistHasTrack
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        val result =
            if (playlist != null) playlistMapper.map(playlist) else PlaylistDbMapper.empty()
        emit(result)
    }

    override fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>> = flow {
        val allTrackEntities: List<TrackInPlaylistEntity> =
            appDatabase.trackInPlaylistDao().getAllTracks()

        val allTracks: List<Track> = allTrackEntities.map { trackMapper.map(it) }

        val neededTracks: MutableList<Track> = mutableListOf()

        for (track in allTracks) {
            if (tracksIds.contains(track.trackId)) neededTracks.add(track)
        }

        val sortedTracks = sortTracks(tracksIds.reversed(), neededTracks)

        emit(markFavoriteTracks(sortedTracks))
    }

    private fun sortTracks(idsWithNeededOrder: List<Int>, tracks: List<Track>): List<Track> {
        val tracksIs: MutableList<Int> = mutableListOf()
        tracks.forEach { it -> tracksIs.add(it.trackId) }
        val sortedTracks: MutableList<Track> = mutableListOf()

        idsWithNeededOrder.forEach { id ->
            val neededTrackIndex = tracksIs.indexOf(id)
            sortedTracks.add(tracks[neededTrackIndex])
        }

        return sortedTracks
    }

    private suspend fun markFavoriteTracks(tracks: List<Track>): List<Track> {
        val favoriteTrackIds = appDatabase.trackDao().getAllTrackId()
        if (favoriteTrackIds.isEmpty())
            return tracks
        val markedTracks: MutableList<Track> = mutableListOf()
        for (i in tracks.indices) {
            markedTracks.add(tracks[i].copy(isFavorite = favoriteTrackIds.contains(tracks[i].trackId)))
        }
        return markedTracks
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistMapper.map(playlist))
    }
}