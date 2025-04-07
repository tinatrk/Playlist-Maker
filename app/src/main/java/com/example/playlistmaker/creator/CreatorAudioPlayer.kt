package com.example.playlistmaker.creator

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl

object CreatorAudioPlayer {

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(getMediaPlayer())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}