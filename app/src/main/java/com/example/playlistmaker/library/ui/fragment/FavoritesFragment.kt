package com.example.playlistmaker.library.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.presentation.models.FavoritesScreenState
import com.example.playlistmaker.library.presentation.view_model.FavoritesFragmentViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BindingFragment<FragmentFavoritesBinding>() {

    private val viewModel: FavoritesFragmentViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateObserve().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesScreenState.Empty -> showEmpty()
                is FavoritesScreenState.Content -> showContent()
            }
        }
    }

    private fun showEmpty() {
        val emptyImageId: Int =  getEmptyImageIdAccordingTheme(
            R.drawable.ic_placeholder_nothing_found_lm_120,
            R.drawable.ic_placeholder_nothing_found_dm_120
        )

        binding.ivErrorImage.setImageResource(emptyImageId)
        binding.tvErrorMessage.text = requireActivity().getString(R.string.empty_favorite_tracks_message)

        binding.groupEmpty.visibility = View.VISIBLE
    }

    private fun getEmptyImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun showContent(){
        binding.groupEmpty.visibility = View.GONE
    }

    companion object{
        fun newInstance() : FavoritesFragment = FavoritesFragment()
    }
}