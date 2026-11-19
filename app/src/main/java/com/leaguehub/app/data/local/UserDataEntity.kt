package com.leaguehub.app.data.local
import androidx.room.Entity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Entity(tableName = "user_data", primaryKeys = ["userId", "dataKey"])
data class UserDataEntity(val userId: String, val dataKey: String, val payload: String)

@Dao
interface UserDataDao {
    @Query("SELECT payload FROM user_data WHERE userId = :userId AND dataKey = :key LIMIT 1")
    suspend fun read(userId: String, key: String): String?
    @Query("SELECT payload FROM user_data WHERE userId = :userId AND dataKey = :key LIMIT 1")
    fun observe(userId: String, key: String): kotlinx.coroutines.flow.Flow<String?>
    @Query("SELECT * FROM user_data WHERE dataKey LIKE 'match-sheet:%'")
    fun allMatchSheets(): kotlinx.coroutines.flow.Flow<List<UserDataEntity>>
    @Query("DELETE FROM user_data WHERE userId=:userId AND dataKey=:key")
    suspend fun delete(userId:String,key:String)
    @Upsert suspend fun upsert(data: UserDataEntity)
}