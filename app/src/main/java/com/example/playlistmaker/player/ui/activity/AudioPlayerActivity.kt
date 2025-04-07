package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.INTENT_TRACK_KEY
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.audioPlayerScreen) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val trackId: Int = this.intent.getIntExtra(INTENT_TRACK_KEY, UNKNOWN_TRACK_ID)

        binding.toolbarAudioPlayerScreen.setNavigationOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(trackId)
        )[PlayerViewModel::class.java]

        viewModel.getPlayerStateLiveData().observe(this) { playerState ->
            if (playerState.isError) showPlayerError(playerState.trackInfo, playerState.curPosition)
            else {
                when (playerState.trackPlaybackState) {
                    PlaybackState.NOT_PREPARED -> {
                        showNotPreparedPlayer(playerState.trackInfo, playerState.curPosition)
                    }

                    PlaybackState.PREPARED -> {
                        showPreparedPlayer(playerState.trackInfo, playerState.curPosition)
                    }

                    PlaybackState.PLAYING -> {
                        showPlayingPlayer(playerState.curPosition)
                    }

                    PlaybackState.PAUSED -> {
                        showPausedPlayer(playerState.trackInfo, playerState.curPosition)
                    }
                }
            }
        }

        binding.ibtnPlayPlayer.setOnClickListener {
            viewModel.playerControl()
        }
    }

    private fun setTrackContent(trackInfo: PlayerTrackInfo) {
        val cornerRadiusDp = (this.resources.getDimension(R.dimen.corner_radius_8)).toInt()
        Glide.with(this)
            .load(trackInfo.artworkUrl)
            .centerInside()
            .transform(RoundedCorners(cornerRadiusDp))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(binding.ivCoverPlayer)

        binding.tvTrackNamePlayer.text = trackInfo.trackName
        binding.tvTrackArtistNamePlayer.text = trackInfo.artistName
        binding.tvTrackDurationPlayer.text = trackInfo.trackTime
        binding.tvTrackYearPlayer.text = trackInfo.releaseDate
        binding.tvTrackGenrePlayer.text = trackInfo.genre
        binding.tvTrackCountryPlayer.text = trackInfo.country
        binding.tvTrackCurrentTimePlayer.text = getString(R.string.track_current_time_placeholder)

        if (trackInfo.collectionName != null) {
            binding.tvTrackAlbumPlayer.text = trackInfo.collectionName
            binding.tvTrackAlbumPlayer.visibility = View.VISIBLE
            binding.tvTitleAlbumPlayer.visibility = View.VISIBLE
        } else {
            binding.tvTrackAlbumPlayer.visibility = View.GONE
            binding.tvTitleAlbumPlayer.visibility = View.GONE
        }
    }

    private fun showNotPreparedPlayer(trackInfo: PlayerTrackInfo, curPlayerPosition: String) {
        setTrackContent(trackInfo)
        binding.ibtnPlayPlayer.isEnabled = false
        binding.ibtnPlayPlayer.setImageResource(R.drawable.ic_play_84)
        binding.tvTrackCurrentTimePlayer.text = curPlayerPosition
    }

    private fun showPreparedPlayer(trackInfo: PlayerTrackInfo, curPlayerPosition: String) {
        setTrackContent(trackInfo)
        binding.ibtnPlayPlayer.isEnabled = true
        binding.ibtnPlayPlayer.setImageResource(R.drawable.ic_play_84)
        binding.tvTrackCurrentTimePlayer.text = curPlayerPosition
    }

    private fun showPlayingPlayer(curPlayerPosition: String) {
        binding.ibtnPlayPlayer.setImageResource(R.drawable.ic_pause_84)
        binding.tvTrackCurrentTimePlayer.text = curPlayerPosition
    }

    private fun showPausedPlayer(trackInfo: PlayerTrackInfo, curPlayerPosition: String) {
        setTrackContent(trackInfo)
        binding.ibtnPlayPlayer.setImageResource(R.drawable.ic_play_84)
        binding.tvTrackCurrentTimePlayer.text = curPlayerPosition
    }

    private fun showPlayerError(trackInfo: PlayerTrackInfo, curPlayerPosition: String) {
        setTrackContent(trackInfo)
        binding.ibtnPlayPlayer.setImageResource(R.drawable.ic_play_84)
        binding.tvTrackCurrentTimePlayer.text = curPlayerPosition
        Toast.makeText(
            this,
            getString(R.string.message_something_went_wrong), Toast.LENGTH_LONG
        ).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.playerPause()
    }

    private companion object {
        const val UNKNOWN_TRACK_ID = -1
    }
}