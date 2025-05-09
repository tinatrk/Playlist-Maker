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
import com.example.playlistmaker.search.presentation.model.ErrorTypePresenter
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.model.SearchTrackInfo
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

        binding.rvTrackListSearch.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL,
            false
        )
        trackAdapter = TrackAdapter { onTrackClicked(it.trackId) }
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

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
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
                    openPlayer(screenState.trackId)
                }
            }
        }
    }

    private fun initHistoryAdapter() {
        historyAdapter = TrackAdapter { onTrackClicked(it.trackId) }
        binding.rvHistoryListSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvHistoryListSearch.adapter = historyAdapter
    }

    private fun showDefaultState() {
        clearFocusEditText()
        binding.searchLine.setText(STRING_DEF_VALUE)
        trackAdapter.clearTracks()

        binding.progressBarSearch.visibility = View.GONE
        binding.clearIconSearchLine.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.groupErrorSearch.visibility = View.GONE
        binding.groupHistory.visibility = View.GONE
        binding.btnErrorSearch.isVisible = false
    }

    private fun showHistory(tracks: List<SearchTrackInfo>) {
        historyAdapter.updateTracks(tracks)
        trackAdapter.clearTracks()

        binding.progressBarSearch.visibility = View.GONE
        binding.clearIconSearchLine.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.groupErrorSearch.visibility = View.GONE
        binding.btnErrorSearch.isVisible = false

        binding.groupHistory.visibility = View.VISIBLE
    }

    private fun showEnteringRequest() {
        binding.groupHistory.visibility = View.GONE
        binding.groupErrorSearch.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.groupHistory.visibility = View.GONE
        binding.groupErrorSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.progressBarSearch.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<SearchTrackInfo>) {
        trackAdapter.updateTracks(tracks)
        binding.rvTrackListSearch.scrollToPosition(0)

        binding.groupHistory.visibility = View.GONE
        binding.groupErrorSearch.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE
        binding.btnErrorSearch.isVisible = false

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.rvTrackListSearch.visibility = View.VISIBLE
    }

    private fun showError(errorType: ErrorTypePresenter) {
        val errorInfo = getErrorInfo(errorType)
        binding.tvErrorSearch.text = errorInfo.errorMessage
        binding.ivErrorSearch.setImageResource(errorInfo.errorImageId)

        binding.groupHistory.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.groupErrorSearch.visibility = View.VISIBLE
        binding.btnErrorSearch.isVisible = errorInfo.isNeedUpdateBtn
    }

    private fun getErrorInfo(errorType: ErrorTypePresenter): ErrorInfo {
        when (errorType) {
            is ErrorTypePresenter.EmptyResult -> {
                return ErrorInfo(
                    requireActivity().getString(R.string.message_nothing_found),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_nothing_found_lm_120,
                        R.drawable.ic_placeholder_nothing_found_dm_120
                    ),
                    false
                )
            }

            is ErrorTypePresenter.NoNetworkConnection -> {
                return ErrorInfo(
                    requireActivity().getString(R.string.message_bad_connection),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_bad_connection_lm_120,
                        R.drawable.ic_placeholder_bad_connection_dm_120
                    ),
                    true
                )
            }

            is ErrorTypePresenter.BadRequest, is ErrorTypePresenter.InternalServerError -> {
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

    private fun onTrackClicked(trackId: Int) {
        viewModel.onTrackClick(trackId)
    }

    private fun openPlayer(trackId: Int) {
        clearFocusEditText()
        viewModel.saveContentStateBeforeOpenPlayer()
        val action = SearchFragmentDirections.actionSearchFragmentToAudioPlayerActivity(trackId)
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

    override fun onDestroyView() {
        binding.rvTrackListSearch.clearOnScrollListeners()
        super.onDestroyView()
    }

    private companion object {
        const val STRING_DEF_VALUE = ""
    }
}