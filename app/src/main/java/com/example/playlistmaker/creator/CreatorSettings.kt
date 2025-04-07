package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import kotlin.properties.Delegates

object CreatorSettings {
    private lateinit var application: Application
    private var isDefaultThemeDark by Delegates.notNull<Boolean>()

    fun initApplication(application: Application) {
        this.application = application
    }

    fun initDefaultTheme(isDefaultThemeDark: Boolean) {
        this.isDefaultThemeDark = isDefaultThemeDark
    }

    private fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(
            SETTINGS_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE
        )
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(isDefaultThemeDark, getSharedPreferences())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private const val SETTINGS_SHARED_PREFERENCES_FILE = "shared_preferences_settings"
}