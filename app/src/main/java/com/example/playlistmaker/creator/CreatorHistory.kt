package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.history.domain.impl.TrackInteractorHistoryImpl
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.repository.TrackRepository

object CreatorHistory {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getSharedPreference() : SharedPreferences {
        return application.getSharedPreferences(
            HISTORY_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    private fun getTrackRepositoryHistory(): TrackRepository {
        return TrackRepositoryImpl(sharedPrefs = getSharedPreference())
    }

    fun provideTrackInteractorHistory(): TrackInteractorHistory {
        return TrackInteractorHistoryImpl(getTrackRepositoryHistory())
    }

    private const val HISTORY_SHARED_PREFERENCES_FILE = "shared_preferences_history"
}