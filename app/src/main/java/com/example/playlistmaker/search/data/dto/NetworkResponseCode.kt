package com.example.playlistmaker.search.data.dto

enum class NetworkResponseCode(private val code: Int) {
    NO_NETWORK_CONNECTION(-1),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500),
    SUCCESS(200)
}