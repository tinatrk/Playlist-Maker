package com.example.playlistmaker.playlists.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val coverPath: String,
    val tracksIds: String,
    val tracksCount: Int
)