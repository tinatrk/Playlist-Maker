package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val isDefaultThemeDark: Boolean,
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, isDefaultThemeDark)
    }

    override fun saveTheme(isDarkThemeOn: Boolean) {
        sharedPreferences.edit()
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