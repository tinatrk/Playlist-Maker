package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.favorites.data.AppDatabase
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
    private val gson: Gson? = null,
    private val trackMapper: SearchRepositoryTrackMapper,
    private val appDatabase: AppDatabase
) : TrackRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {

        if (networkClient != null) {
            val response = networkClient.doRequest(TrackSearchRequest(text))

            when (response.resultCode) {
                NetworkResponseCode.SUCCESS -> {
                    val results = (response as TrackSearchResponse).results
                    if (results.isEmpty()) emit(Resource.Empty())
                    else {
                        val tracks = markFavoriteTracks(results.map { trackMapper.map(it) })
                        emit(Resource.Success(tracks))
                    }
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

    override fun getHistory(): Flow<List<Track>> = flow {
        emit(markFavoriteTracks(getHistoryFromSP()))
    }

    private fun getHistoryFromSP(): List<Track> {
        if (sharedPreferences != null && gson != null) {
            val json = sharedPreferences.getString(KEY_FOR_HISTORY_TRACK_LIST, null)
            val result: List<Track> = if (json != null) {
                val type: Type = object : TypeToken<List<Track>>() {}.type
                gson.fromJson(json, type) ?: listOf()
            } else {
                listOf()
            }
            return result
        } else return listOf()
    }

    override suspend fun updateHistory(tracks: List<Track>) {
        if (sharedPreferences != null && gson != null) {
            val json: String = gson.toJson(tracks)
            sharedPreferences.edit {
                putString(KEY_FOR_HISTORY_TRACK_LIST, json)
            }
        }
    }

    private suspend fun markFavoriteTracks(tracks: List<Track>): List<Track> {
        val favoriteTrackIds = appDatabase.trackDao().getAllTrackId()
        if (favoriteTrackIds.isEmpty())
            return tracks
        val markedTracks: MutableList<Track> = mutableListOf()
        for (i in tracks.indices) {
            markedTracks.add(tracks[i].copy(isFavorite = favoriteTrackIds.contains(tracks[i].trackId)))
        }
        return markedTracks
    }

    companion object {
        private const val KEY_FOR_HISTORY_TRACK_LIST = "history_track_list"
    }
}