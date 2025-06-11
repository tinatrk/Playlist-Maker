package com.example.playlistmaker.playlists.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.playlists.presentation.models.PlaylistsScreenState

class PlaylistsViewModel() : ViewModel() {
    private val screenStateLiveData: MutableLiveData<PlaylistsScreenState> =
        MutableLiveData(PlaylistsScreenState.Empty)

    fun screenStateObserve(): LiveData<PlaylistsScreenState> = screenStateLiveData
}