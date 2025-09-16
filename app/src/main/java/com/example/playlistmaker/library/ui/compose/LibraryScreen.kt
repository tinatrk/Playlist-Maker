package com.example.playlistmaker.library.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.ui.compose.PlaylistsScreen
import com.example.playlistmaker.commonComposeUi.Toolbar
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.favorites.ui.compose.FavoritesScreen
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
    navigateToAudioPlayerScreen: (Track) -> Unit,
    navigateToModifyPlaylistScreen: () -> Unit,
    navigateToOnePlaylistScreen: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.library_screen_title)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = AppTheme.colors.colorPrimary),
            verticalArrangement = Arrangement.Top
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AppTheme.colors.colorPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(currentTabPosition = tabPositions[selectedTabIndex.value])
                            .padding(horizontal = 16.dp),
                        color = AppTheme.colors.colorOnPrimary,
                        height = 2.dp,
                    )
                },
                divider = {
                    TabRowDefaults.Divider(
                        color = AppTheme.colors.colorPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex.value == LibraryTabs.Favorites.ordinal,
                    selectedContentColor = colorResource(R.color.red_300),
                    unselectedContentColor = colorResource(R.color.blue_200),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(LibraryTabs.Favorites.ordinal)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.favorite_tracks),
                            style = AppTheme.typography.h6
                        )
                    }
                )

                Tab(
                    selected = selectedTabIndex.value == LibraryTabs.Playlists.ordinal,
                    selectedContentColor = colorResource(R.color.red_300),
                    unselectedContentColor = colorResource(R.color.blue_200),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(LibraryTabs.Playlists.ordinal)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.playlists),
                            style = AppTheme.typography.h6
                        )
                    }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    LibraryTabs.Favorites.ordinal -> FavoritesScreen(
                        viewModel = koinViewModel(),
                        navigateToAudioPlayerScreen = navigateToAudioPlayerScreen
                    )

                    LibraryTabs.Playlists.ordinal -> PlaylistsScreen(
                        viewModel = koinViewModel(),
                        navigateToModifyPlaylistScreen = navigateToModifyPlaylistScreen,
                        navigateToOnePlaylistScreen = navigateToOnePlaylistScreen
                    )
                }
            }
        }
    }
}

enum class LibraryTabs {
    Favorites,
    Playlists
}