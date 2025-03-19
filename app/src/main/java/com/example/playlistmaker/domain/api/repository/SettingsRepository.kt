package com.example.playlistmaker.domain.api.repository

interface SettingsRepository {
    fun getTheme(): Boolean

    fun saveTheme(isDarkThemeOn: Boolean)
}