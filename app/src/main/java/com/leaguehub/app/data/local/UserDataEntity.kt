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
    @Upsert suspend fun upsert(data: UserDataEntity)
}