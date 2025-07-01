package com.example.playlistmaker.playlists.presentation.mapper

import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.PlaylistInfo

class PlaylistInfoMapper() {
    fun map(playlist: Playlist): PlaylistInfo {
        return PlaylistInfo(
            id = playlist.id,
            title = playlist.title,
            coverPath = playlist.coverPath,
            tracksCount = playlist.tracksCount
        )
    }
}