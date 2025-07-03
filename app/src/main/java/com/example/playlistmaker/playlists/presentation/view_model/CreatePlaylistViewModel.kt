package com.example.playlistmaker.playlists.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.CreatePlaylistsScreenState
import com.example.playlistmaker.playlists.presentation.models.PlaylistCreationState
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _screenStateFlow =
        MutableStateFlow<CreatePlaylistsScreenState>(CreatePlaylistsScreenState.Empty)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val onCreateClickedLiveData = SingleEventLiveData<PlaylistCreationState>()
    fun observeOnCreateClickedLiveData(): LiveData<PlaylistCreationState> = onCreateClickedLiveData

    private val onBackClickedLiveData = SingleEventLiveData<Boolean>()
    fun observeOnBackClickedLiveData(): LiveData<Boolean> = onBackClickedLiveData

    private var coverUri: String = EMPTY_STRING

    fun setCover(uri: String) {
        coverUri = uri
        _screenStateFlow.update { CreatePlaylistsScreenState.Content(uri) }
    }

    fun backClicked(title: String, description: String) {
        onBackClickedLiveData.value = title.isNotEmpty()
                || description.isNotEmpty()
                || coverUri != EMPTY_STRING
    }

    fun createPlaylist(playlistTitle: String, playlistDescription: String, storagePath: File) {
        viewModelScope.launch {
            var isNeedCreate: Boolean = true

            val checkJob = launch {
                playlistInteractor.checkPlaylistExistence(playlistTitle)
                    .collect { isPlaylistExists ->
                        if (isPlaylistExists) {
                            isNeedCreate = false
                            onCreateClickedLiveData.postValue(
                                PlaylistCreationState.AlreadyExists(
                                    playlistTitle
                                )
                            )
                        }
                    }
            }

            checkJob.join()

            launch {
                if (isNeedCreate) {
                    val filePath: File? = if (coverUri != EMPTY_STRING) {
                        File(storagePath, playlistTitle)
                    } else null
                    playlistInteractor.createNewPlaylist(
                        Playlist(
                            title = playlistTitle,
                            description = playlistDescription,
                            coverPath = filePath?.toString() ?: EMPTY_STRING,
                            tracksIds = listOf(),
                            tracksCount = 0
                        )
                    )
                    onCreateClickedLiveData.postValue(
                        PlaylistCreationState.SuccessCreated(
                            playlistTitle,
                            coverUri,
                            filePath
                        )
                    )
                }
            }
        }
    }
}