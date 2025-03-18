package com.example.playlistmaker.data.network

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.App.Companion.SUCCESS_CODE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TrackRepositoryImpl(
    private val networkClient: NetworkClient? = null,
    private val application: Application? = null
) : TrackRepository {

    private val sharedPrefs: SharedPreferences? = application?.getSharedPreferences(
        HISTORY_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    override fun searchTracks(text: String): List<Track>? {
        if (networkClient != null) {
            val response = networkClient.doRequest(TrackSearchRequest(text))
            return if (response.resultCode == SUCCESS_CODE) {
                (response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.artistName,
                        it.collectionName,
                        it.trackName,
                        it.artworkUrl100,
                        it.getCoverArtwork(),
                        it.getTrackTime(),
                        it.country,
                        it.primaryGenreName,
                        it.getTrackYear(),
                        it.previewUrl
                    )
                }
            } else {
                null
            }
        } else return null
    }

    override fun getHistory(): List<Track> {
        if (sharedPrefs != null){
            val json = sharedPrefs.getString(KEY_FOR_HISTORY_TRACK_LIST, null)
            return if (json != null) {
                val type: Type = object : TypeToken<List<Track>>() {}.type
                Gson().fromJson(json, type) ?: listOf()
            } else {
                listOf()
            }
        } else return listOf()
    }

    override fun updateHistory(tracks: List<Track>) {
        if (sharedPrefs != null){
            val json: String = Gson().toJson(tracks)
            sharedPrefs.edit()
                .putString(KEY_FOR_HISTORY_TRACK_LIST, json)
                .apply()
        }
    }

    companion object {
        private const val KEY_FOR_HISTORY_TRACK_LIST = "history_track_list"
        private const val HISTORY_SHARED_PREFERENCES_FILE = "shared_preferences_history"
    }
}