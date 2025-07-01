package com.example.playlistmaker.playlists.data.impl

import com.example.playlistmaker.favorites.data.AppDatabase
import com.example.playlistmaker.playlists.data.mapper.PlaylistDbMapper
import com.example.playlistmaker.playlists.domain.api.repository.PlaylistRepository
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistMapper: PlaylistDbMapper
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
        val newTrackList: MutableList<Int> = mutableListOf()
        newTrackList.addAll(playlist.tracksIds)
        newTrackList.add(track.trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTrackList, tracksCount = newTrackList.size)

        appDatabase.playlistDao().updatePlaylist(playlistMapper.map(updatedPlaylist))
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        val newTrackList: MutableList<Int> = mutableListOf()
        newTrackList.addAll(playlist.tracksIds)
        newTrackList.remove(track.trackId)

        val updatedPlaylist =
            playlist.copy(tracksIds = newTrackList, tracksCount = newTrackList.size)

        appDatabase.playlistDao().updatePlaylist(playlistMapper.map(updatedPlaylist))

        if (checkOnlyOnePlaylistHasTrack(playlist.id, track.trackId))
            appDatabase.trackInPlaylistDao().deleteTrackInPlaylist(track.trackId)
    }

    override fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean> = flow {
        val playlistsTitles = appDatabase.playlistDao().getAllPlaylistsTitles()
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
}