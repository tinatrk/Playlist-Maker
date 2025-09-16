package com.example.playlistmaker.playlists.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.commonComposeUi.CommonButton
import com.example.playlistmaker.commonComposeUi.ErrorMessageWithIcon
import com.example.playlistmaker.commonComposeUi.LoadingProgressBar
import com.example.playlistmaker.commonComposeUi.PlaylistsGrid
import com.example.playlistmaker.playlists.presentation.models.PlaylistsScreenState
import com.example.playlistmaker.playlists.presentation.view_model.PlaylistsViewModel

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    navigateToModifyPlaylistScreen: () -> Unit,
    navigateToOnePlaylistScreen: (Int) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updatePlaylists()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (screenState) {
            is PlaylistsScreenState.Loading -> LoadingProgressBar()

            is PlaylistsScreenState.Content -> {
                CommonPlaylistsContent(onClick = navigateToModifyPlaylistScreen)
                PlaylistsGrid(
                    playlists = (screenState as PlaylistsScreenState.Content).playlists,
                    onPlaylistClick = navigateToOnePlaylistScreen
                )
            }

            is PlaylistsScreenState.Empty -> {
                CommonPlaylistsContent(onClick = navigateToModifyPlaylistScreen)
                ErrorMessageWithIcon(
                    message = stringResource(R.string.empty_playlists_message),
                    iconId = R.drawable.ic_placeholder_nothing_found_120,
                    topPaddingDp = 46
                )
            }
        }
    }
}

@Composable
fun CommonPlaylistsContent(onClick: () -> Unit) {
    Spacer(modifier = Modifier.padding(top = 24.dp))
    CommonButton(stringResource(R.string.btn_new_playlist)) {
        onClick()
    }
}



