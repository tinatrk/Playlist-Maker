package com.example.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_ARTIST_NAME_KEY
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_TITLE_KEY
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_URL_KEY
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerService : Service(), MediaPlayerControl {
    private val binder = PlayerServiceBinder()

    private var trackUrl: String = ""
    private var trackArtistName: String = ""
    private var trackTitle: String = ""

    private var mediaPlayer: MediaPlayer? = null

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Default())
    private val playerState = _playerState.asStateFlow()
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        trackUrl = intent?.getStringExtra(MEDIA_PLAYER_INTENT_TRACK_URL_KEY) ?: EMPTY_STRING
        trackArtistName =
            intent?.getStringExtra(MEDIA_PLAYER_INTENT_TRACK_ARTIST_NAME_KEY) ?: EMPTY_STRING
        trackTitle = intent?.getStringExtra(MEDIA_PLAYER_INTENT_TRACK_TITLE_KEY) ?: EMPTY_STRING

        initMediaPlayer()

        return binder
    }

    private fun initMediaPlayer() {
        if (trackUrl.isEmpty()) return

        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerState.Prepared()
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            stopForeground()
            _playerState.value = PlayerState.Prepared()
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Paused(getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        _playerState.value = PlayerState.Default()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
                _playerState.value =
                    PlayerState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(mediaPlayer?.currentPosition) ?: DEFAULT_CUR_POSITION_STRING
    }

    override fun getPlayerState(): StateFlow<PlayerState> {
        return playerState
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.player_notification_channel_description)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$trackArtistName $trackTitle")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    override fun startForeground() {
        if (checkPermissions().not()) {
            stopSelf()
            return
        }
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val res = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            return res == PackageManager.PERMISSION_GRANTED
        } else return true

    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private companion object {
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
        const val DEFAULT_CUR_POSITION_STRING = "00:00"
        const val NOTIFICATION_CHANNEL_ID = "player_service_channel"
        const val NOTIFICATION_CHANNEL_NAME = "player_service"
        const val SERVICE_NOTIFICATION_ID = 101
    }
}