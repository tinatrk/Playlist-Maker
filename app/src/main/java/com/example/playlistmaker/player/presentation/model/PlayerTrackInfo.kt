package com.example.playlistmaker.player.presentation.model

data class PlayerTrackInfo(
    val trackId: Int,
    val artistName: String,
    val collectionName: String?,
    val trackName: String,
    val artworkUrl: String,
    val trackTime: String,
    val country: String,
    val genre: String,
    val releaseDate: String,
    val previewUrl: String
) {
    companion object {
        private const val UNKNOWN_ID = -1
        private const val EMPTY_STRING = "Ничего не найдено"
        private const val EMPTY_LINK = ""

        fun empty(): PlayerTrackInfo {
            return PlayerTrackInfo(
                trackId = UNKNOWN_ID,
                artistName = EMPTY_STRING,
                collectionName = null,
                trackName = EMPTY_STRING,
                artworkUrl = EMPTY_LINK,
                trackTime = EMPTY_STRING,
                country = EMPTY_STRING,
                genre = EMPTY_STRING,
                releaseDate = EMPTY_STRING,
                previewUrl = EMPTY_LINK

            )
        }
    }
}
