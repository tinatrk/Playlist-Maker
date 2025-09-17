package com.example.playlistmaker.favorites.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.commonComposeUi.ErrorMessageWithIcon
import com.example.playlistmaker.commonComposeUi.LoadingProgressBar
import com.example.playlistmaker.commonComposeUi.TrackList
import com.example.playlistmaker.favorites.presentation.models.FavoritesScreenState
import com.example.playlistmaker.favorites.presentation.models.NavigationEvent
import com.example.playlistmaker.favorites.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    navigateToAudioPlayerScreen: (Track) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsStateWithLifecycle()
    val navigationState = viewModel.navigationEvent.collectAsStateWithLifecycle(initialValue = null)

    LifecycleStartEffect(Unit) {
        viewModel.updateFavoriteTracks()
        onStopOrDispose { }
    }

    LaunchedEffect(navigationState.value) {
        navigationState.value.let { event ->
            when (event) {
                is NavigationEvent.OpenAudioPlayer -> {
                    navigateToAudioPlayerScreen(event.track)
                }

                else -> {}
            }
        }
    }

    when (screenState) {
        is FavoritesScreenState.Loading -> {
            LoadingProgressBar()
        }

        is FavoritesScreenState.Content -> {
            TrackList(
                (screenState as FavoritesScreenState.Content).tracks.toImmutableList(),
                viewModel::onTrackClicked
            )
        }

        is FavoritesScreenState.Empty -> {
            ErrorMessageWithIcon(
                message = stringResource(R.string.empty_favorite_tracks_message),
                iconId = R.drawable.ic_placeholder_nothing_found_120,
                topPaddingDp = 106
            )
        }
    }
}


