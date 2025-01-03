package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.ImageView
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var searchLineText = STRING_DEF_VALUE
    private var searchLineHasFocus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar_search_screen)
        toolbar.setNavigationOnClickListener{
            finish()
        }

        val searchLine = findViewById<EditText>(R.id.search_line)
        searchLine.setOnFocusChangeListener { v, hasFocus -> searchLineHasFocus = hasFocus}

        val clearButton = findViewById<ImageView>(R.id.clearIcon_search_line)

        clearButton.setOnClickListener {
            searchLine.setText(STRING_DEF_VALUE)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchLine.windowToken, 0)
            searchLine.clearFocus()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchLineText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchLine.addTextChangedListener(textWatcher)

        val tracks: List<Track> = listOf(
            Track(getString(R.string.track_name_1), getString(R.string.track_artist_name_1),
                getString(R.string.track_time_1), getString(R.string.track_art_link_1)),
            Track(getString(R.string.track_name_2), getString(R.string.track_artist_name_2),
                getString(R.string.track_time_2), getString(R.string.track_art_link_2)),
            Track(getString(R.string.track_name_3), getString(R.string.track_artist_name_3),
            getString(R.string.track_time_3), getString(R.string.track_art_link_3)),
            Track(getString(R.string.track_name_4), getString(R.string.track_artist_name_4),
                getString(R.string.track_time_4), getString(R.string.track_art_link_4)),
            Track(getString(R.string.track_name_5), getString(R.string.track_artist_name_5),
                getString(R.string.track_time_5), getString(R.string.track_art_link_5))
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_track_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
            false)
        val trackAdapter: TrackAdapter = TrackAdapter(tracks)
        recyclerView.adapter = trackAdapter

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchLineText = savedInstanceState.getString(SEARCH_LINE_TEXT, STRING_DEF_VALUE)
        val searchLine = findViewById<EditText>(R.id.search_line)
        searchLine.setText(searchLineText)
        searchLineHasFocus = savedInstanceState.getBoolean(SEARCH_LINE_HAS_FOCUS, false)
        if (searchLineHasFocus){
            searchLine.setSelection(searchLine.length())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(searchLine, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE_TEXT, searchLineText)
        outState.putBoolean(SEARCH_LINE_HAS_FOCUS, searchLineHasFocus)
    }

    private companion object{
        const val SEARCH_LINE_TEXT = "SEARCH_LINE_TEXT"
        const val STRING_DEF_VALUE = ""
        const val SEARCH_LINE_HAS_FOCUS = "SEARCH_LINE_HAS_FOCUS"
    }

}