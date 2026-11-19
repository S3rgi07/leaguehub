package com.leaguehub.app.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserAccountDao {
    @Insert suspend fun insert(account: UserAccountEntity)
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserAccountEntity?
    @Query("UPDATE user_accounts SET role = :role, selectedLeagueId = :leagueId, onboardingCompleted = :completed WHERE userId = :userId")
    suspend fun updatePreferences(userId: String, role: String?, leagueId: String?, completed: Boolean)
}