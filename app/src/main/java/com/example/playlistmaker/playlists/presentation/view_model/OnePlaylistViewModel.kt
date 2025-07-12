package com.example.playlistmaker.playlists.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.OnePlaylistDetails
import com.example.playlistmaker.playlists.presentation.models.OnePlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.interactor.ExternalNavigatorInteractor
import com.example.playlistmaker.util.ResourceProvider
import com.example.playlistmaker.util.SingleEventLiveData
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class OnePlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistInteractor,
    private val resourceProvider: ResourceProvider,
    private val externalNavigatorInteractor: ExternalNavigatorInteractor
) : ViewModel() {

    private lateinit var playlist: Playlist
    private lateinit var tracks: List<Track>

    private val _screenStateFlow =
        MutableStateFlow<OnePlaylistScreenState>(OnePlaylistScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val onTrackClickedLiveData = SingleEventLiveData<Track>()
    fun observeOnTrackClickedLiveData(): LiveData<Track> = onTrackClickedLiveData

    private val toastLiveData = SingleEventLiveData<String>()
    fun observeToastLiveData(): LiveData<String> = toastLiveData

    private val deletingPlaylistEventLiveData = SingleEventLiveData<Boolean>()
    fun observeDeletingPlaylistEventLiveData(): LiveData<Boolean> = deletingPlaylistEventLiveData

    init {
        updatePlaylistDetails()
    }

    fun updatePlaylistDetails() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(playlistId).collect { resultPlaylist ->
                playlist = resultPlaylist
            }

            playlistsInteractor.getTracksByIds(playlist.tracksIds).collect { resultTracks ->
                tracks = resultTracks
                _screenStateFlow.update {
                    OnePlaylistScreenState.Content(
                        OnePlaylistDetails(
                            title = playlist.title,
                            description = playlist.description,
                            coverPath = playlist.coverPath,
                            tracks = resultTracks,
                            tracksCountString = getPlurals(
                                resultTracks.size,
                                resourceProvider.getString(R.string.one_track),
                                resourceProvider.getString(R.string.few_tracks),
                                resourceProvider.getString(R.string.other_tracks)
                            ),
                            sumTracksDuration = getPlurals(
                                getSumTracksDuration(resultTracks),
                                resourceProvider.getString(R.string.one_minute),
                                resourceProvider.getString(R.string.few_minutes),
                                resourceProvider.getString(R.string.other_minutes)
                            )
                        )
                    )
                }
            }
        }
    }

    private val onTrackClickedDebounce: (Track) -> Unit =
        debounce<Track>(ON_TRACK_CLICK_DELAY_MILLIS, viewModelScope, false) { track ->
            onTrackClickedLiveData.value = track
        }

    private fun getSumTracksDuration(tracks: List<Track>): Int {
        val tracksDuration = tracks.map { it.trackTimeMillis }
        val sumMillis = tracksDuration.sum()

        return sumMillis / MILLIS_PER_MINUTE
    }

    fun onTrackClicked(track: Track) {
        onTrackClickedDebounce(track)
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(playlist, trackId)
            updatePlaylistDetails()
        }
    }

    private fun getPlurals(
        elementCount: Int,
        stringForOneElement: String,
        stringForFewElements: String,
        stringForOtherElements: String
    ): String {
        return when {
            (elementCount % 10 == 1) ->
                "$elementCount $stringForOneElement"

            (elementCount % 10 in 2..4) ->
                "$elementCount $stringForFewElements"

            else ->
                "$elementCount $stringForOtherElements"
        }
    }

    fun sharePlaylist() {
        if (playlist.tracksIds.isEmpty()) toastLiveData.value =
            resourceProvider.getString(R.string.toast_nothing_tracks_for_sharing_playlist)
        else {
            externalNavigatorInteractor.shareLink(getMessageForSharing())
        }
    }

    private fun getMessageForSharing(): String {
        val messageBuilder = StringBuilder()
        messageBuilder.append("${playlist.title}\n")
        if (playlist.description != EMPTY_STRING) messageBuilder.append("${playlist.description}\n")

        val trackCountsString = getPlurals(
            tracks.size,
            resourceProvider.getString(R.string.one_track),
            resourceProvider.getString(R.string.few_tracks),
            resourceProvider.getString(R.string.other_tracks)
        )
        messageBuilder.append("$trackCountsString\n")

        for (i in 0 until playlist.tracksCount) {
            messageBuilder.append(
                "${i + 1}. ${tracks[i].artistName} - " +
                        "${tracks[i].trackName} " +
                        "(${getTrackTimeString(tracks[i].trackTimeMillis)})\n"
            )
        }

        return messageBuilder.toString()
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlist)
            deletingPlaylistEventLiveData.postValue(true)
        }
    }

    private fun getTrackTimeString(trackTimeMillis: Int): String =
        if (trackTimeMillis != DEFAULT_INT) {
            SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
        } else {
            DEFAULT_STRING
        }

    companion object {
        private const val MILLIS_PER_MINUTE = 60_000
        private const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
        private const val TRACK_TIME_FORMAT = "mm:ss"
    }
}