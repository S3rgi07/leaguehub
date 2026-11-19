package com.leaguehub.app.data.local

import androidx.room.*

@Entity(tableName = "match_events", foreignKeys = [
    ForeignKey(entity = FixtureEntity::class, parentColumns = ["id"], childColumns = ["fixtureId"], onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = PlayerEntity::class, parentColumns = ["id"], childColumns = ["playerId"], onDelete = ForeignKey.RESTRICT)
], indices = [Index("fixtureId"), Index("playerId")])
data class MatchEventEntity(
    @PrimaryKey val id: String,
    val fixtureId: String,
    val playerId: String,
    val kind: String,
    val minute: Int,
    val minutesPlayed: Int = 0,
    val rating: Double? = null
)
