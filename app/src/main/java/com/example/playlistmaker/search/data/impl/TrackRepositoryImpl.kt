package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Type

class TrackRepositoryImpl(
    private val networkClient: NetworkClient? = null,
    private val sharedPreferences: SharedPreferences? = null,
    private val gson: Gson? = null
) : TrackRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        if (networkClient != null) {
            val response = networkClient.doRequest(TrackSearchRequest(text))

            when (response.resultCode) {
                NetworkResponseCode.SUCCESS -> {
                    val results = (response as TrackSearchResponse).results
                    if (results.isEmpty()) emit(Resource.Empty())
                    else emit(Resource.Success(results.map { SearchRepositoryTrackMapper.map(it) }))
                }

                NetworkResponseCode.NO_NETWORK_CONNECTION -> {
                    emit(Resource.Error(errorType = ErrorType.NoNetworkConnection))
                }

                NetworkResponseCode.BAD_REQUEST -> {
                    emit(Resource.Error(errorType = ErrorType.BadRequest()))
                }

                NetworkResponseCode.INTERNAL_SERVER_ERROR -> {
                    emit(Resource.Error(errorType = ErrorType.InternalServerError()))
                }
            }
        } else {
            emit(Resource.Error(ErrorType.BadRequest()))
        }
    }

    override fun getHistory(): List<Track> {
        if (sharedPreferences != null && gson != null) {
            val json = sharedPreferences.getString(KEY_FOR_HISTORY_TRACK_LIST, null)
            return if (json != null) {
                val type: Type = object : TypeToken<List<Track>>() {}.type
                gson.fromJson(json, type) ?: listOf()
            } else {
                listOf()
            }
        } else return listOf()
    }

    override fun updateHistory(tracks: List<Track>) {
        if (sharedPreferences != null && gson != null) {
            val json: String = gson.toJson(tracks)
            sharedPreferences.edit {
                putString(KEY_FOR_HISTORY_TRACK_LIST, json)
            }
        }
    }

    companion object {
        private const val KEY_FOR_HISTORY_TRACK_LIST = "history_track_list"
    }
}