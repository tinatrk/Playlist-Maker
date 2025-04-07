package com.example.playlistmaker.settings.domain.api.repository

interface SettingsRepository {
    fun getTheme(): Boolean

    fun saveTheme(isDarkThemeOn: Boolean)

    fun setSavedTheme()
}