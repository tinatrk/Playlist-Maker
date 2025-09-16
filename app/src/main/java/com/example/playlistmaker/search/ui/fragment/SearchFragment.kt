package com.example.playlistmaker.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.search.ui.compose.SearchScreen
import com.example.playlistmaker.composeAppTheme.AppTheme
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.compose.koinViewModel


class SearchFragment : Fragment() {

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
                        SearchScreen(
                            viewModel = koinViewModel(),
                            navigateToAudioPlayerScreen = ::navigateToAudioPlayerScreen
                        )
                    }
                }
            }
        }
        return composeView
    }

    fun navigateToAudioPlayerScreen(track: Track) {
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerActivity(track)
        parentFragment?.findNavController()?.navigate(action)
    }
}