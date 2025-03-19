package com.example.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSettings
import com.example.playlistmaker.domain.api.interactor.SettingsInteractor

class App : Application() {
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        CreatorSettings.initApplication(this)
        CreatorExternalNavigator.initApplication(this)
        CreatorHistory.initApplication(this)
        settingsInteractor = CreatorSettings.provideSettingsInteractor()
        setSavedTheme()
    }

    private fun setSavedTheme() {
        val isDarkTheme = settingsInteractor.getTheme()
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        settingsInteractor.saveTheme(darkThemeEnabled)
    }

    companion object {
        const val INTENT_TRACK_KEY = "track"
        const val BAD_REQUEST_CODE = 400
        const val SUCCESS_CODE = 200
        const val INTERNAL_SERVER_ERROR_CODE = 500
    }
}