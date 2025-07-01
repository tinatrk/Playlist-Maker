package com.example.playlistmaker.playlists.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.playlists.data.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TrackInPlaylistEntity)

    @Query("SELECT * FROM playlist_track_table")
    suspend fun getAllTracks(): List<TrackInPlaylistEntity>

    @Query("DELETE FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun deleteTrackInPlaylist(trackId: Int)
}