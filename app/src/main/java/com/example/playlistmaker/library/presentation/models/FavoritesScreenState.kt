package com.example.playlistmaker.library.presentation.models

sealed class FavoritesScreenState {
    data object Content: FavoritesScreenState()
    data object Empty: FavoritesScreenState()
}