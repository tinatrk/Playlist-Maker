package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSearch
import com.example.playlistmaker.creator.CreatorSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CreatorSearch.initApplication(this)
        CreatorSettings.initApplication(this)
        CreatorExternalNavigator.initApplication(this)
        CreatorHistory.initApplication(this)

        val settingsInteractor = CreatorSettings.provideSettingsInteractor()
        settingsInteractor.setSavedTheme()
    }

    companion object {
        const val INTENT_TRACK_KEY = "track"
    }
}