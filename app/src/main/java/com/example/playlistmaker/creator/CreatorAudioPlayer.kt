package com.example.playlistmaker.creator

import com.example.playlistmaker.data.audioPlayer.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl

object CreatorAudioPlayer {

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractorImpl {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}