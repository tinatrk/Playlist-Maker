package com.example.playlistmaker.playlists.data.mapper

import com.example.playlistmaker.app.App.Companion.DEFAULT_INT
import com.example.playlistmaker.app.App.Companion.DEFAULT_STRING
import com.example.playlistmaker.app.App.Companion.EMPTY_STRING
import com.example.playlistmaker.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.playlists.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PlaylistDbMapper(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = convertIntsToString(playlist.tracksIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = convertStringToInts(playlist.tracksIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun mapList(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { this.map(it) }
    }

    fun convertIntsToString(list: List<Int>): String {
        return gson.toJson(list)
    }

    fun convertStringToInts(str: String): List<Int> {
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(str, type)
    }

    companion object {
        fun empty(): Playlist {
            return Playlist(
                id = DEFAULT_INT,
                title = DEFAULT_STRING,
                description = EMPTY_STRING,
                coverPath = EMPTY_STRING,
                tracksIds = listOf(),
                tracksCount = DEFAULT_INT
            )
        }
    }
}