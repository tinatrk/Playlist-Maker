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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val itunesService: ItunesApiService,
    private val application: Application
) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply {
                resultCode = NetworkResponseCode.NO_NETWORK_CONNECTION
            }
        }

        if (dto !is TrackSearchRequest) {
            return NetworkResponse().apply { resultCode = NetworkResponseCode.BAD_REQUEST }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.searchTracks(dto.text)
                response.apply {
                    resultCode = NetworkResponseCode.SUCCESS
                }
            } catch (e: Throwable) {
                NetworkResponse().apply { resultCode = NetworkResponseCode.INTERNAL_SERVER_ERROR }
            }
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