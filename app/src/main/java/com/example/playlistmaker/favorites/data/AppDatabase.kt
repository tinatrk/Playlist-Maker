package com.example.playlistmaker.favorites.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.favorites.data.dao.TrackDao
import com.example.playlistmaker.favorites.data.entity.TrackEntity
import com.example.playlistmaker.playlists.data.dao.PlaylistDao
import com.example.playlistmaker.playlists.data.dao.TrackInPlaylistDao
import com.example.playlistmaker.playlists.data.entity.PlaylistEntity
import com.example.playlistmaker.playlists.data.entity.TrackInPlaylistEntity

@Database(
    version = 3,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}
