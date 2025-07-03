package com.example.playlistmaker.playlists.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.example.playlistmaker.playlists.presentation.models.PlaylistsScreenState
import com.example.playlistmaker.playlists.presentation.view_model.PlaylistsViewModel
import com.example.playlistmaker.playlists.ui.fragment.adapter.PlaylistAdapterGrid
import com.example.playlistmaker.util.BindingFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var playlistAdapter: PlaylistAdapterGrid

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
        }

        playlistAdapter = PlaylistAdapterGrid { element, playlist ->
            onPlaylistLongClick(element, playlist)
        }
        binding.rvPlaylists.layoutManager =
            GridLayoutManager(requireContext(), COUNT_COLUMNS, GridLayoutManager.VERTICAL, false)
        binding.rvPlaylists.adapter = playlistAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                renderState(state)
            }
        }
    }

    private fun renderState(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Loading -> showLoading()
            is PlaylistsScreenState.Empty -> showEmpty()
            is PlaylistsScreenState.Content -> showContent(state.playlists)
        }
    }

    private fun showLoading() {
        binding.rvPlaylists.isVisible = false
        binding.groupEmpty.isVisible = false

        binding.progressBarPlaylists.isVisible = true
    }

    private fun showEmpty() {
        val emptyImageId: Int = getEmptyImageIdAccordingTheme(
            R.drawable.ic_placeholder_nothing_found_lm_120,
            R.drawable.ic_placeholder_nothing_found_dm_120
        )

        binding.ivErrorImage.setImageResource(emptyImageId)
        binding.tvErrorMessage.text = requireActivity().getString(R.string.empty_playlists_message)

        binding.progressBarPlaylists.isVisible = false
        binding.rvPlaylists.isVisible = false
        binding.groupEmpty.isVisible = true
    }

    private fun getEmptyImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistAdapter.updatePlaylists(playlists)
        binding.progressBarPlaylists.isVisible = false
        binding.groupEmpty.isVisible = false
        binding.rvPlaylists.isVisible = true
    }

    private fun onPlaylistLongClick(view: View, playlist: Playlist): Boolean {

        val popup: PopupMenu = PopupMenu(requireContext(), view)

        popup.inflate(R.menu.playlist_menu)

        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.menu_item_delete -> {
                    File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        playlist.title
                    ).delete()
                    viewModel.deletePlaylist(playlist)
                }
            }
            false
        }
        popup.show()
        return true
    }

    override fun onStart() {
        super.onStart()
        viewModel.updatePlaylists()
    }

    override fun onDestroyView() {
        binding.rvPlaylists.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
        private const val COUNT_COLUMNS = 2
    }
}