package com.example.playlistmaker.library.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.presentation.models.FavoritesScreenState

class FavoritesFragmentViewModel() : ViewModel() {
    private val screenStateLiveData: MutableLiveData<FavoritesScreenState> =
        MutableLiveData(FavoritesScreenState.Empty)

    fun screenStateObserve(): LiveData<FavoritesScreenState> = screenStateLiveData
}