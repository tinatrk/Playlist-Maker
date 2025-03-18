package com.example.playlistmaker.data.localStoage

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.playlistmaker.domain.api.repository.SettingsRepository

class SettingsRepositoryImpl(private val application: Application) : SettingsRepository {
    private val sharedPrefs: SharedPreferences = application.getSharedPreferences(
        SETTINGS_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE
    )

    override fun getTheme(): Boolean {
        val isDefaultThemeDark = application.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        return sharedPrefs.getBoolean(DARK_THEME_KEY, isDefaultThemeDark)
    }

    override fun saveTheme(isDarkThemeOn: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, isDarkThemeOn)
            .apply()
    }

    companion object {
        private const val DARK_THEME_KEY = "is_dark_theme_on"
        private const val SETTINGS_SHARED_PREFERENCES_FILE = "shared_preferences_settings"
    }
}