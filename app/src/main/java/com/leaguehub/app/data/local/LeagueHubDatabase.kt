package com.leaguehub.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserSessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class LeagueHubDatabase : RoomDatabase() {
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        @Volatile private var instance: LeagueHubDatabase? = null

        fun getInstance(context: Context): LeagueHubDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LeagueHubDatabase::class.java,
                    "leaguehub.db"
                ).build().also { instance = it }
            }
    }
}
