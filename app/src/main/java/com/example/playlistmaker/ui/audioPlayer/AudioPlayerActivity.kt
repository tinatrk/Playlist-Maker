package com.example.playlistmaker.ui.audioPlayer

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.CreatorAudioPlayer
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.App.Companion.INTENT_TRACK_KEY

class AudioPlayerActivity : AppCompatActivity() {

    private val audioPlayerInteractor = CreatorAudioPlayer.provideAudioPlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var btnPlay: ImageButton
    private lateinit var trackCurrentTimeV: TextView

    private val setTrackCurTimeRunnable = object : Runnable {
        override fun run() {
            trackCurrentTimeV.text = audioPlayerInteractor.getCurrentPosition()
            handler.postDelayed(this, SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio_player_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.intent.getSerializableExtra(INTENT_TRACK_KEY, Track::class.java)
        else
            this.intent.getSerializableExtra(INTENT_TRACK_KEY) as Track

        val toolbar = findViewById<Toolbar>(R.id.toolbar_audio_player_screen)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val trackCoverV: ImageView = findViewById(R.id.iv_cover_player)
        val trackNameV: TextView = findViewById(R.id.tv_track_name_player)
        val trackArtistV: TextView = findViewById(R.id.tv_track_artist_name_player)
        val trackDurationV: TextView = findViewById(R.id.tv_track_duration_player)
        val trackAlbumV: TextView = findViewById(R.id.tv_track_album_player)
        val trackTitleAlbumV: TextView = findViewById(R.id.tv_title_album_player)
        val trackReleaseDataV: TextView = findViewById(R.id.tv_track_year_player)
        val trackGenreV: TextView = findViewById(R.id.tv_track_genre_player)
        val trackCountryV: TextView = findViewById(R.id.tv_track_country_player)
        trackCurrentTimeV = findViewById(R.id.tv_track_current_time_player)
        val btnAddToPlaylist: ImageButton = findViewById(R.id.ibtn_add_track_to_playlist_player)
        val btnLike: ImageButton = findViewById(R.id.ibtn_like_player)
        btnPlay = findViewById(R.id.ibtn_play_player)
        btnPlay.isEnabled = false

        val cornerRadiusDp = (this.resources.getDimension(R.dimen.corner_radius_8)).toInt()
        Glide.with(this)
            .load(track?.artworkUrl512 ?: "")
            .centerInside()
            .transform(RoundedCorners(cornerRadiusDp))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(trackCoverV)

        trackNameV.text = track?.trackName ?: getString(R.string.message_nothing_found)
        trackArtistV.text = track?.artistName ?: getString(R.string.message_nothing_found)
        trackDurationV.text = track?.trackTime ?: getString(R.string.message_nothing_found)
        trackReleaseDataV.text =
            track?.releaseDate ?: getString(R.string.message_nothing_found)
        trackGenreV.text = track?.primaryGenreName ?: getString(R.string.message_nothing_found)
        trackCountryV.text = track?.country ?: getString(R.string.message_nothing_found)
        trackCurrentTimeV.text = getString(R.string.track_current_time_placeholder)

        if (track?.collectionName != null) {
            trackAlbumV.text = track.collectionName
            trackAlbumV.visibility = View.VISIBLE
            trackTitleAlbumV.visibility = View.VISIBLE
        } else {
            trackAlbumV.visibility = View.GONE
            trackTitleAlbumV.visibility = View.GONE
        }

        if (track?.previewUrl != null) {
            audioPlayerInteractor.playerPrepare(
                track.previewUrl,
                { playerPrepareCallback() },
                { playerCompletionCallback() })
        }

        btnPlay.setOnClickListener {
            audioPlayerInteractor.playerControl(
                { playerStartCallback() },
                { playerPauseCallback() },
                { playerErrorCallback() }
            )
        }
    }

    private fun playerPrepareCallback() {
        btnPlay.isEnabled = true
    }

    private fun playerCompletionCallback() {
        handler.removeCallbacks(setTrackCurTimeRunnable)
        btnPlay.setImageResource(R.drawable.ic_play_84)
        trackCurrentTimeV.text = getString(R.string.track_current_time_placeholder)
    }

    private fun playerErrorCallback() {
        Toast.makeText(
            this,
            getString(R.string.message_something_went_wrong),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun playerStartCallback() {
        handler.post(setTrackCurTimeRunnable)
        btnPlay.setImageResource(R.drawable.ic_pause_84)
    }

    private fun playerPauseCallback() {
        handler.removeCallbacks(setTrackCurTimeRunnable)
        btnPlay.setImageResource(R.drawable.ic_play_84)
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.playerPause(
            { playerPauseCallback() },
            { playerErrorCallback() })
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.playerRelease()
    }

    private companion object {
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 500L
    }
}