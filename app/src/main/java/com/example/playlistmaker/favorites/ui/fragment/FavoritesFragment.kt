package com.example.playlistmaker.favorites.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.favorites.presentation.models.FavoritesScreenState
import com.example.playlistmaker.favorites.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.fragment.LibraryFragmentDirections
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.util.BindingFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {

    private val viewModel: FavoritesViewModel by viewModel<FavoritesViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter { onTrackClicked(it) }
        binding.rvTrackList.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvTrackList.adapter = trackAdapter

        //viewModel.observeScreenState().observe(viewLifecycleOwner) { state ->
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                renderState(state)
            }
        }

        viewModel.observeOnTrackClickedLiveData().observe(viewLifecycleOwner) { track ->
            val action = LibraryFragmentDirections.actionLibraryFragmentToAudioPlayerActivity(
                track
            )
            parentFragment?.findNavController()?.navigate(action)
        }
    }

    private fun renderState(state: FavoritesScreenState){
        when (state) {
            is FavoritesScreenState.Loading -> showLoading()
            is FavoritesScreenState.Empty -> showEmpty()
            is FavoritesScreenState.Content -> showContent(state.tracks)
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.onTrackClicked(track)
    }

    private fun showLoading() {
        binding.rvTrackList.isVisible = false
        binding.groupEmpty.isVisible = false

        binding.progressBarFavorites.isVisible = true
    }

    private fun showEmpty() {
        binding.rvTrackList.isVisible = false
        binding.progressBarFavorites.isVisible = false

        val emptyImageId: Int = getEmptyImageIdAccordingTheme(
            R.drawable.ic_placeholder_nothing_found_lm_120,
            R.drawable.ic_placeholder_nothing_found_dm_120
        )

        binding.ivErrorImage.setImageResource(emptyImageId)
        binding.tvErrorMessage.text =
            requireActivity().getString(R.string.empty_favorite_tracks_message)

        binding.groupEmpty.isVisible = true
    }

    private fun getEmptyImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.groupEmpty.isVisible = false
        binding.progressBarFavorites.isVisible = false

        trackAdapter.updateTracks(tracks)
        binding.rvTrackList.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateFavoriteTracks()
    }

    override fun onDestroyView() {
        binding.rvTrackList.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}