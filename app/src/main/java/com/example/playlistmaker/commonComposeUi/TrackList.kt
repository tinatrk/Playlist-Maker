package com.example.playlistmaker.commonComposeUi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TrackList(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, onTrackClick)
        }
    }
}

@Composable
fun TrackItem(track: Track, onTrackClick: (Track) -> Unit) {
    val trackDuration = if (track.trackTimeMillis != -1) {
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
    } else {
        stringResource(R.string.message_nothing_found)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp)
            .clickable {
                onTrackClick(track)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TrackCoverMini(track.artworkUrl100)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = track.trackName,
                style = AppTheme.typography.h4,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = track.artistName,
                    style = AppTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_text_delimiter_13),
                    contentDescription = "",
                    tint = AppTheme.colors.colorOnTertiary
                )
                Text(
                    text = trackDuration,
                    style = AppTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = stringResource(R.string.open_player_desc),
            tint = AppTheme.colors.colorOnTertiary,
        )
    }
}

@Composable
fun TrackCoverMini(url: String) {
    AsyncImage(
        model = url,
        contentDescription = stringResource(R.string.track_cover_desc),
        placeholder = painterResource(R.drawable.ic_placeholder_45),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(2.dp)),
        error = painterResource(R.drawable.ic_placeholder_45)
    )
}

@Composable
fun TrackListWithButton(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    buttonTitle: String,
    onButtonClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, onTrackClick)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            CommonButton(title = buttonTitle, onClick = onButtonClick)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}