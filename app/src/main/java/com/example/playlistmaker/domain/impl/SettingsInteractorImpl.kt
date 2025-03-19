package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.api.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getTheme(): Boolean {
        return repository.getTheme()
    }

    override fun saveTheme(isDarkThemeOn: Boolean) {
        repository.saveTheme(isDarkThemeOn)
    }
}