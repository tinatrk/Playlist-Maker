package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSearch
import com.example.playlistmaker.domain.api.interactor.TrackInteractorSearch
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.App.Companion.INTENT_TRACK_KEY
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    private var searchLineText = STRING_DEF_VALUE
    private var searchLineHasFocus: Boolean = false
    private var isResponseDisplayed: Boolean = false

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val tracks: MutableList<Track> = mutableListOf()

    private lateinit var searchLine: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var wgErrorSearch: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var errorBtn: Button

    private val searchRunnable = Runnable { searchTrack() }
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var progressBar: ProgressBar
    private var isOnTrackClickAllowed: Boolean = true

    private val trackInteractorSearch: TrackInteractorSearch =
        CreatorSearch.provideTrackInteractorSearch()
    private val trackInteractorHistory = CreatorHistory.provideTrackInteractorHistory()
    private lateinit var rwHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_screen)) { v, insets ->
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar_search_screen)
        searchLine = findViewById(R.id.search_line)
        trackRecyclerView = findViewById(R.id.rv_track_list_search)
        val clearETButton = findViewById<ImageView>(R.id.clearIcon_search_line)
        wgErrorSearch = findViewById(R.id.vg_error_search)
        errorImage = findViewById(R.id.iv_error_search)
        errorMessage = findViewById(R.id.tv_error_search)
        errorBtn = findViewById(R.id.btn_error_search)
        progressBar = findViewById(R.id.progress_bar_search)

        val clearHistoryBtn = findViewById<Button>(R.id.btn_clear_history_search)
        rwHistory = findViewById(R.id.rv_history_list_search)
        val wgHistory = findViewById<LinearLayout>(R.id.vg_history_search)
        initHistoryAdapter()

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearETButton.setOnClickListener {
            searchLine.setText(STRING_DEF_VALUE)
            clearFocusEditText()
            clearTrackList()
            isResponseDisplayed = false
        }

        searchLine.setOnFocusChangeListener { _, hasFocus ->
            searchLineHasFocus = hasFocus
            wgHistory.visibility =
                if (hasFocus && searchLine.text.isEmpty() && historyAdapter.tracks.size != 0)
                    View.VISIBLE
                else View.GONE
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hideTrackList()
                hideErrorMessage()
                if (!s.isNullOrEmpty()) {
                    searchDebounce()
                } else {
                    handler.removeCallbacks(searchRunnable)
                }
                clearETButton.isVisible = !s.isNullOrEmpty()
                searchLineText = s.toString()
                wgHistory.visibility = if (searchLine.hasFocus() && s?.isEmpty() == true &&
                    historyAdapter.tracks.size != 0
                ) View.VISIBLE
                else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                isResponseDisplayed = false
            }
        }
        searchLine.addTextChangedListener(textWatcher)

        clearHistoryBtn.setOnClickListener {
            trackInteractorHistory.clearHistory()
            historyAdapter.tracks.clear()
            historyAdapter.notifyDataSetChanged()
            wgHistory.visibility = View.GONE
            clearFocusEditText()
        }

        trackRecyclerView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL,
            false
        )
        trackAdapter = TrackAdapter {
            openPlayer(it)
        }
        trackAdapter.tracks = tracks
        trackRecyclerView.adapter = trackAdapter
        trackRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                clearFocusEditText()
            }
        })

        errorBtn.setOnClickListener {
            searchTrack()
            isResponseDisplayed = true
        }
    }

    private fun initHistoryAdapter() {
        historyAdapter = TrackAdapter { openPlayer(it) }
        historyAdapter.tracks = trackInteractorHistory.getHistory().toMutableList()
        rwHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rwHistory.adapter = historyAdapter
    }

    private fun showProgressBar() {
        hideTrackList()
        hideErrorMessage()
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun updateAllTrackList(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun clearTrackList() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun showTrackList() {
        trackRecyclerView.scrollToPosition(0)
        trackRecyclerView.visibility = View.VISIBLE
    }

    private fun hideTrackList() {
        trackRecyclerView.visibility = View.GONE
    }

    private fun showErrorMessage(message: String, isConnectionError: Boolean) {
        if (isConnectionError) {
            showErrorImage(
                R.drawable.ic_placeholder_bad_connection_lm_120,
                R.drawable.ic_placeholder_bad_connection_dm_120
            )
            errorBtn.visibility = View.VISIBLE
        } else {
            showErrorImage(
                R.drawable.ic_placeholder_nothing_found_lm_120,
                R.drawable.ic_placeholder_nothing_found_dm_120
            )
            errorBtn.visibility = View.GONE
        }
        errorMessage.text = message
        wgErrorSearch.visibility = View.VISIBLE
        clearTrackList()
    }

    private fun hideErrorMessage() {
        wgErrorSearch.visibility = View.GONE
    }

    private fun showErrorImage(imageIdLightMode: Int, imageIdDarkMode: Int) {
        when (getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> errorImage.setImageResource(imageIdDarkMode)

            Configuration.UI_MODE_NIGHT_NO -> errorImage.setImageResource(imageIdLightMode)
        }
    }

    private fun searchTrack() {
        showProgressBar()

        trackInteractorSearch.searchTracks(
            searchLine.text.toString().trim(),
            object : TrackInteractorSearch.TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post { showSearchResults(foundTracks) }
                }
            })
    }

    private fun showSearchResults(foundTracks: List<Track>?) {
        hideProgressBar()
        if (foundTracks != null) {
            if (foundTracks.isNotEmpty()) {
                updateAllTrackList(foundTracks)
                showTrackList()
                hideErrorMessage()
            } else {
                showErrorMessage(getString(R.string.message_nothing_found), false)
            }
        } else {
            showErrorMessage(getString(R.string.message_bad_connection), true)
        }
    }

    private fun openPlayer(track: Track) {
        if (onTrackClickDebounce()) {
            saveTrackToHistory(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(INTENT_TRACK_KEY, track)
            startActivity(intent)
        }
    }

    private fun saveTrackToHistory(track: Track) {
        trackInteractorHistory.updateHistory(track)
        historyAdapter.tracks = trackInteractorHistory.getHistory().toMutableList()
        historyAdapter.notifyDataSetChanged()
        clearFocusEditText()
    }

    private fun clearFocusEditText() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchLine.windowToken, 0)
        searchLine.clearFocus()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DELAY_MILLIS)
    }

    private fun onTrackClickDebounce(): Boolean {
        val currentState: Boolean = isOnTrackClickAllowed
        if (isOnTrackClickAllowed) {
            isOnTrackClickAllowed = false
            handler.postDelayed({ isOnTrackClickAllowed = true }, ON_TRACK_CLICK_DELAY_MILLIS)
        }
        return currentState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchLineText = savedInstanceState.getString(SEARCH_LINE_TEXT, STRING_DEF_VALUE)
        val searchLine = findViewById<EditText>(R.id.search_line)
        searchLine.setText(searchLineText)
        searchLineHasFocus = savedInstanceState.getBoolean(SEARCH_LINE_HAS_FOCUS, false)
        if (searchLineHasFocus) {
            searchLine.setSelection(searchLine.length())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(searchLine, InputMethodManager.SHOW_IMPLICIT)
        }
        isResponseDisplayed = savedInstanceState.getBoolean(IS_RESPONSE_DISPLAYED, false)
        if (isResponseDisplayed) {
            searchTrack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE_TEXT, searchLineText)
        outState.putBoolean(SEARCH_LINE_HAS_FOCUS, searchLineHasFocus)
        outState.putBoolean(IS_RESPONSE_DISPLAYED, isResponseDisplayed)
    }

    override fun onDestroy() {
        super.onDestroy()
        trackRecyclerView.clearOnScrollListeners()
        handler.removeCallbacksAndMessages(null)
    }

    private companion object {
        const val SEARCH_LINE_TEXT = "SEARCH_LINE_TEXT"
        const val STRING_DEF_VALUE = ""
        const val SEARCH_LINE_HAS_FOCUS = "SEARCH_LINE_HAS_FOCUS"
        const val IS_RESPONSE_DISPLAYED = "IS_RESPONSE_DISPLAYED"
        const val SEARCH_DELAY_MILLIS = 2000L
        const val ON_TRACK_CLICK_DELAY_MILLIS = 1000L
    }

}