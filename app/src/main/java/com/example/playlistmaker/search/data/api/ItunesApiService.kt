package com.example.playlistmaker.search.data.api

import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface  ItunesApiService {
    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): TrackSearchResponse
}