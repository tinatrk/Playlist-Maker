package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.history.domain.impl.TrackInteractorHistoryImpl
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.repository.TrackRepository

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