package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.playlists.presentation.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(
            trackInteractorSearch = get(),
            trackInteractorHistory = get(),
            favoritesInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(externalNavigatorInteractor = get(), settingsInteractor = get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            audioPlayerInteractor = get(),
            trackMapper = get(),
            favoritesInteractor = get()
        )
    }

    viewModel {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel {
        PlaylistsViewModel()
    }

    factory {
        PlayerPresenterTrackMapper()
    }
}