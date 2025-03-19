package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.NetworkResponse
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.ui.App.Companion.BAD_REQUEST_CODE
import com.example.playlistmaker.ui.App.Companion.INTERNAL_SERVER_ERROR_CODE
import java.io.IOException

class RetrofitNetworkClient(private val itunesService: ItunesApiService) : NetworkClient {

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