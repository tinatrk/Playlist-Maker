package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.UNKNOWN_ID
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.library.ui.compose.LibraryScreen
import com.example.playlistmaker.playlists.ui.fragment.ModifyPlaylistFragment
import com.example.playlistmaker.search.domain.models.Track

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    activity?.let {
                        LibraryScreen(
                            navigateToAudioPlayerScreen = ::navigateToAudioPlayerScreen,
                            navigateToModifyPlaylistScreen = ::navigateToModifyPlaylistScreen,
                            navigateToOnePlaylistScreen = ::navigateToOnePlaylistScreen,
                        )
                    }
                }
            }
        }
        return composeView
    }

    fun navigateToAudioPlayerScreen(track: Track) {
        val action = LibraryFragmentDirections.actionLibraryFragmentToAudioPlayerActivity(track)
        parentFragment?.findNavController()?.navigate(action)
    }

    fun navigateToModifyPlaylistScreen() {
        parentFragment?.findNavController()?.navigate(
            R.id.action_libraryFragment_to_modifyPlaylistFragment,
            ModifyPlaylistFragment.createArgs(UNKNOWN_ID)
        )
    }

    fun navigateToOnePlaylistScreen(playlistId: Int) {
        val action =
            LibraryFragmentDirections.actionLibraryFragmentToOnePlaylistFragment(playlistId)
        parentFragment?.findNavController()?.navigate(action)
    }
}