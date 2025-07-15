package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.model.PlaybackState
import com.example.playlistmaker.player.presentation.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.model.PlaylistsState
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.player.ui.adapter.PlaylistAdapterVertical
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.AddingTrackToPlaylistState
import com.example.playlistmaker.playlists.ui.fragment.ModifyPlaylistFragment
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var playlistAdapter: PlaylistAdapterVertical

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

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistAdapter = PlaylistAdapterVertical { playlist ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.rvPlaylists.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylists.adapter = playlistAdapter

        track = args.track

        binding.toolbarAudioPlayerScreen.setNavigationOnClickListener {
            finish()
        }

        binding.ibtnLikePlayer.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.ibtnPlayPlayer.setOnClickListener {
            viewModel.playerControl()
        }

        binding.ibtnAddTrackToPlaylistPlayer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.btnAddTrackToPlaylistClicked()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.vOverlay.isVisible = false
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.vOverlay.isVisible = true
                binding.vOverlay.alpha = (slideOffset + 1).toFloat() / 2
            }
        })

        binding.btnModifyPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.commit {
                add(R.id.player_fragment_container, ModifyPlaylistFragment.newInstance(UNKNOWN_ID))
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }

        lifecycleScope.launch {
            viewModel.playerScreenStateFlow.collect { state ->
                renderState(state)
            }
        }

        viewModel.observeAddingTrackToPlaylistState().observe(this) { state ->
            when (state) {
                is AddingTrackToPlaylistState.SuccessAdding -> {
                    Snackbar.make(
                        binding.rvPlaylists,
                        "${getString(R.string.toast_success_adding_track_to_playlist)} " +
                                state.playlistTitle,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                is AddingTrackToPlaylistState.AlreadyExists -> Snackbar.make(
                    binding.rvPlaylists,
                    "${getString(R.string.toast_track_already_exists_in_playlist)} " +
                            state.playlistTitle,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun renderState(playerState: PlayerScreenState) {
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

            when (playerState.playlistsState) {
                is PlaylistsState.Idle -> {}
                is PlaylistsState.Loading -> {
                    showLoadingPlaylists()
                }

                is PlaylistsState.Empty -> {
                    showEmptyPlaylists()
                }

                is PlaylistsState.Content -> {
                    showPlaylists(playerState.playlistsState.playlists)
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
        Snackbar.make(
            binding.ibtnPlayPlayer,
            getString(R.string.message_something_went_wrong), Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showLoadingPlaylists() {
        binding.rvPlaylists.isVisible = false

        binding.progressBarPlaylists.isVisible = true
    }

    private fun showEmptyPlaylists() {
        binding.rvPlaylists.isVisible = false
        binding.progressBarPlaylists.isVisible = false
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistAdapter.updatePlaylists(playlists)

        binding.progressBarPlaylists.isVisible = false

        binding.rvPlaylists.isVisible = true
    }

    override fun onPause() {
        super.onPause()
        viewModel.playerPause()
    }
}