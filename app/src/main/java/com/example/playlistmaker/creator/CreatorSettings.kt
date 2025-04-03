package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl

object CreatorSettings {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(application)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}