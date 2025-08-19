package com.example.playlistmaker.player.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_ARTIST_NAME_KEY
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_TITLE_KEY
import com.example.playlistmaker.app.App.Companion.MEDIA_PLAYER_INTENT_TRACK_URL_KEY
import com.example.playlistmaker.app.App.Companion.NETWORK_CONNECTIVITY_CHANGED_ACTION
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.model.PlayerScreenState
import com.example.playlistmaker.player.presentation.model.PlayerState
import com.example.playlistmaker.player.presentation.model.PlayerTrackInfo
import com.example.playlistmaker.player.presentation.model.PlaylistsState
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.player.services.PlayerService
import com.example.playlistmaker.player.ui.adapter.PlaylistAdapterVertical
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.AddingTrackToPlaylistState
import com.example.playlistmaker.playlists.ui.fragment.ModifyPlaylistFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.NetworkConnectionBroadcastReceiver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
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

    private val networkConnectionBroadcastReceiver = object : NetworkConnectionBroadcastReceiver() {
        override fun showNetworkConnectionLack() {
            Snackbar.make(
                binding.root,
                getString(R.string.snackbar_no_network_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private var isPlayerServiceConnected: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.PlayerServiceBinder
            isPlayerServiceConnected = true
            viewModel.setMediaPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPlayerServiceConnected = false
            viewModel.removeMediaPlayerControl()
        }
    }

    private lateinit var serviceIntent: Intent

    private val requester = PermissionRequester.instance()
    private lateinit var permissionDialog: MaterialAlertDialogBuilder

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

        permissionDialog = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.permission_open_app_setting_title))
            .setMessage(getString(R.string.permission_notifications_message))
            .setNeutralButton(getString(R.string.permission_cancel)) { dialog, which -> }
            .setPositiveButton(getString(R.string.permission_ok)) { dialog, which ->
                openAppSettings()
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

        serviceIntent = Intent(this, PlayerService::class.java).apply {
            putExtra(MEDIA_PLAYER_INTENT_TRACK_URL_KEY, track.previewUrl)
            putExtra(MEDIA_PLAYER_INTENT_TRACK_ARTIST_NAME_KEY, track.artistName)
            putExtra(MEDIA_PLAYER_INTENT_TRACK_TITLE_KEY, track.trackName)
        }

        binding.toolbarAudioPlayerScreen.setNavigationOnClickListener {
            finish()
        }

        binding.ibtnLikePlayer.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.btnPlayPlayer.setOnClickListener {
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
                        "${getString(R.string.snackbar_success_adding_track_to_playlist)} " +
                                state.playlistTitle,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                is AddingTrackToPlaylistState.AlreadyExists -> Snackbar.make(
                    binding.rvPlaylists,
                    "${getString(R.string.snackbar_track_already_exists_in_playlist)} " +
                            state.playlistTitle,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun renderState(playerState: PlayerScreenState) {
        if (playerState.isError) showPlayerError(
            playerState.trackInfo,
            playerState.playerState
        )
        else {
            when (playerState.playerState) {
                is PlayerState.Default -> {
                    showNotPreparedPlayer(playerState.trackInfo, playerState.playerState)
                }

                is PlayerState.Prepared -> {
                    showPreparedPlayer(playerState.trackInfo, playerState.playerState)
                }

                is PlayerState.Playing -> {
                    showPlayingPlayer(playerState.trackInfo, playerState.playerState)
                }

                is PlayerState.Paused -> showPausedPlayer(
                    playerState.trackInfo,
                    playerState.playerState
                )
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

    private fun showNotPreparedPlayer(
        trackInfo: PlayerTrackInfo,
        playerState: PlayerState
    ) {
        setTrackContent(trackInfo)
        binding.btnPlayPlayer.isEnabled = playerState.isButtonEnabled
        binding.btnPlayPlayer.setPlayingState(isPlaying = playerState.isButtonPlaying)
        binding.tvTrackCurrentTimePlayer.text = playerState.progress
    }

    private fun showPreparedPlayer(
        trackInfo: PlayerTrackInfo,
        playerState: PlayerState
    ) {
        setTrackContent(trackInfo)
        binding.btnPlayPlayer.isEnabled = playerState.isButtonEnabled
        binding.btnPlayPlayer.setPlayingState(isPlaying = playerState.isButtonPlaying)
        binding.tvTrackCurrentTimePlayer.text = playerState.progress
    }

    private fun showPlayingPlayer(
        trackInfo: PlayerTrackInfo,
        playerState: PlayerState
    ) {
        setTrackContent(trackInfo)
        binding.tvTrackCurrentTimePlayer.text = playerState.progress
    }

    private fun showPausedPlayer(
        trackInfo: PlayerTrackInfo,
        playerState: PlayerState
    ) {
        setTrackContent(trackInfo)
        binding.btnPlayPlayer.setPlayingState(isPlaying = playerState.isButtonPlaying)
        binding.tvTrackCurrentTimePlayer.text = playerState.progress
    }

    private fun showPlayerError(
        trackInfo: PlayerTrackInfo,
        playerState: PlayerState
    ) {
        setTrackContent(trackInfo)
        binding.btnPlayPlayer.isEnabled = playerState.isButtonEnabled
        binding.btnPlayPlayer.setPlayingState(isPlaying = playerState.isButtonPlaying)
        binding.tvTrackCurrentTimePlayer.text = playerState.progress
        Snackbar.make(
            binding.btnPlayPlayer,
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

    private fun bindPlayerService() {
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun requestNotificationPermissionAndBindService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lifecycleScope.launch {
                requester.request(Manifest.permission.POST_NOTIFICATIONS)
                    .collect { result ->
                        when (result) {
                            is PermissionResult.Granted -> {
                                bindPlayerService()
                            }

                            is PermissionResult.Denied.DeniedPermanently -> {
                                permissionDialog.show()
                            }

                            is PermissionResult.Denied, PermissionResult.Cancelled -> {}

                        }
                    }
            }
        } else bindPlayerService()
    }

    private fun openAppSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data =
            Uri.fromParts(INTENT_SETTINGS_SCHEME, packageName, null)
        startActivity(intent)
    }

    private fun unBindPlayerService() {
        if (isPlayerServiceConnected)
            unbindService(serviceConnection)
    }

    override fun onStart() {
        super.onStart()
        requestNotificationPermissionAndBindService()
        viewModel.onComponentStart()
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            this, networkConnectionBroadcastReceiver,
            IntentFilter(NETWORK_CONNECTIVITY_CHANGED_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        ContextCompat.startForegroundService(this, serviceIntent)
        viewModel.onComponentStop()
        unBindPlayerService()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkConnectionBroadcastReceiver)
    }

    private companion object {
        const val INTENT_SETTINGS_SCHEME = "package"
    }
}