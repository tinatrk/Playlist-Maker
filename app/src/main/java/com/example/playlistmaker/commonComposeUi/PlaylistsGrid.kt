package com.example.playlistmaker.commonComposeUi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.playlists.domain.models.Playlist

@Composable
fun PlaylistsGrid(playlists: List<Playlist>, onPlaylistClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(top = 12.dp, bottom = 60.dp)
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        items(playlists) { playlist ->
            PlaylistsGridItem(playlist, onPlaylistClick)
        }
    }
}

@Composable
fun PlaylistsGridItem(playlist: Playlist, onPlaylistClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable {
                onPlaylistClick(playlist.id)
            }
    ) {
        PlaylistCover(
            url = playlist.coverPath,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        Text(
            text = playlist.title,
            style = AppTheme.typography.caption,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        TrackPluralsText(playlist.tracksCount)
    }
}

@Composable
fun PlaylistCover(url: String, modifier: Modifier) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.playlist_cover_description),
        placeholder = painterResource(R.drawable.ic_placeholder_45),
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        error = painterResource(R.drawable.ic_placeholder_45)
    )
}

@Composable
fun TrackPluralsText(trackCount: Int) {

    val oneTrack = stringResource(R.string.one_track)
    val fewTracks = stringResource(R.string.few_tracks)
    val manyTracks = stringResource(R.string.other_tracks)

    val trackCountString = remember {
        when {
            (trackCount % 10 == 1) ->
                "$trackCount $oneTrack"

            (trackCount % 10 in 2..4) ->
                "$trackCount $fewTracks"

            else ->
                "$trackCount $manyTracks"
        }
    }

    Text(
        text = trackCountString,
        style = AppTheme.typography.caption,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

