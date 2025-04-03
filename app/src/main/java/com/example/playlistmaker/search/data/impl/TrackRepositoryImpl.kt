package com.example.playlistmaker.search.data.impl

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.NetworkResponseCode
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.SearchRepositoryTrackMapper
import com.example.playlistmaker.search.domain.api.repository.TrackRepository
import com.example.playlistmaker.search.domain.models.ErrorType
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TrackRepositoryImpl(
    private val networkClient: NetworkClient? = null,
    application: Application? = null
) : TrackRepository {

    private val sharedPrefs: SharedPreferences? = application?.getSharedPreferences(
        HISTORY_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE
    )

    override fun searchTracks(text: String): Resource<List<Track>> {
        if (networkClient != null) {
            val response = networkClient.doRequest(TrackSearchRequest(text))

            when (response.resultCode) {
                NetworkResponseCode.SUCCESS -> {
                    val results = (response as TrackSearchResponse).results
                    return if (results.isEmpty()) Resource.Empty()
                    else Resource.Success(results.map { SearchRepositoryTrackMapper.map(it) })
                }

                NetworkResponseCode.NO_NETWORK_CONNECTION -> {
                    return Resource.Error(errorType = ErrorType.NoNetworkConnection)
                }

                NetworkResponseCode.BAD_REQUEST -> {
                    return Resource.Error(errorType = ErrorType.BadRequest())
                }

                NetworkResponseCode.INTERNAL_SERVER_ERROR -> {
                    return Resource.Error(errorType = ErrorType.InternalServerError())
                }
            }
        }
        return Resource.Error(ErrorType.BadRequest())
    }

    override fun getHistory(): List<Track> {
        if (sharedPrefs != null) {
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
        if (sharedPrefs != null) {
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