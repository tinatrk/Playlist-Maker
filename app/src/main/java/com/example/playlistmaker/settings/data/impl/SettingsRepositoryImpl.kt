package com.example.playlistmaker.settings.data.impl

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.repository.SettingsRepository

class SettingsRepositoryImpl(
    //private val application: Application,
    private val isDefaultThemeDark: Boolean,
    private val sharedPrefs: SharedPreferences
) : SettingsRepository {

    override fun getTheme(): Boolean {
        /*val isDefaultThemeDark = application.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES*/
        return sharedPrefs.getBoolean(DARK_THEME_KEY, isDefaultThemeDark)
    }

    override fun saveTheme(isDarkThemeOn: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, isDarkThemeOn)
            .apply()
    }

    override fun setSavedTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getTheme()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        private const val DARK_THEME_KEY = "is_dark_theme_on"
    }
}