package com.example.playlistmaker.search.data.impl

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.api.ItunesApiService
import com.example.playlistmaker.search.data.dto.NetworkResponse
import com.example.playlistmaker.search.data.dto.NetworkResponseCode
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val application: Application
) : NetworkClient {

    override fun doRequest(dto: Any): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply {
                resultCode = NetworkResponseCode.NO_NETWORK_CONNECTION
            }
        }

        if (dto is TrackSearchRequest) {
            val response = try {
                itunesService.searchTracks(dto.text).execute()
            } catch (e: IOException) {
                null
            }

            if (response == null) {
                return NetworkResponse().apply {
                    resultCode = NetworkResponseCode.INTERNAL_SERVER_ERROR
                }
            }

            if (response.body() == null) return NetworkResponse().apply {
                resultCode = NetworkResponseCode.BAD_REQUEST
            }

            val body = response.body() ?: NetworkResponse()

            return body.apply { resultCode = NetworkResponseCode.SUCCESS }
        } else {
            return NetworkResponse().apply { resultCode = NetworkResponseCode.BAD_REQUEST }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }

        return false
    }
}