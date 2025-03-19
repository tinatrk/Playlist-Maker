package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.impl.TrackInteractorSearchImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CreatorSearch {
    private const val BASE_URL_ITUNES = "https://itunes.apple.com"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getItunesApiService(): ItunesApiService {
        return getRetrofit().create(ItunesApiService::class.java)
    }

    private fun getTrackRepositorySearch(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(getItunesApiService()))
    }

    fun provideTrackInteractorSearch(): TrackInteractorSearch {
        return TrackInteractorSearchImpl(getTrackRepositorySearch())
    }
}