package com.example.playlistmaker.search.presentation.model

import com.example.playlistmaker.search.domain.models.Track

sealed interface NavigationEvent {
    data object Default: NavigationEvent
    data class OpenAudioPlayer(val track: Track) : NavigationEvent
}