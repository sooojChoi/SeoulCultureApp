package com.example.seoulclutureapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * from events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<LikeEvent>>

    @Query("SELECT * from events WHERE title = :title and date = :date")
    fun getEventByTitleAndDate(title: String, date:String): Flow<LikeEvent>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: LikeEvent)

    @Update
    suspend fun update(event: LikeEvent)

    @Delete
    suspend fun delete(event: LikeEvent)
}