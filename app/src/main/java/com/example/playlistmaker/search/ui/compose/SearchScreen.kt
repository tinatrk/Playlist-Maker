package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.commonComposeUi.CommonButton
import com.example.playlistmaker.commonComposeUi.CustomTextField
import com.example.playlistmaker.commonComposeUi.ErrorMessageWithIcon
import com.example.playlistmaker.commonComposeUi.LoadingProgressBar
import com.example.playlistmaker.commonComposeUi.Toolbar
import com.example.playlistmaker.commonComposeUi.TrackList
import com.example.playlistmaker.commonComposeUi.TrackListWithButton
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.model.NavigationEvent
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    navigateToAudioPlayerScreen: (Track) -> Unit
) {
    val screenState by viewModel.screenStateFlow.collectAsStateWithLifecycle()
    val navigationState = viewModel.navigationEvent.collectAsStateWithLifecycle(initialValue = null)

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LifecycleStartEffect(Unit) {
        viewModel.updateSearchResults()
        viewModel.updateHistory()
        onStopOrDispose { }
    }

    LaunchedEffect(navigationState.value) {
        navigationState.value.let { event ->
            when (event) {
                is NavigationEvent.OpenAudioPlayer -> {
                    focusManager.clearFocus()
                    navigateToAudioPlayerScreen(event.track)
                }

                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.search_screen_title)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .background(color = AppTheme.colors.colorPrimary)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                text = screenState.searchText,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_16),
                        tint = AppTheme.colors.colorOnSecondary,
                        contentDescription = stringResource(R.string.search_screen_title),
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear_16),
                        tint = AppTheme.colors.colorOnSecondary,
                        contentDescription = stringResource(R.string.edit_text_btn_close_desc),
                        modifier = Modifier.clickable {
                            focusManager.clearFocus()
                            viewModel.clearSearchRequest()
                        })
                },
                placeholderText = stringResource(R.string.search_screen_title),
                onTextChanged = { newText -> viewModel.onSearchLineTextChanged(newText) },
                onFocusChanged = { isFocused -> viewModel.onSearchLineFocusChanged(isFocused) })

            when (screenState) {
                is SearchScreenState.Default -> {}
                is SearchScreenState.EnteringRequest -> {}

                is SearchScreenState.Loading -> {
                    LoadingProgressBar()
                }

                is SearchScreenState.Content -> {
                    TrackList(
                        tracks = (screenState as SearchScreenState.Content).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClicked
                    )
                }

                is SearchScreenState.Error -> {
                    SearchScreenError(
                        errorType = (screenState as SearchScreenState.Error).errorType,
                        onUpdateClick = viewModel::searchTrack
                    )
                }

                is SearchScreenState.History -> {
                    SearchHistoryBlock(
                        tracks = (screenState as SearchScreenState.History).tracks.toImmutableList(),
                        onTrackClick = viewModel::onTrackClicked,
                        onClearHistoryClick = viewModel::clearHistory
                    )
                }
            }
        }
    }
}

@Composable
fun SearchScreenError(errorType: ErrorType, onUpdateClick: () -> Unit) {
    val topPaddingDp = 110

    when (errorType) {
        is ErrorType.NoNetworkConnection -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ErrorMessageWithIcon(
                    message = stringResource(R.string.message_bad_connection),
                    iconId = R.drawable.ic_placeholder_bad_connection_120,
                    topPaddingDp = topPaddingDp
                )
                Spacer(modifier = Modifier.height(24.dp))
                CommonButton(stringResource(R.string.update), onClick = onUpdateClick)
            }

        }

        is ErrorType.EmptyResult -> {
            ErrorMessageWithIcon(
                message = stringResource(R.string.message_nothing_found),
                iconId = R.drawable.ic_placeholder_nothing_found_120,
                topPaddingDp = topPaddingDp
            )
        }

        else -> {
            ErrorMessageWithIcon(
                message = stringResource(R.string.message_something_went_wrong),
                iconId = R.drawable.ic_placeholder_nothing_found_120,
                topPaddingDp = topPaddingDp
            )
        }
    }
}

@Composable
fun SearchHistoryBlock(
    tracks: ImmutableList<Track>, onTrackClick: (Track) -> Unit, onClearHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = stringResource(R.string.history_title),
            style = AppTheme.typography.h2,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        TrackListWithButton(
            tracks = tracks,
            onTrackClick = onTrackClick,
            buttonTitle = stringResource(R.string.history_clear_btn),
            onButtonClick = onClearHistoryClick
        )
    }
}


