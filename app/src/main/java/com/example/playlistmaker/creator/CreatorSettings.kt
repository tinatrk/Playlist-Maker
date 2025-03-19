package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.localStoage.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.domain.api.repository.SettingsRepository
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl

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