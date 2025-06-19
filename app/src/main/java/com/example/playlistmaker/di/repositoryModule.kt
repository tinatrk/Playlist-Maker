package com.example.playlistmaker.di

import android.content.res.Configuration
import com.example.playlistmaker.app.App.Companion.DI_HISTORY_SP_NAME
import com.example.playlistmaker.app.App.Companion.DI_SETTINGS_SP_NAME
import com.example.playlistmaker.favorites.data.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.favorites.data.mapper.TrackDbMapper
import com.example.playlistmaker.favorites.domain.api.repository.FavoritesRepository
import com.example.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.search.data.mapper.SearchRepositoryTrackMapper
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.repository.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.repository.ExternalNavigator
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(
            networkClient = get(),
            sharedPreferences = get(named(DI_HISTORY_SP_NAME)),
            gson = get(),
            trackMapper = get(),
            appDatabase = get()
        )
    }

    single<SettingsRepository> {
        val isDefaultThemeDark = androidApplication().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        SettingsRepositoryImpl(
            isDefaultThemeDark,
            sharedPreferences = get(named(DI_SETTINGS_SP_NAME))
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidApplication())
    }

    single<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(mediaPlayer = get())
    }

    factory {
        SearchRepositoryTrackMapper()
    }

   single<FavoritesRepository>{
        FavoritesRepositoryImpl(appDatabase = get(), trackMapper = get())
    }

    factory {
        TrackDbMapper()
    }
}