package com.example.playlistmaker.playlists.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.databinding.FragmentOnePlaylistBinding
import com.example.playlistmaker.playlists.presentation.models.OnePlaylistDetails
import com.example.playlistmaker.playlists.presentation.models.OnePlaylistScreenState
import com.example.playlistmaker.playlists.presentation.view_model.OnePlaylistViewModel
import com.example.playlistmaker.playlists.ui.adapter.OnePlaylistTrackAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.IOException
import kotlin.properties.Delegates

class OnePlaylistFragment : BindingFragment<FragmentOnePlaylistBinding>() {

    private val args: OnePlaylistFragmentArgs by navArgs()

    private var playlistId by Delegates.notNull<Int>()

    private val viewModel: OnePlaylistViewModel by lazy {
        getViewModel { parametersOf(playlistId) }
    }

    private lateinit var deleteTrackDialog: MaterialAlertDialogBuilder

    private lateinit var deletePlaylistDialog: MaterialAlertDialogBuilder

    private lateinit var trackAdapter: OnePlaylistTrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnePlaylistBinding {
        return FragmentOnePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = args.playlistId

        deleteTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(requireActivity().getString(R.string.dialog_delete_track))
            .setNegativeButton(requireActivity().getString(R.string.dialog_btn_no)) { dialog, which -> }
            .setPositiveButton(requireActivity().getString(R.string.dialog_btn_yes)) { dialog, which -> }

        deletePlaylistDialog = MaterialAlertDialogBuilder(requireContext())
            .setNegativeButton(requireActivity().getString(R.string.dialog_btn_no)) { dialog, which -> }
            .setPositiveButton(requireActivity().getString(R.string.dialog_btn_yes)) { dialog, which ->
                viewModel.deletePlaylist()
            }

        trackAdapter = OnePlaylistTrackAdapter(
            onTackClickListener = { track -> viewModel.onTrackClicked(track) },
            onTrackLongClickListener = { track -> openDeleteTrackDialog(track) }
        )
        binding.rvTracks.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvTracks.adapter = trackAdapter

        val bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.toolbarOnePlaylistScreen.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.tvSharePlaylist.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.sharePlaylist()
        }

        binding.ivMenu.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.vOverlay.isVisible = false
                        binding.ivShare.isEnabled = true
                        binding.ivMenu.isEnabled = true
                    }

                    else -> {
                        binding.ivShare.isEnabled = false
                        binding.ivMenu.isEnabled = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.vOverlay.isVisible = true
                binding.vOverlay.alpha = (slideOffset + 1).toFloat() / 2
            }
        })

        binding.tvDeletePlaylist.setOnClickListener {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            deletePlaylistDialog.show()
        }

        binding.tvEditPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_onePlaylistFragment_to_modifyPlaylistFragment,
                ModifyPlaylistFragment.createArgs(playlistId)
            )
        }

        val bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetTracksBehavior.peekHeight = calculateBottomSheetTracksHeight()


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state -> renderScreenState(state) }
        }

        viewModel.observeOnTrackClickedLiveData().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }

        viewModel.observeToastLiveData().observe(viewLifecycleOwner) { message ->
            showToast(message)
        }

        viewModel.observeDeletingPlaylistEventLiveData().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun renderScreenState(state: OnePlaylistScreenState) {
        when (state) {
            is OnePlaylistScreenState.Loading -> showLoading()
            is OnePlaylistScreenState.Content -> showContent(state.playlistDetails)
        }
    }

    private fun showLoading() {
        binding.groupContent.isVisible = false
        binding.groupBtns.isVisible = false
        binding.rvTracks.isVisible = false

        binding.progressBarOnePlaylist.isVisible = true
    }

    private fun showContent(playlistDetails: OnePlaylistDetails) {
        setPlaylistDetails(playlistDetails)

        binding.progressBarOnePlaylist.isVisible = false

        binding.groupContent.isVisible = true
        binding.groupBtns.isVisible = true
        binding.rvTracks.isVisible = true
    }

    private fun setPlaylistDetails(playlistDetails: OnePlaylistDetails) {
        val bitmap: Bitmap? = try {
            val inputStream = File(playlistDetails.coverPath).inputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            null
        }

        Glide.with(requireActivity())
            .load(bitmap)
            .placeholder(R.drawable.ic_placeholder_45)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(binding.ivCover)

        binding.tvPlaylistTitle.text = playlistDetails.title

        if (playlistDetails.description != EMPTY_STRING) {
            binding.tvPlaylistDescription.text = playlistDetails.description
            binding.tvPlaylistDescription.isVisible = true
        } else binding.tvPlaylistDescription.isVisible = false

        binding.tvPlaylistSumDuration.text = playlistDetails.sumTracksDuration

        val trackCountString = playlistDetails.tracksCountString
        binding.tvPlaylistTracksCount.text = trackCountString

        trackAdapter.updateTracks(playlistDetails.tracks)
        binding.rvTracks.isVisible = playlistDetails.tracks.isNotEmpty()
        binding.tvEmptyTrackList.isVisible = playlistDetails.tracks.isEmpty()


        val cornerRadiusDp =
            (requireActivity().resources.getDimension(R.dimen.corner_radius_2)).toInt()
        Glide.with(requireActivity())
            .load(bitmap)
            .placeholder(R.drawable.ic_placeholder_45)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadiusDp)))
            .into(binding.ivPlaylistCoverMini)
        binding.tvPlaylistTitleMini.text = playlistDetails.title
        binding.tvPlaylistTracksCountMini.text = trackCountString

        deletePlaylistDialog.setMessage(
            requireActivity().getString(R.string.dialog_delete_playlist) +
                    playlistDetails.title +
                    requireActivity().getString(R.string.question)
        )
    }

    private fun openPlayer(track: Track) {
        val action =
            OnePlaylistFragmentDirections.actionOnePlaylistFragmentToAudioPlayerActivity(track)
        findNavController().navigate(action)
    }

    private fun openDeleteTrackDialog(track: Track): Boolean {
        deleteTrackDialog.setPositiveButton(requireActivity().getString(R.string.dialog_btn_yes)) { dialog, which ->
            viewModel.deleteTrack(track.trackId)
        }.show()
        return true
    }

    private fun showToast(toastMessage: String) {
        Snackbar.make(
            binding.ivShare,
            toastMessage,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun calculateBottomSheetTracksHeight(): Int {
        val screenSizePx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.getWindowInsets()
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            arrayOf(
                windowMetrics.bounds.width(),
                (windowMetrics.bounds.height() - insets.top - insets.bottom)
            )
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            arrayOf(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        val bottomSheetTracksIndentationPx =
            requireActivity().resources.getDimension(R.dimen.bottom_sheet_tracks_indentation)
                .toInt()

        return screenSizePx[1] - screenSizePx[0] - bottomSheetTracksIndentationPx
    }

    override fun onStart() {
        super.onStart()
        viewModel.updatePlaylistDetails()
    }

    override fun onDestroyView() {
        binding.rvTracks.adapter = null
        super.onDestroyView()
    }
}