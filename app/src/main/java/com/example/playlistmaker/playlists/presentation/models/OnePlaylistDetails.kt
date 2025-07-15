package com.example.playlistmaker.playlists.presentation.models

import com.example.playlistmaker.search.domain.models.Track

data class OnePlaylistDetails(
    val title: String,
    val description: String,
    val coverPath: String,
    val tracks: List<Track>,
    val tracksCountString: String,
    val sumTracksDuration: String
)
