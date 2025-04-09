package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.app.App.Companion.DI_HISTORY_SP_NAME
import com.example.playlistmaker.app.App.Companion.DI_SETTINGS_SP_NAME
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.api.ItunesApiService
import com.example.playlistmaker.search.data.impl.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL_ITUNES = "https://itunes.apple.com"
private const val SETTINGS_SHARED_PREFERENCES_FILE = "shared_preferences_settings"
private const val HISTORY_SHARED_PREFERENCES_FILE = "shared_preferences_history"


val dataModule = module {
    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single(named(DI_HISTORY_SP_NAME)) {
        androidApplication().getSharedPreferences(
            HISTORY_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE
        )
    }

    single(named(DI_SETTINGS_SP_NAME)) {
        androidApplication().getSharedPreferences(
            SETTINGS_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE
        )
    }

    factory {
        Gson()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidApplication())
    }

    factory {
        MediaPlayer()
    }
}