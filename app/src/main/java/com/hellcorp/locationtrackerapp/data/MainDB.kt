package com.hellcorp.locationtrackerapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hellcorp.locationtrackerapp.data.db.TrackDAO
import com.hellcorp.locationtrackerapp.data.db.TrackItemEntity

@Database(entities = [TrackItemEntity::class], version = 1)
abstract class MainDB : RoomDatabase() {

    abstract fun getDao(): TrackDAO

    companion object {
        @Volatile
        var instanceDB: MainDB? = null

        fun getDatabase(context: Context): MainDB {
            return instanceDB ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDB::class.java,
                    "LocationTracker.db"
                ).build()
                instanceDB = instance
                return instance
            }
        }
    }
}