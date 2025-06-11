package com.example.playlistmaker.favorites.presentation.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoritesScreenState {
    data object Loading: FavoritesScreenState
    data class Content(val tracks: List<Track>): FavoritesScreenState
    data object Empty: FavoritesScreenState
}