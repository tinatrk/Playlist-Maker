package com.example.playlistmaker.library.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.presentation.models.PlaylistsScreenState
import com.example.playlistmaker.library.presentation.view_model.PlaylistsFragmentViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateObserve().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsScreenState.Empty -> showEmpty()
                is PlaylistsScreenState.Content -> showContent()
            }
        }
    }

    private fun showEmpty() {
        val emptyImageId: Int = getEmptyImageIdAccordingTheme(
            R.drawable.ic_placeholder_nothing_found_lm_120,
            R.drawable.ic_placeholder_nothing_found_dm_120
        )

        binding.ivErrorImage.setImageResource(emptyImageId)
        binding.tvErrorMessage.text = requireActivity().getString(R.string.empty_playlists_message)

        binding.groupEmpty.isVisible = true
    }

    private fun getEmptyImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun showContent() {
        binding.groupEmpty.isVisible = false
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}