package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.search.data.api.ItunesApiService
import com.example.playlistmaker.search.data.impl.RetrofitNetworkClient
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.impl.TrackInteractorSearchImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CreatorSearch {
    private lateinit var application: Application

    private const val BASE_URL_ITUNES = "https://itunes.apple.com"

    fun initApplication(application: Application) {
        this.application = application
    }

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
        return TrackRepositoryImpl(RetrofitNetworkClient(getItunesApiService(), application))
    }

    fun provideTrackInteractorSearch(): TrackInteractorSearch {
        return TrackInteractorSearchImpl(getTrackRepositorySearch())
    }
}