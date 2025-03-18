package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorHistoryImpl

object CreatorHistory {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTrackRepositoryHistory(): TrackRepository {
        return TrackRepositoryImpl(application = application)
    }

    fun provideTrackInteractorHistory(): TrackInteractorHistory {
        return TrackInteractorHistoryImpl(getTrackRepositoryHistory())
    }
}