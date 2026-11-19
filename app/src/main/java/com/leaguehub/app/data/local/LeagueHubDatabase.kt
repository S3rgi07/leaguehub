package com.leaguehub.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserSessionEntity::class, UserAccountEntity::class, UserDataEntity::class, LeagueEntity::class, ClubEntity::class, PlayerEntity::class, FixtureEntity::class, MatchEventEntity::class],
    version = 4,
    exportSchema = true
)
abstract class LeagueHubDatabase : RoomDatabase() {
    abstract fun userSessionDao(): UserSessionDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun userDataDao(): UserDataDao
    abstract fun competitionDao(): CompetitionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS user_accounts (userId TEXT NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, passwordHash TEXT NOT NULL, passwordSalt TEXT NOT NULL, role TEXT, selectedLeagueId TEXT, onboardingCompleted INTEGER NOT NULL, PRIMARY KEY(userId))")
                db.execSQL("CREATE TABLE IF NOT EXISTS user_data (userId TEXT NOT NULL, dataKey TEXT NOT NULL, payload TEXT NOT NULL, PRIMARY KEY(userId, dataKey))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_user_accounts_email ON user_accounts (email)")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS leagues (id TEXT NOT NULL PRIMARY KEY, ownerId TEXT NOT NULL, name TEXT NOT NULL, season TEXT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS clubs (id TEXT NOT NULL PRIMARY KEY, leagueId TEXT NOT NULL, name TEXT NOT NULL, FOREIGN KEY(leagueId) REFERENCES leagues(id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_clubs_leagueId ON clubs(leagueId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS players (id TEXT NOT NULL PRIMARY KEY, clubId TEXT NOT NULL, name TEXT NOT NULL, number INTEGER NOT NULL, position TEXT NOT NULL, accountId TEXT, FOREIGN KEY(clubId) REFERENCES clubs(id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_players_clubId ON players(clubId)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_players_accountId ON players(accountId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS fixtures (id TEXT NOT NULL PRIMARY KEY, leagueId TEXT NOT NULL, homeId TEXT NOT NULL, awayId TEXT NOT NULL, kickoff TEXT NOT NULL, venue TEXT NOT NULL, matchday INTEGER NOT NULL, homeScore INTEGER, awayScore INTEGER, FOREIGN KEY(leagueId) REFERENCES leagues(id) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(homeId) REFERENCES clubs(id) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(awayId) REFERENCES clubs(id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_fixtures_leagueId ON fixtures(leagueId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_fixtures_homeId ON fixtures(homeId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_fixtures_awayId ON fixtures(awayId)")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS match_events (id TEXT NOT NULL PRIMARY KEY, fixtureId TEXT NOT NULL, playerId TEXT NOT NULL, kind TEXT NOT NULL, minute INTEGER NOT NULL, minutesPlayed INTEGER NOT NULL, rating REAL, FOREIGN KEY(fixtureId) REFERENCES fixtures(id) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(playerId) REFERENCES players(id) ON UPDATE NO ACTION ON DELETE RESTRICT)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_match_events_fixtureId ON match_events(fixtureId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_match_events_playerId ON match_events(playerId)")
            }
        }
        @Volatile private var instance: LeagueHubDatabase? = null

        fun getInstance(context: Context): LeagueHubDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LeagueHubDatabase::class.java,
                    "leaguehub.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build().also { instance = it }
            }
    }
}
