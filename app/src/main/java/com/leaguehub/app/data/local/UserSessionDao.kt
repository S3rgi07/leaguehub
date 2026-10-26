package com.leaguehub.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_session WHERE id = 1 LIMIT 1")
    fun observeSession(): Flow<UserSessionEntity?>

    @Upsert
    suspend fun upsert(session: UserSessionEntity)

    @Query("DELETE FROM user_session")
    suspend fun clear()
}
