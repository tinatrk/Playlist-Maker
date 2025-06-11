package com.example.playlistmaker.favorites.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.favorites.data.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorite_track_table")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getAllTrackId() : List<Int>

    @Query("SELECT * FROM favorite_track_table WHERE trackId = :id")
    fun getTrackById(id: Int) : Flow<TrackEntity?>
}