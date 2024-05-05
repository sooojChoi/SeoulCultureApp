package com.example.seoulclutureapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LikeEvent::class], version = 1, exportSchema = false)
abstract class EventDatabase:RoomDatabase(){
    abstract fun eventDao(): EventDao

    companion object{
        @Volatile
        private var Instance: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EventDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }

    }
}