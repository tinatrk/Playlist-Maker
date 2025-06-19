package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.model.ErrorInfo
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHistoryAdapter()

        binding.clearIconSearchLine.setOnClickListener {
            viewModel.clearSearchRequest()
        }

        binding.searchLine.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onSearchLineFocusChanged(hasFocus)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchLineTextChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.searchLine.addTextChangedListener(textWatcher)


        binding.btnClearHistorySearch.setOnClickListener {
            viewModel.clearHistory()
        }

        trackAdapter = TrackAdapter {
            onTrackClicked(it)
        }
        binding.rvTrackListSearch.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvTrackListSearch.adapter = trackAdapter
        binding.rvTrackListSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (binding.searchLine.hasFocus())
                    clearFocusEditText()
            }
        })

        binding.btnErrorSearch.setOnClickListener {
            viewModel.searchTrack()
        }

        viewModel.observeScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is SearchScreenState.Default -> {
                    showDefaultState()
                }

                is SearchScreenState.EnteringRequest -> {
                    showEnteringRequest()
                }

                is SearchScreenState.Loading -> {
                    showLoading()
                }

                is SearchScreenState.Content -> {
                    showContent(screenState.tracks)
                }

                is SearchScreenState.Error -> {
                    showError(screenState.errorType)
                }

                is SearchScreenState.History -> {
                    showHistory(screenState.tracks)
                }

                is SearchScreenState.OnTrackClickedEvent -> {
                    openPlayer(screenState.track)
                }
            }
        }

        viewModel.observeOnTrackClickedLiveData().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }
    }

    private fun initHistoryAdapter() {
        historyAdapter = TrackAdapter {
            onTrackClicked(it)
        }
        binding.rvHistoryListSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvHistoryListSearch.adapter = historyAdapter
    }

    private fun showDefaultState() {
        clearFocusEditText()
        binding.searchLine.setText(STRING_DEF_VALUE)
        trackAdapter.clearTracks()

        binding.progressBarSearch.isVisible = false
        binding.clearIconSearchLine.isVisible = false
        binding.rvTrackListSearch.isVisible = false
        binding.groupErrorSearch.isVisible = false
        binding.groupHistory.isVisible = false
        binding.btnErrorSearch.isVisible = false
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateTracks(tracks)
        trackAdapter.clearTracks()

        binding.progressBarSearch.isVisible = false
        binding.clearIconSearchLine.isVisible = false
        binding.rvTrackListSearch.isVisible = false
        binding.groupErrorSearch.isVisible = false
        binding.btnErrorSearch.isVisible = false

        binding.groupHistory.isVisible = true
    }

    private fun showEnteringRequest() {
        binding.groupHistory.isVisible = false
        binding.groupErrorSearch.isVisible = false
        binding.progressBarSearch.isVisible = false
        binding.rvTrackListSearch.isVisible = false
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.isVisible = true
    }

    private fun showLoading() {
        binding.groupHistory.isVisible = false
        binding.groupErrorSearch.isVisible = false
        binding.rvTrackListSearch.isVisible = false
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.isVisible = true
        binding.progressBarSearch.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)
        binding.rvTrackListSearch.scrollToPosition(0)

        binding.groupHistory.isVisible = false
        binding.groupErrorSearch.isVisible = false
        binding.progressBarSearch.isVisible = false
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.isVisible = true
        binding.rvTrackListSearch.isVisible = true
    }

    private fun showError(errorType: ErrorType) {
        val errorInfo = getErrorInfo(errorType)
        binding.tvErrorSearch.text = errorInfo.errorMessage
        binding.ivErrorSearch.setImageResource(errorInfo.errorImageId)

        binding.groupHistory.isVisible = false
        binding.progressBarSearch.isVisible = false
        binding.rvTrackListSearch.isVisible = false

        binding.clearIconSearchLine.isVisible = true
        binding.groupErrorSearch.isVisible = true
        binding.btnErrorSearch.isVisible = errorInfo.isNeedUpdateBtn
    }

    private fun getErrorInfo(errorType: ErrorType): ErrorInfo {
        when (errorType) {
            is ErrorType.EmptyResult -> {
                return ErrorInfo(
                    requireActivity().getString(R.string.message_nothing_found),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_nothing_found_lm_120,
                        R.drawable.ic_placeholder_nothing_found_dm_120
                    ),
                    false
                )
            }

            is ErrorType.NoNetworkConnection -> {
                return ErrorInfo(
                    requireActivity().getString(R.string.message_bad_connection),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_bad_connection_lm_120,
                        R.drawable.ic_placeholder_bad_connection_dm_120
                    ),
                    true
                )
            }

            is ErrorType.BadRequest, is ErrorType.InternalServerError -> {
                return ErrorInfo(
                    requireActivity().getString(R.string.message_something_went_wrong),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_nothing_found_lm_120,
                        R.drawable.ic_placeholder_nothing_found_dm_120
                    ),
                    false
                )
            }
        }
    }

    private fun getErrorImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.onTrackClicked(track)
    }

    private fun openPlayer(track: Track) {
        clearFocusEditText()
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerActivity(
            track
        )
        findNavController().navigate(action)
    }

    private fun clearFocusEditText() {
        if (binding.searchLine.hasFocus()) {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchLine.windowToken, 0)
            binding.searchLine.clearFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateSearchResults()
        viewModel.updateHistory()
    }

    override fun onDestroyView() {
        binding.rvTrackListSearch.clearOnScrollListeners()
        binding.rvHistoryListSearch.adapter = null
        binding.rvTrackListSearch.adapter = null
        super.onDestroyView()
    }

    private companion object {
        const val STRING_DEF_VALUE = ""
    }
}