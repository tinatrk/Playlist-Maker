package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var isDarkTheme = false
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
        val isDarkModeOn = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
        isDarkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, isDarkModeOn)
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object {
        const val SHARED_PREFERENCES_FILE = "shared_preferences"
        const val DARK_THEME_KEY = "is_dark_theme"
    }
}