package com.hellcorp.locationtrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDAO {
    @Insert
    suspend fun insertTrack(trackItemEntity: TrackItemEntity)

    @Query("DELETE FROM track_item WHERE id = :trackId")
    suspend fun removeTrack(trackId: Int)

    @Query("SELECT * FROM track_item WHERE id = :trackId")
    suspend fun getTrack(trackId: Int): TrackItemEntity

    @Query("SELECT * FROM track_item")
    fun getTrackList(): Flow<List<TrackItemEntity>>
}