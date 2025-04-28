package com.example.playlistmaker.di

import com.example.playlistmaker.library.presentation.view_model.FavoritesFragmentViewModel
import com.example.playlistmaker.library.presentation.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(trackInteractorSearch = get(), trackInteractorHistory = get())
    }

    viewModel {
        SettingsViewModel(externalNavigatorInteractor = get(), settingsInteractor = get())
    }

    viewModel { (trackId: Int) ->
        PlayerViewModel(
            trackId = trackId,
            audioPlayerInteractor = get(),
            trackInteractorHistory = get()
        )
    }

    viewModel{
        FavoritesFragmentViewModel()
    }

    viewModel {
        PlaylistsFragmentViewModel()
    }
}