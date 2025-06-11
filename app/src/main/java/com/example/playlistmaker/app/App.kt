package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val settingsInteractor: SettingsInteractor = getKoin().get()

        settingsInteractor.setSavedTheme()
    }

    companion object {
        const val DI_SETTINGS_SP_NAME = "settings_shared_preferences"
        const val DI_HISTORY_SP_NAME = "history_shared_preferences"
        const val UNKNOWN_ID = -1
        const val DEFAULT_STRING = "Ничего не нашлось"
        const val DEFAULT_LINK = ""
        const val DEFAULT_INT = -1
    }
}