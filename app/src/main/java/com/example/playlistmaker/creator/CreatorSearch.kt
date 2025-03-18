package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorSearchImpl

object CreatorSearch {

    private fun getTrackRepositorySearch(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractorSearch(): TrackInteractorSearch {
        return TrackInteractorSearchImpl(getTrackRepositorySearch())
    }
}