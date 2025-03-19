package com.example.playlistmaker.creator

import android.media.MediaPlayer
import com.example.playlistmaker.data.audioPlayer.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.api.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl

object CreatorAudioPlayer {

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(getMediaPlayer())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractorImpl {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}