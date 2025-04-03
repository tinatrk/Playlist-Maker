package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App.Companion.INTENT_TRACK_KEY
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.example.playlistmaker.search.presentation.model.ErrorTypePresenter
import com.example.playlistmaker.search.presentation.model.SearchScreenState
import com.example.playlistmaker.search.presentation.model.SearchTrackInfo
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.model.ErrorInfo

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModels<SearchViewModel> { SearchViewModel.getViewModelFactory() }

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchScreen) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (ime.bottom != 0) {
                ime.bottom
            } else {
                systemBars.bottom
            }
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        initHistoryAdapter()

        binding.toolbarSearchScreen.setNavigationOnClickListener {
            finish()
        }

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
            this, LinearLayoutManager.VERTICAL,
            false
        )
        trackAdapter = TrackAdapter {
            openPlayer(it)
        }
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

        viewModel.getScreenStateLiveData().observe(this) { state ->
            when (state) {
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
                    showContent(state.tracks)
                }

                is SearchScreenState.Error -> {
                    showError(state.errorType)
                }

                is SearchScreenState.History -> {
                    showHistory()
                }
            }
        }

        viewModel.getHistoryStateLiveData().observe(this) { historyTracks ->
            historyAdapter.updateTracks(historyTracks)
        }
    }

    private fun initHistoryAdapter() {
        historyAdapter = TrackAdapter { openPlayer(it) }
        viewModel.getHistory()
        binding.rvHistoryListSearch.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvHistoryListSearch.adapter = historyAdapter
    }

    private fun showDefaultState() {
        clearFocusEditText()
        binding.searchLine.setText(STRING_DEF_VALUE)
        trackAdapter.clearTracks()

        binding.progressBarSearch.visibility = View.GONE
        binding.clearIconSearchLine.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.vgErrorSearch.visibility = View.GONE
        binding.vgHistorySearch.visibility = View.GONE
    }

    private fun showHistory() {
        trackAdapter.clearTracks()

        binding.progressBarSearch.visibility = View.GONE
        binding.clearIconSearchLine.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE
        binding.vgErrorSearch.visibility = View.GONE

        binding.vgHistorySearch.visibility = View.VISIBLE
    }

    private fun showEnteringRequest() {
        binding.vgHistorySearch.visibility = View.GONE
        binding.vgErrorSearch.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE

        binding.clearIconSearchLine.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.vgHistorySearch.visibility = View.GONE
        binding.vgErrorSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.progressBarSearch.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<SearchTrackInfo>) {
        trackAdapter.updateTracks(tracks)
        binding.rvTrackListSearch.scrollToPosition(0)

        binding.vgHistorySearch.visibility = View.GONE
        binding.vgErrorSearch.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.rvTrackListSearch.visibility = View.VISIBLE
    }

    private fun showError(errorType: ErrorTypePresenter) {
        val errorInfo = getErrorInfo(errorType)
        binding.tvErrorSearch.text = errorInfo.errorMessage
        binding.ivErrorSearch.setImageResource(errorInfo.errorImageId)
        binding.btnErrorSearch.isVisible = errorInfo.isNeedUpdateBtn

        binding.vgHistorySearch.visibility = View.GONE
        binding.progressBarSearch.visibility = View.GONE
        binding.rvTrackListSearch.visibility = View.GONE

        binding.clearIconSearchLine.visibility = View.VISIBLE
        binding.vgErrorSearch.visibility = View.VISIBLE
    }

    private fun getErrorInfo(errorType: ErrorTypePresenter): ErrorInfo {
        when (errorType) {
            is ErrorTypePresenter.EmptyResult -> {
                return ErrorInfo(
                    getString(R.string.message_nothing_found),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_nothing_found_lm_120,
                        R.drawable.ic_placeholder_nothing_found_dm_120
                    ),
                    false
                )
            }

            is ErrorTypePresenter.NoNetworkConnection -> {
                return ErrorInfo(
                    getString(R.string.message_bad_connection),
                    getErrorImageIdAccordingTheme(
                        R.drawable.ic_placeholder_bad_connection_dm_120,
                        R.drawable.ic_placeholder_bad_connection_dm_120
                    ),
                    true
                )
            }

            is ErrorTypePresenter.BadRequest, is ErrorTypePresenter.InternalServerError -> {
                return ErrorInfo(
                    getString(R.string.message_something_went_wrong),
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
        return when (getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun openPlayer(track: SearchTrackInfo) {
        clearFocusEditText()
        if (viewModel.onTrackClick(track.trackId)) {
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(INTENT_TRACK_KEY, track.trackId)
            startActivity(intent)
        }
    }

    private fun clearFocusEditText() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchLine.windowToken, 0)
        binding.searchLine.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvTrackListSearch.clearOnScrollListeners()
    }

    private companion object {
        const val STRING_DEF_VALUE = ""
    }

}