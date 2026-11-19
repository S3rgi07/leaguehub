package com.leaguehub.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName="cloud_bindings",indices=[Index(value=["uid"],unique=true)])
data class CloudBindingEntity(@PrimaryKey val userId:String,val uid:String,val canonicalId:String)
@Entity(tableName="sync_leagues")
data class SyncLeagueEntity(
 @PrimaryKey val leagueId:String,val ownerLocalId:String,
 @ColumnInfo(defaultValue="0") val revision:Long=0,
 @ColumnInfo(defaultValue="0") val localVersion:Long=0,
 @ColumnInfo(defaultValue="0") val syncedVersion:Long=0,
 @ColumnInfo(defaultValue="0") val deleted:Boolean=false,
 val basePayload:String?=null,val ownerUid:String?=null,val conflictPayload:String?=null,
 @ColumnInfo(defaultValue="0") val conflictRevision:Long=0,val lastError:String?=null
)
@Entity(tableName="sync_control")
data class SyncControlEntity(@PrimaryKey val id:Int=1,@ColumnInfo(defaultValue="0") val suppressed:Boolean=false)
@Entity(tableName="sync_recovery")
data class SyncRecoveryEntity(@PrimaryKey val id:String,val leagueId:String,val userId:String,val payload:String,val savedAt:Long)
@Dao interface SyncDao {
 @Query("SELECT * FROM cloud_bindings WHERE userId=:userId") suspend fun binding(userId:String):CloudBindingEntity?
 @Query("SELECT * FROM cloud_bindings") suspend fun bindings():List<CloudBindingEntity>
 @Query("SELECT * FROM cloud_bindings WHERE userId=:userId") fun observeBinding(userId:String):Flow<CloudBindingEntity?>
 @Query("UPDATE sync_leagues SET localVersion=localVersion+1 WHERE leagueId IN (SELECT clubs.leagueId FROM players JOIN clubs ON players.clubId=clubs.id WHERE players.accountId=:localId)")
 suspend fun mappingChanged(localId:String)
 @Upsert suspend fun save(binding:CloudBindingEntity)
 @Query("SELECT * FROM sync_leagues") fun observeAll():Flow<List<SyncLeagueEntity>>
 @Query("SELECT * FROM sync_leagues") suspend fun all():List<SyncLeagueEntity>
 @Query("SELECT * FROM sync_leagues WHERE leagueId=:id") suspend fun league(id:String):SyncLeagueEntity?
 @Upsert suspend fun save(row:SyncLeagueEntity)
 @Query("UPDATE sync_control SET suppressed=:value WHERE id=1") suspend fun suppress(value:Boolean)
 @Insert suspend fun save(recovery:SyncRecoveryEntity)
 @Query("SELECT * FROM sync_recovery WHERE userId=:userId ORDER BY savedAt DESC") suspend fun recoveries(userId:String):List<SyncRecoveryEntity>
}
