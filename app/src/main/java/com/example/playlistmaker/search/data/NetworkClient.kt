package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: Any): NetworkResponse
}