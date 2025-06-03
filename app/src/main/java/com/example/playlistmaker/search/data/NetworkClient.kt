package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResponse
}