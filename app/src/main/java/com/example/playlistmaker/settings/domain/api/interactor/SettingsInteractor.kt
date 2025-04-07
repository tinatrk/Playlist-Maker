package com.example.playlistmaker.settings.domain.api.interactor

interface SettingsInteractor {
    fun getTheme(): Boolean

    fun setAndSaveTheme(isDarkThemeOn: Boolean)

    fun setSavedTheme()
}