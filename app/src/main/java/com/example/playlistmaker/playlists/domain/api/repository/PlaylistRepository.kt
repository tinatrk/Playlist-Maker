package com.example.playlistmaker.playlists.domain.api.repository

import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createNewPlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addNewTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)

    fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean>
}