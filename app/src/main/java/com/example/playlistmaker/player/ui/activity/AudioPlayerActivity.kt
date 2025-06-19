package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by lazy {
        getViewModel { parametersOf(track) }
    }

    private val args: AudioPlayerActivityArgs by navArgs()

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

        track = args.track

        binding.toolbarAudioPlayerScreen.setNavigationOnClickListener {
            finish()
        }

        binding.ibtnLikePlayer.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        //viewModel.getPlayerStateLiveData().observe(this) { playerState ->
        lifecycleScope.launch {
            viewModel.playerStateFlow.collect{ state ->
                renderState(state)
            }
        }

        binding.ibtnPlayPlayer.setOnClickListener {
            viewModel.playerControl()
        }
    }

    private fun renderState(playerState: PlayerState){
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
                    showPlayingPlayer(playerState.trackInfo, playerState.curPosition)
                }

                PlaybackState.PAUSED -> {
                    showPausedPlayer(playerState.trackInfo, playerState.curPosition)
                }
            }
        }
    }

    private fun setIsTrackFavorite(isTrackFavorite: Boolean) {
        binding.ibtnLikePlayer.isSelected = isTrackFavorite
    }

    private fun setTrackContent(trackInfo: PlayerTrackInfo) {
        if (trackInfo.trackId == UNKNOWN_ID) {
            binding.ibtnLikePlayer.isEnabled = false
            binding.ibtnAddTrackToPlaylistPlayer.isEnabled = false
        } else {
            binding.ibtnLikePlayer.isEnabled = true
            binding.ibtnAddTrackToPlaylistPlayer.isEnabled = true
        }

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

        if (trackInfo.collectionName != DEFAULT_STRING) {
            binding.tvTrackAlbumPlayer.text = trackInfo.collectionName
            binding.tvTrackAlbumPlayer.isVisible = true
            binding.tvTitleAlbumPlayer.isVisible = true
        } else {
            binding.tvTrackAlbumPlayer.isVisible = false
            binding.tvTitleAlbumPlayer.isVisible = false
        }

        setIsTrackFavorite(trackInfo.isFavorite)
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

    private fun showPlayingPlayer(trackInfo: PlayerTrackInfo, curPlayerPosition: String) {
        setTrackContent(trackInfo)
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
}