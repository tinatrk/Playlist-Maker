package com.example.playlistmaker.settings.presentation.model

sealed class SettingsScreenState {
    data class Default(val isDefaultThemeDark: Boolean) : SettingsScreenState()
}