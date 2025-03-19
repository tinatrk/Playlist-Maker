package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.externalNavigator.ExternalNavigatorImpl
import com.example.playlistmaker.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.domain.api.repository.ExternalNavigator
import com.example.playlistmaker.domain.impl.ExternalNavigatorInteractorImpl

object CreatorExternalNavigator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    fun provideExternalNavigatorInteractor(): ExternalNavigatorInteractor {
        return ExternalNavigatorInteractorImpl(getExternalNavigator())
    }
}