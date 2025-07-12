package com.example.playlistmaker.di

import com.example.playlistmaker.favorites.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.player.presentation.mapper.PlayerPresenterTrackMapper
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.playlists.presentation.view_model.ModifyPlaylistViewModel
//import com.example.playlistmaker.playlists.presentation.view_model.EditPlaylistViewModel
import com.example.playlistmaker.playlists.presentation.view_model.OnePlaylistViewModel
import com.example.playlistmaker.playlists.presentation.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import com.example.playlistmaker.util.ResourceProvider
import org.koin.android.ext.koin.androidContext
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
            favoritesInteractor = get(),
            playlistInteractor = get()
        )
    }

    viewModel {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel {
        PlaylistsViewModel(playlistInteractor = get())
    }

    factory {
        PlayerPresenterTrackMapper()
    }

    viewModel { (playlistId: Int) ->
        ModifyPlaylistViewModel(playlistInteractor = get(), playlistId = playlistId)
    }

    viewModel { (playlistId: Int) ->
        OnePlaylistViewModel(
            playlistId = playlistId,
            playlistsInteractor = get(),
            resourceProvider = get(),
            externalNavigatorInteractor = get()
        )
    }

    single {
        ResourceProvider(context = androidContext())
    }
}