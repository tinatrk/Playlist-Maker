package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App.Companion.INTENT_TRACK_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchLineText = STRING_DEF_VALUE
    private var searchLineHasFocus: Boolean = false
    private var isResponseDisplayed: Boolean = false

    private lateinit var trackService: TrackApi
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val tracks: MutableList<Track> = mutableListOf()

    private lateinit var searchLine: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var wgErrorSearch: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var errorBtn: Button

    private lateinit var searchHistory: SearchHistory

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

        val baseUrlITunesSearchApi = getString(R.string.base_url_itunes_search_api)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlITunesSearchApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        trackService = retrofit.create(TrackApi::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_search_screen)
        searchLine = findViewById(R.id.search_line)
        trackRecyclerView = findViewById(R.id.rv_track_list_search)
        val clearETButton = findViewById<ImageView>(R.id.clearIcon_search_line)
        wgErrorSearch = findViewById(R.id.vg_error_search)
        errorImage = findViewById(R.id.iv_error_search)
        errorMessage = findViewById(R.id.tv_error_search)
        errorBtn = findViewById(R.id.btn_error_search)

        searchHistory = SearchHistory((applicationContext as App).sharedPrefs)
        val clearHistoryBtn = findViewById<Button>(R.id.btn_clear_history_search)
        val rwHistory = findViewById<RecyclerView>(R.id.rv_history_list_search)
        val wgHistory = findViewById<LinearLayout>(R.id.vg_history_search)
        historyAdapter = TrackAdapter { openPlayer(it) }
        historyAdapter.tracks = searchHistory.getSearchHistoryTracks().toMutableList()
        rwHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rwHistory.adapter = historyAdapter


        toolbar.setNavigationOnClickListener {
            finish()
        }


        searchLine.setOnFocusChangeListener { _, hasFocus ->
            searchLineHasFocus = hasFocus
            wgHistory.visibility =
                if (hasFocus && searchLine.text.isEmpty() && historyAdapter.tracks.size != 0)
                    View.VISIBLE
                else View.GONE
        }
        searchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                clearFocusEditText()
                isResponseDisplayed = true
                true
            }
            false
        }

        clearETButton.setOnClickListener {
            searchLine.setText(STRING_DEF_VALUE)
            clearFocusEditText()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            showErrorMessage("", false)
            isResponseDisplayed = false
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearETButton.isVisible = !s.isNullOrEmpty()
                searchLineText = s.toString()
                wgHistory.visibility = if (searchLine.hasFocus() && s?.isEmpty() == true &&
                    historyAdapter.tracks.size != 0
                ) View.VISIBLE
                else View.GONE
                wgErrorSearch.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                isResponseDisplayed = false
            }
        }
        searchLine.addTextChangedListener(textWatcher)


        clearHistoryBtn.setOnClickListener {
            searchHistory.clearSearchHistory()
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


        errorBtn.setOnClickListener {
            searchTrack()
            isResponseDisplayed = true
        }
    }

    private fun showErrorMessage(message: String, isConnectionError: Boolean) {
        if (message.isNotEmpty()) {
            if (isConnectionError) {
                showErrorImage(
                    R.drawable.ic_placeholder_bad_connection_lm_120,
                    R.drawable.ic_placeholder_bad_connection_dm_120
                )
                errorMessage.text = getString(R.string.message_bad_connection)
                wgErrorSearch.visibility = View.VISIBLE
                errorBtn.visibility = View.VISIBLE
            } else {
                showErrorImage(
                    R.drawable.ic_placeholder_nothing_found_lm_120,
                    R.drawable.ic_placeholder_nothing_found_dm_120
                )
                errorMessage.text = getString(R.string.message_nothing_found)
                wgErrorSearch.visibility = View.VISIBLE
                errorBtn.visibility = View.GONE
            }
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        } else {
            wgErrorSearch.visibility = View.GONE
        }
    }

    private fun showErrorImage(imageIdLightMode: Int, imageIdDarkMode: Int) {
        when (getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> errorImage.setImageResource(imageIdDarkMode)

            Configuration.UI_MODE_NIGHT_NO -> errorImage.setImageResource(imageIdLightMode)
        }
    }

    private fun searchTrack() {
        trackService
            .searchTracks(searchLine.text.toString().trim())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.clear()
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                            showErrorMessage("", false)
                        } else {
                            showErrorMessage(getString(R.string.message_nothing_found), false)
                        }
                    } else {
                        showErrorMessage(getString(R.string.message_bad_connection), true)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showErrorMessage(getString(R.string.message_bad_connection), true)
                }
            })
    }

    private fun openPlayer(track: Track){
        saveTrackToHistory(track)
        val intent: Intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(INTENT_TRACK_KEY, track)
        startActivity(intent)
    }

    private fun saveTrackToHistory(track: Track) {
        searchHistory.saveTrack(track)
        historyAdapter.tracks = searchHistory.getSearchHistoryTracks().toMutableList()
        historyAdapter.notifyDataSetChanged()
        clearFocusEditText()
    }

    private fun clearFocusEditText() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchLine.windowToken, 0)
        searchLine.clearFocus()
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
        if (isResponseDisplayed) searchTrack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE_TEXT, searchLineText)
        outState.putBoolean(SEARCH_LINE_HAS_FOCUS, searchLineHasFocus)
        outState.putBoolean(IS_RESPONSE_DISPLAYED, isResponseDisplayed)
    }

    private companion object {
        const val SEARCH_LINE_TEXT = "SEARCH_LINE_TEXT"
        const val STRING_DEF_VALUE = ""
        const val SEARCH_LINE_HAS_FOCUS = "SEARCH_LINE_HAS_FOCUS"
        const val IS_RESPONSE_DISPLAYED = "IS_RESPONSE_DISPLAYED"
    }

}