package com.example.playlistmaker.favorites.presentation.models

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.search.domain.models.Track

@Immutable
sealed interface NavigationEvent {
    data object Default: NavigationEvent
    data class OpenAudioPlayer(val track: Track) : NavigationEvent
}