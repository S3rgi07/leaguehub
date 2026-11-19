package com.leaguehub.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "leagues")
data class LeagueEntity(@PrimaryKey val id: String, val ownerId: String, val name: String, val season: String)

@Entity(tableName = "clubs", foreignKeys = [ForeignKey(entity = LeagueEntity::class, parentColumns = ["id"], childColumns = ["leagueId"], onDelete = ForeignKey.RESTRICT)], indices = [Index("leagueId")])
data class ClubEntity(@PrimaryKey val id: String, val leagueId: String, val name: String)

@Entity(tableName = "players", foreignKeys = [ForeignKey(entity = ClubEntity::class, parentColumns = ["id"], childColumns = ["clubId"], onDelete = ForeignKey.RESTRICT)], indices = [Index("clubId"), Index(value = ["accountId"], unique = true)])
data class PlayerEntity(@PrimaryKey val id: String, val clubId: String, val name: String, val number: Int, val position: String, val accountId: String? = null)

@Entity(tableName = "fixtures", foreignKeys = [
    ForeignKey(entity = LeagueEntity::class, parentColumns = ["id"], childColumns = ["leagueId"], onDelete = ForeignKey.RESTRICT),
    ForeignKey(entity = ClubEntity::class, parentColumns = ["id"], childColumns = ["homeId"], onDelete = ForeignKey.RESTRICT),
    ForeignKey(entity = ClubEntity::class, parentColumns = ["id"], childColumns = ["awayId"], onDelete = ForeignKey.RESTRICT)
], indices = [Index("leagueId"), Index("homeId"), Index("awayId")])
data class FixtureEntity(@PrimaryKey val id: String, val leagueId: String, val homeId: String, val awayId: String,
    val kickoff: String, val venue: String, val matchday: Int, val homeScore: Int? = null, val awayScore: Int? = null)

@Dao
interface CompetitionDao {
    @Query("SELECT * FROM leagues WHERE ownerId = :ownerId ORDER BY name") fun observeLeagues(ownerId: String): Flow<List<LeagueEntity>>
    @Query("SELECT * FROM clubs WHERE leagueId = :leagueId ORDER BY name") fun observeClubs(leagueId: String): Flow<List<ClubEntity>>
    @Query("SELECT * FROM players WHERE clubId = :clubId ORDER BY number") fun observePlayers(clubId: String): Flow<List<PlayerEntity>>
    @Query("SELECT * FROM fixtures WHERE leagueId = :leagueId ORDER BY kickoff") fun observeFixtures(leagueId: String): Flow<List<FixtureEntity>>
    @Query("SELECT * FROM leagues WHERE id = :id") suspend fun league(id: String): LeagueEntity?
    @Query("SELECT * FROM clubs WHERE id = :id") suspend fun club(id: String): ClubEntity?
    @Query("SELECT * FROM players WHERE id = :id") suspend fun player(id: String): PlayerEntity?
    @Query("SELECT * FROM fixtures WHERE id = :id") suspend fun fixture(id: String): FixtureEntity?
    @Upsert suspend fun save(league: LeagueEntity)
    @Upsert suspend fun save(club: ClubEntity)
    @Upsert suspend fun save(player: PlayerEntity)
    @Upsert suspend fun save(fixture: FixtureEntity)
    @Delete suspend fun delete(league: LeagueEntity)
    @Delete suspend fun delete(club: ClubEntity)
    @Delete suspend fun delete(player: PlayerEntity)
    @Delete suspend fun delete(fixture: FixtureEntity)
}
