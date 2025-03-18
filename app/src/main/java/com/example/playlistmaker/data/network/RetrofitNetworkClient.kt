package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.example.playlistmaker.data.dto.NetworkResponse
import com.example.playlistmaker.ui.App.Companion.BAD_REQUEST_CODE
import com.example.playlistmaker.ui.App.Companion.INTERNAL_SERVER_ERROR_CODE

class RetrofitNetworkClient: NetworkClient {
    private val baseUrlItunes = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrlItunes)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is TrackSearchRequest) {
            val response = try {
                itunesService.searchTracks(dto.text).execute()
            } catch (e: IOException) {
                null
            }

            if (response == null) {
                return NetworkResponse().apply { resultCode = INTERNAL_SERVER_ERROR_CODE }
            }

            val body = response.body() ?: NetworkResponse()

            return body.apply { resultCode = response.code() }
        } else {
            return NetworkResponse().apply { resultCode = BAD_REQUEST_CODE }
        }
    }
}