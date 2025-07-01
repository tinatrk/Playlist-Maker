package com.example.playlistmaker.playlists.domain.impl

import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.api.repository.PlaylistRepository
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        playlistRepository.createNewPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addNewTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addNewTrackToPlaylist(playlist, track)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.deleteTrackFromPlaylist(playlist, track)
    }

    override fun checkPlaylistExistence(playlistTitle: String): Flow<Boolean> {
        return playlistRepository.checkPlaylistExistence(playlistTitle)
    }
}