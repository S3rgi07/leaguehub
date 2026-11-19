package com.leaguehub.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserSessionEntity::class, UserAccountEntity::class, UserDataEntity::class],
    version = 2,
    exportSchema = true
)
abstract class LeagueHubDatabase : RoomDatabase() {
    abstract fun userSessionDao(): UserSessionDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS user_accounts (userId TEXT NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, passwordHash TEXT NOT NULL, passwordSalt TEXT NOT NULL, role TEXT, selectedLeagueId TEXT, onboardingCompleted INTEGER NOT NULL, PRIMARY KEY(userId))")
                db.execSQL("CREATE TABLE IF NOT EXISTS user_data (userId TEXT NOT NULL, dataKey TEXT NOT NULL, payload TEXT NOT NULL, PRIMARY KEY(userId, dataKey))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_user_accounts_email ON user_accounts (email)")
            }
        }
        @Volatile private var instance: LeagueHubDatabase? = null

        fun getInstance(context: Context): LeagueHubDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LeagueHubDatabase::class.java,
                    "leaguehub.db"
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
    }
}
