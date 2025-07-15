package com.example.playlistmaker.playlists.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.playlists.domain.api.interactor.PlaylistInteractor
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.ModifyPlaylistScreenState
import com.example.playlistmaker.playlists.presentation.models.PlaylistCoverModificationType
import com.example.playlistmaker.playlists.presentation.models.PlaylistModificationState
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModifyPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    private lateinit var playlist: Playlist

    private val _screenStateFlow =
        MutableStateFlow<ModifyPlaylistScreenState>(ModifyPlaylistScreenState.Init())
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val onModifyClickedLiveData = SingleEventLiveData<PlaylistModificationState>()
    fun observeOnCreateClickedLiveData(): LiveData<PlaylistModificationState> =
        onModifyClickedLiveData

    private val onBackClickedLiveData = SingleEventLiveData<Boolean>()
    fun observeOnBackClickedLiveData(): LiveData<Boolean> = onBackClickedLiveData

    private var coverUri: String = EMPTY_STRING
    private var oldCover: String = EMPTY_STRING

    init {
        if (playlistId != UNKNOWN_ID) {
            viewModelScope.launch {
                playlistInteractor.getPlaylistById(playlistId).collect { resultPlaylist ->
                    playlist = resultPlaylist
                    coverUri = playlist.coverPath
                    oldCover = coverUri

                    _screenStateFlow.update {
                        ModifyPlaylistScreenState.Init(
                            coverUri = playlist.coverPath,
                            title = playlist.title,
                            description = playlist.description
                        )
                    }
                }
            }
        }
    }

    fun setCover(uri: String) {
        oldCover = coverUri
        coverUri = uri
        _screenStateFlow.update { ModifyPlaylistScreenState.CoverContent(uri) }
    }

    fun backClicked(title: String, description: String) {
        onBackClickedLiveData.value = title.isNotEmpty()
                || description.isNotEmpty()
                || coverUri != EMPTY_STRING
    }

    fun createPlaylist(
        playlistTitle: String,
        playlistDescription: String,
        newCoverPath: String
    ) {
        val coverPath: String = if (coverUri == EMPTY_STRING) EMPTY_STRING else newCoverPath
        viewModelScope.launch {
            var isNeedCreate: Boolean = true

            val checkJob = launch {
                playlistInteractor.checkPlaylistExistence(playlistTitle, playlistId)
                    .collect { isPlaylistExists ->
                        if (isPlaylistExists) {
                            isNeedCreate = false
                            onModifyClickedLiveData.postValue(
                                PlaylistModificationState.AlreadyExists(
                                    playlistTitle
                                )
                            )
                        }
                    }
            }

            checkJob.join()

            launch {
                if (isNeedCreate) {
                    playlistInteractor.createNewPlaylist(
                        Playlist(
                            title = playlistTitle,
                            description = playlistDescription,
                            coverPath = coverPath,
                            tracksIds = listOf(),
                            tracksCount = 0
                        )
                    )
                    val coverModificationType: PlaylistCoverModificationType =
                        if (coverUri == EMPTY_STRING) PlaylistCoverModificationType.NotChanged
                        else PlaylistCoverModificationType.ChangedContent(
                            newContentUri = coverUri,
                            coverName = playlistTitle
                        )
                    onModifyClickedLiveData.postValue(
                        PlaylistModificationState.SuccessCreated(
                            playlistTitle = playlistTitle,
                            coverUri = coverUri,
                            filePath = coverPath,
                            coverModification = coverModificationType
                        )
                    )
                }
            }
        }
    }

    fun updatePlaylist(
        playlistTitle: String,
        playlistDescription: String,
        newCoverPath: String
    ) {
        var isNewCover = false
        val oldTitle = playlist.title
        val coverPath: String = if (coverUri == EMPTY_STRING) EMPTY_STRING else newCoverPath

        var modificationType = 0
        modificationType += if (oldTitle != playlistTitle) TITLE_CHANGED else WITHOUT_CHANGES
        modificationType += if (playlist.description != playlistDescription) DESCRIPTION_CHANGED else WITHOUT_CHANGES
        modificationType += if (oldCover != coverUri) COVER_CONTENT_CHANGED else WITHOUT_CHANGES

        if (modificationType > WITHOUT_CHANGES) {
            viewModelScope.launch {
                var isNeedUpdate: Boolean = true

                val checkJob = launch {
                    playlistInteractor.checkPlaylistExistence(playlistTitle, playlistId)
                        .collect { isPlaylistExists ->
                            if (isPlaylistExists) {
                                isNeedUpdate = false
                                onModifyClickedLiveData.postValue(
                                    PlaylistModificationState.AlreadyExists(
                                        playlistTitle
                                    )
                                )
                            }
                        }
                }

                checkJob.join()

                launch {
                    if (isNeedUpdate) {
                        playlistInteractor.updatePlaylist(
                            playlist.copy(
                                title = playlistTitle,
                                description = playlistDescription,
                                coverPath = coverPath
                            )
                        )
                        val coverModificationType: PlaylistCoverModificationType =
                            when (modificationType) {
                                in WITHOUT_CHANGES..DESCRIPTION_CHANGED ->
                                    PlaylistCoverModificationType.NotChanged

                                in TITLE_CHANGED until COVER_CONTENT_CHANGED ->
                                    PlaylistCoverModificationType.Renamed(
                                        oldName = oldTitle,
                                        newName = playlistTitle
                                    )

                                in COVER_CONTENT_CHANGED until TITLE_AND_COVER_CONTENT_CHANGED ->
                                    PlaylistCoverModificationType.ChangedContent(
                                        newContentUri = coverUri,
                                        coverName = playlistTitle
                                    )

                                else -> PlaylistCoverModificationType.RenamedAndChangedContent(
                                    oldName = oldTitle,
                                    newName = playlistTitle,
                                    newContentUri = coverUri
                                )
                            }

                        onModifyClickedLiveData.postValue(
                            PlaylistModificationState.SuccessUpdated(
                                oldTitle = oldTitle,
                                newTitle = playlistTitle,
                                coverUri = coverUri,
                                filePath = coverPath,
                                coverModification = coverModificationType
                            )
                        )
                    }
                }
            }
        } else onModifyClickedLiveData.value = PlaylistModificationState.NothingChanged
    }

    companion object {
        private const val WITHOUT_CHANGES = 0
        private const val DESCRIPTION_CHANGED = 1
        private const val TITLE_CHANGED = 2
        private const val COVER_CONTENT_CHANGED = 4
        private const val TITLE_AND_COVER_CONTENT_CHANGED = 6
    }
}