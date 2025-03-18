package com.example.playlistmaker.domain.api.interactor

interface SettingsInteractor {
    fun getTheme(): Boolean

    fun saveTheme(isDarkThemeOn: Boolean)

}