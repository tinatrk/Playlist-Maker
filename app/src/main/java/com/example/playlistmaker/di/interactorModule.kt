package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.domain.api.interactor.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.history.domain.api.interactor.TrackInteractorHistory
import com.example.playlistmaker.history.domain.impl.TrackInteractorHistoryImpl
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.search.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.search.domain.impl.TrackInteractorSearchImpl
import com.example.playlistmaker.settings.domain.api.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<TrackInteractorSearch> {
        TrackInteractorSearchImpl(trackRepository = get())
    }

    single<TrackInteractorHistory> {
        TrackInteractorHistoryImpl(trackRepository = get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    single<ExternalNavigatorInteractor> {
        ExternalNavigatorInteractorImpl(externalNavigator = get())
    }

    single<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(audioPlayerRepository = get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(favoritesRepository = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }
}